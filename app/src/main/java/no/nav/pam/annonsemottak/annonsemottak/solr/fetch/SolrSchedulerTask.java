package no.nav.pam.annonsemottak.annonsemottak.solr.fetch;

import net.javacrumbs.shedlock.core.SchedulerLock;
import no.nav.pam.annonsemottak.annonsemottak.Kilde;
import no.nav.pam.annonsemottak.annonsemottak.externalRun.ExternalRun;
import no.nav.pam.annonsemottak.annonsemottak.externalRun.ExternalRunService;
import no.nav.pam.annonsemottak.stilling.Stilling;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Component
@Profile("prod")
@ConditionalOnProperty(name = "scheduler.enabled", matchIfMissing = true)
public class SolrSchedulerTask {

    private static final Logger LOG = LoggerFactory.getLogger(SolrSchedulerTask.class);
    private final SolrFetchService solrFetchService;
    private final ExternalRunService externalRunService;

    @Autowired
    SolrSchedulerTask(SolrFetchService solrFetchService, ExternalRunService externalRunService) {
        this.solrFetchService = solrFetchService;
        this.externalRunService = externalRunService;
    }

    @Scheduled(cron = "0 0 8 * * *")
    @SchedulerLock(name = "saveLatestAdsFromStillingsolr")
    public void saveLatestAdsFromStillingsolr() {
        try {
            ExternalRun externalRun = externalRunService.retrieveExternalRun(Kilde.STILLINGSOLR.value());
            LocalDateTime lastRun = getLastRunFromExternalRun(externalRun);

            List<Stilling> newStillinger = solrFetchService.saveNewStillingerFromSolr(lastRun);

            newStillinger.stream()
                    .max(Comparator.comparing(s ->
                            s.getProperties().get(StillingSolrBeanFieldNames.REG_DATO)))
                    .ifPresent((stilling) ->
                            saveNewLastRun(stilling.getProperties().get(StillingSolrBeanFieldNames.REG_DATO), externalRun));
            LOG.info("Scheduler for stillingsolr ferdig");
        } catch (Exception e) {
            LOG.error("Unable to save results from stillingsolr", e);
        }
    }

    private LocalDateTime getLastRunFromExternalRun(ExternalRun externalRun) {
        LocalDateTime lastRun = null;

        if (externalRun != null) {
            lastRun = externalRun.getLastRun();
        }
        if (lastRun == null) {
            lastRun = LocalDateTime.parse("2017-01-01T00:00:00Z");
        }

        return lastRun;
    }

    private void saveNewLastRun(String latestRegDato, ExternalRun externalRun) {
        LocalDateTime lastRegistered = LocalDateTime.parse(latestRegDato);

        if (externalRun == null) {
            externalRun = new ExternalRun(Kilde.STILLINGSOLR.value(), Kilde.STILLINGSOLR.value(), lastRegistered);
        } else {
            externalRun.setLastRun(lastRegistered);
        }

        externalRunService.save(externalRun);
    }
}
