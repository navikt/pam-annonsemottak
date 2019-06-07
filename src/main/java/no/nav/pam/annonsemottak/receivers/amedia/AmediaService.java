package no.nav.pam.annonsemottak.receivers.amedia;

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
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Amedia operasjoner
 */
@Service
public class AmediaService {
    private static final LocalDateTime FIRST_FETCH_DATE = LocalDateTime.of(1900, 1, 1, 0, 0, 0);
    public final static Integer MAXANTALl_TREFF = 1200;
    private static final Logger LOG = LoggerFactory.getLogger(AmediaService.class);

    private final AmediaConnector amediaConnector;
    private final AnnonseFangstService annonseFangstService;
    private final ExternalRunService externalRunService;


    @Inject
    public AmediaService(AmediaConnector amediaConnector,
                         AnnonseFangstService annonseFangstService,
                         ExternalRunService externalRunService) {
        this.amediaConnector = amediaConnector;
        this.annonseFangstService = annonseFangstService;
        this.externalRunService = externalRunService;
    }

    public ResultsOnSave saveLatestResults() {
        long start = System.currentTimeMillis();

        LOG.info("Starting amedia fetch");

        ExternalRun externalRun = externalRunService.findByNameAndMedium(Kilde.AMEDIA.toString(), Kilde.AMEDIA.value());

        List<String> alleStillingIDerFraAmedia = AmediaResponseMapper.mapEksternIder(
                amediaConnector.hentData(AmediaRequestParametere.DAWN_OF_TIME));

        List<Stilling> returnerteStillingerFraAmedia = hentAmediaData(getLastRun(externalRun));
        LOG.info("Amediameldinger hentet fra api: {}", returnerteStillingerFraAmedia.size());

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
    private ResultsOnSave save(long start, ExternalRun externalRun,
                               List<String> alleStillingIDer, List<Stilling> returnerteStillinger) {

        AnnonseResult annonseResultat = saveAnnonseresultat(alleStillingIDer, returnerteStillinger);

        saveLastRun(externalRun, returnerteStillinger);
        annonseFangstService.addMetricsCounters(Kilde.AMEDIA, "AMEDIA",
                annonseResultat.getNewList().size(), annonseResultat.getStopList().size(), annonseResultat.getDuplicateList().size(),
                annonseResultat.getModifyList().size());

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
        annonseFangstService.handleDuplicates(annonseResult);
        annonseFangstService.saveAll(annonseResult);

        return annonseResult;
    }

    private LocalDateTime getLastRun(ExternalRun externalRun) {
        LocalDateTime lastRun;
        if (externalRun != null && externalRun.getLastRun() != null) {
            lastRun = externalRun.getLastRun();
        } else {
            LOG.info("First time fetching ads from AMEDIA");
            lastRun = FIRST_FETCH_DATE;
        }
        LOG.info("Amedia bruker lastrun {}", lastRun);
        return lastRun;
    }

    private List<Stilling> hentAmediaData(LocalDateTime sisteModifiserteDato) {

        AmediaRequestParametere requestParametere = new AmediaRequestParametere(sisteModifiserteDato, true, MAXANTALl_TREFF);
        return AmediaResponseMapper
                .mapResponse(amediaConnector.hentData(requestParametere));
    }


}
