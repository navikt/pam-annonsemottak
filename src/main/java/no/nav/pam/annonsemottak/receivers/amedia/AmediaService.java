package no.nav.pam.annonsemottak.receivers.amedia;

import no.nav.pam.annonsemottak.app.metrics.AnnonseMottakProbe;
import no.nav.pam.annonsemottak.receivers.Kilde;
import no.nav.pam.annonsemottak.receivers.Medium;
import no.nav.pam.annonsemottak.receivers.amedia.filter.StillingFilterchain;
import no.nav.pam.annonsemottak.receivers.common.rest.payloads.ResultsOnSave;
import no.nav.pam.annonsemottak.receivers.externalRun.ExternalRun;
import no.nav.pam.annonsemottak.receivers.externalRun.ExternalRunService;
import no.nav.pam.annonsemottak.receivers.fangst.AnnonseFangstService;
import no.nav.pam.annonsemottak.receivers.fangst.AnnonseResult;
import no.nav.pam.annonsemottak.stilling.Stilling;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

/**
 * Amedia operasjoner
 */
@Service
public class AmediaService {

    private static final int MODIFISERT_DATO_BUFFER_MINUTT = 10;
    private static final Logger LOG = LoggerFactory.getLogger(AmediaService.class);

    private final AmediaConnector amediaConnector;
    private final AnnonseFangstService annonseFangstService;
    private final ExternalRunService externalRunService;
    private final AnnonseMottakProbe probe;

    public static void logFeltlengder(Stilling s) {
        var felter = new HashMap<String, String>();
        felter.put("createdby", s.getCreatedBy());
        felter.put("updatedby", s.getUpdatedBy());
        felter.put("createdbydisplayname", s.getCreatedByDisplayName());
        felter.put("updatedbydisplayname", s.getUpdatedByDisplayName());
        felter.put("externalid", s.getExternalId());
        felter.put("place", s.getPlace());
        felter.put("title", s.getTitle());
        felter.put("duedate", s.getDueDate());
        felter.put("employer", s.getEmployerDescription());
        felter.put("hash", s.getHash());
        if (s.getMerknader().isPresent()) {
            felter.put("merknader", s.getMerknader().get().asString());
        }

        // hvis et felt er over 254 tegn, logg det
        felter.entrySet().stream()
                .filter(e -> e.getValue() != null && e.getValue().length() > 254)
                .forEach(e -> LOG.warn("Annonnse med externalid '{}' og id '{}' har feltet '{}' lengde {} verdi: '{}'",
                        s.getExternalId(), s.getId(), e.getKey(), e.getValue().length(), e.getValue()));

    }

    @Autowired
    public AmediaService(
            AmediaConnector amediaConnector,
            AnnonseFangstService annonseFangstService,
            ExternalRunService externalRunService,
            AnnonseMottakProbe probe) {
        this.amediaConnector = amediaConnector;
        this.annonseFangstService = annonseFangstService;
        this.externalRunService = externalRunService;
        this.probe = probe;
    }

    public ResultsOnSave saveLatestResults() {
        long start = System.currentTimeMillis();

        LOG.info("Starting amedia fetch");

        ExternalRun externalRun = externalRunService.findByNameAndMedium(Kilde.AMEDIA.toString(), Kilde.AMEDIA.value());

        List<String> alleStillingIDerFraAmedia = amediaConnector.fetchAllEksternId();

        List<Stilling> returnerteStillingerFraAmedia = amediaConnector.hentData(modifisertDatoMedBuffertid(getLastRun(externalRun)));

        LOG.info("Amediameldinger hentet fra api: {}", returnerteStillingerFraAmedia.size());

        for (Stilling returnertStilling : returnerteStillingerFraAmedia) {
            logFeltlengder(returnertStilling);
        }

        List<Stilling> filtrert = new StillingFilterchain()
                .doFilter(returnerteStillingerFraAmedia);

        return save(start, externalRun, alleStillingIDerFraAmedia, filtrert);
    }

    /**
     * Behandler annonsene, lagrer i database og sender statistikk TODO: Denne bør etterhvert kunne
     * gjenbrukes også av finn når den ekstra filtreringen(randomSelection) tas bort. Bør også kunne
     * brukes av andre mottak.
     *
     * @param start       starttidspunkt for kall som henter og prosseserer et mottak.
     * @param externalRun inneholder kjøretidspunkt for en annonsemottaksjobb
     */
    private ResultsOnSave save(
            long start,
            ExternalRun externalRun,

            List<String> alleStillingIDer,
            List<Stilling> returnerteStillinger) {

        AnnonseResult annonseResultat = saveAnnonseresultat(alleStillingIDer, returnerteStillinger);

        saveLastRun(externalRun, returnerteStillinger);
        probe.addMetricsCounters(Kilde.AMEDIA.toString(), "AMEDIA", annonseResultat.getNewList().size(), annonseResultat.getStopList().size(), annonseResultat.getDuplicateList().size(), annonseResultat.getModifyList().size());
        LOG.info("Saved {} new, {} changed, {} stopped ads from AMEDIA", annonseResultat.getNewList().size(), annonseResultat.getModifyList().size(), annonseResultat.getStopList().size());

        return new ResultsOnSave(
                returnerteStillinger.size(),
                annonseResultat.getNewList().size(),
                System.currentTimeMillis() - start);
    }

    private void saveLastRun(ExternalRun externalRun, List<Stilling> returnerteStillinger) {
        returnerteStillinger.stream()
                .map(Stilling::getSystemModifiedDate)
                .max(LocalDateTime::compareTo)
                .ifPresent(dateTime -> {
                    ExternalRun er = new ExternalRun(
                            externalRun != null ? externalRun.getId() : null,
                            Kilde.AMEDIA.toString(),
                            Medium.AMEDIA.toString(),
                            dateTime);
                    externalRunService.save(er);
                });
    }

    private AnnonseResult saveAnnonseresultat(List<String> alleStillingIDerFraAmedia,
                                              List<Stilling> returnerteStillingerFraAmedia) {
        AnnonseResult annonseResult = annonseFangstService
                .retrieveAnnonseLists(returnerteStillingerFraAmedia, alleStillingIDerFraAmedia,
                        Kilde.AMEDIA.toString(), Medium.AMEDIA.toString());
        annonseFangstService.saveAll(annonseResult);

        return annonseResult;
    }

    private LocalDateTime getLastRun(ExternalRun externalRun) {
        LocalDateTime lastRun;
        if (externalRun != null && externalRun.getLastRun() != null) {
            lastRun = externalRun.getLastRun();
        } else {
            LOG.info("First time fetching ads from AMEDIA");
            lastRun = AmediaDateConverter.getInitialDate();
        }
        LOG.info("Amedia bruker lastrun {}", lastRun);
        return lastRun;
    }

    private LocalDateTime modifisertDatoMedBuffertid(LocalDateTime sisteModifiserteDato) {
        if (sisteModifiserteDato == null) {
            return AmediaDateConverter.getInitialDate();
        }

        return sisteModifiserteDato.minusMinutes(MODIFISERT_DATO_BUFFER_MINUTT);
    }


}
