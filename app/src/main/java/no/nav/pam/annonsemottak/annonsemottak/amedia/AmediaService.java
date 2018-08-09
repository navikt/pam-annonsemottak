package no.nav.pam.annonsemottak.annonsemottak.amedia;

import com.google.common.collect.ImmutableMap;
import no.nav.pam.annonsemottak.annonsemottak.Kilde;
import no.nav.pam.annonsemottak.annonsemottak.Medium;
import no.nav.pam.annonsemottak.annonsemottak.amedia.filter.StillingFilterchain;
import no.nav.pam.annonsemottak.annonsemottak.common.rest.payloads.ResultsOnSave;
import no.nav.pam.annonsemottak.annonsemottak.externalRuns.ExternalRun;
import no.nav.pam.annonsemottak.annonsemottak.externalRuns.ExternalRunsService;
import no.nav.pam.annonsemottak.annonsemottak.fangst.AnnonseFangstService;
import no.nav.pam.annonsemottak.annonsemottak.fangst.AnnonseResult;
import no.nav.pam.annonsemottak.app.sensu.SensuClient;
import no.nav.pam.annonsemottak.stilling.Stilling;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * Amedia operasjoner
 */
@Service
public class AmediaService {

    public final static Integer MAXANTALl_TREFF = 1200;
    private static final Logger LOG = LoggerFactory.getLogger(AmediaService.class);
    private final AmediaConnector amediaConnector;
    private final AnnonseFangstService annonseFangstService;
    private final ExternalRunsService externalRunsService;


    @Inject
    public AmediaService(AmediaConnector amediaConnector,
        AnnonseFangstService annonseFangstService,
        ExternalRunsService externalRunsService) {
        this.amediaConnector = amediaConnector;
        this.annonseFangstService = annonseFangstService;
        this.externalRunsService = externalRunsService;
    }

    public ResultsOnSave saveLatestResults() {
        long start = System.currentTimeMillis();

        LOG.info("Starting amedia fetch {} ");

        ExternalRun externalRun = externalRunsService
            .findByNameAndMedium(Kilde.AMEDIA.toString(), Kilde.AMEDIA.value());

        List<String> alleStillingIDerFraAmedia = AmediaResponseMapper.mapEksternIder(
            amediaConnector.hentData(AmediaDateConverter.getInitialDate(), false, 10000));

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
     * @param start starttidspunkt for kall som henter og prosseserer et mottak.
     * @param externalRun inneholder kjøretidspunkt for en annonsemottaksjobb
     */
    private ResultsOnSave save(long start, ExternalRun externalRun,
        List<String> alleStillingIDer, List<Stilling> returnerteStillinger) {

        AnnonseResult annonseResultat = saveAnnonseresultat(alleStillingIDer, returnerteStillinger);

        saveLastRun(externalRun, returnerteStillinger);

        sendSensuEvent(alleStillingIDer, annonseResultat);

        return new ResultsOnSave(returnerteStillinger.size(), annonseResultat.getNewList().size(),
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
                LOG.info("Amedia lagrer externalrun {}", er.getLastRun());
                externalRunsService.save(er);
            });
    }

    private void sendSensuEvent(List<String> alleStillingIDer, AnnonseResult annonseResultat) {

        ImmutableMap<String, Integer> sensuFields = ImmutableMap.of(
            "total", alleStillingIDer.size(),
            //"new", annonseResult.getNewList().size(),
            "new", annonseResultat.getNewList().size(),
            "rejected",
            annonseResultat.getDuplicateList().size() + annonseResultat.getExpiredList().size(),
            "changed", annonseResultat.getModifyList().size(),
            "stopped", annonseResultat.getStopList().size());

        LOG.info("Amedia, sender sensuevent med felter: {}", sensuFields);

        SensuClient.sendEvent("amediaStillingerHentet.event", Collections.emptyMap(), sensuFields);
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
            lastRun = AmediaDateConverter.getInitialDate();
        }
        LOG.info("Amedia bruker lastrun {}", lastRun);
        return lastRun;
    }

    private List<Stilling> hentAmediaData(LocalDateTime sisteModifiserteDato) {
        return AmediaResponseMapper
            .mapResponse(amediaConnector.hentData(sisteModifiserteDato, true, MAXANTALl_TREFF));
    }


}
