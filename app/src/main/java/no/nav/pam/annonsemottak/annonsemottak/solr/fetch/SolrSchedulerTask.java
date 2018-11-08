package no.nav.pam.annonsemottak.annonsemottak.solr.fetch;

import net.javacrumbs.shedlock.core.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Profile("prod")
@ConditionalOnProperty(name = "scheduler.enabled", matchIfMissing = true)
public class SolrSchedulerTask {

    private static final Logger LOG = LoggerFactory.getLogger(SolrSchedulerTask.class);
    private final SolrFetchService solrFetchService;

    @Autowired
    SolrSchedulerTask(SolrFetchService solrFetchService) {
        this.solrFetchService = solrFetchService;
    }

    @Scheduled(cron = "0 0 8 * * *")
    @SchedulerLock(name = "saveLatestAdsFromStillingsolr")
    public void saveLatestAdsFromStillingsolr() {
        try {
            solrFetchService.saveStillingerFromSolr();

            LOG.info("Scheduler for stillingsolr ferdig");
        } catch (Exception e) {
            LOG.error("Unable to save results from stillingsolr", e);
        }
    }
}
