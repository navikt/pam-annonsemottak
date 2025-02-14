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
import java.util.List;
import java.util.Map;

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

        // TODO: Flytte denne filtreringslogikken inn som en del av StillingFilterchain()
        List<Stilling> validerteStillinger = returnerteStillingerFraAmedia.stream().filter(this::erFelteneInnenForTillatLengde).toList();
        LOG.info("Antall stillinger som ble filtrert bort pga for lange felter: {}", returnerteStillingerFraAmedia.size() - validerteStillinger.size());

        List<Stilling> filtrert = new StillingFilterchain()
                .doFilter(validerteStillinger);

        return save(start, externalRun, alleStillingIDerFraAmedia, filtrert);
    }

    public boolean erFelteneInnenForTillatLengde(Stilling stilling) {
        List<Map.Entry<String, String>> felterSomErForlange = stilling.felterSomOverstigerGrensenPaa255Tegn();

        if(!felterSomErForlange.isEmpty()) {
            LOG.warn("Annonse med externalid '{}' mist et felt som er for langt, se neste logginnslag. Komplett stilling {}", stilling.getExternalId(), stilling);
            for(Map.Entry<String, String> felt : felterSomErForlange) {
                LOG.warn("Annonse med externalid '{}' og har feltet '{}' lengde {} verdi: '{}'",
                        stilling.getExternalId(), felt.getKey(), felt.getValue().length(), felt.getValue());
            }
        }

        return felterSomErForlange.isEmpty();
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
