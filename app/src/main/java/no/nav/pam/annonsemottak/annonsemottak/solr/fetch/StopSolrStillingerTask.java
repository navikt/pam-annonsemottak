package no.nav.pam.annonsemottak.annonsemottak.solr.fetch;

import net.javacrumbs.shedlock.core.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "scheduler.enabled", matchIfMissing = true)
public class StopSolrStillingerTask {
    private static final Logger LOG = LoggerFactory.getLogger(StopSolrStillingerTask.class);

    private final StopSolrStillingerService stopSolrStillingerService;

    @Autowired
    public StopSolrStillingerTask(StopSolrStillingerService stopSolrStillingerService) {
        this.stopSolrStillingerService = stopSolrStillingerService;
    }

    @Scheduled(cron = "0 00 2 * * *")
    @SchedulerLock(name = "stopInactiveSolrAds")
    public void stopInactiveSolrAds() {
        LOG.info("Running scheduled job for finding inactive ads in the solr index and stopping them");

        try {
            stopSolrStillingerService.findAndStopOldSolrStillinger();
            LOG.info("Finished job");
        } catch (Exception e) {
            LOG.error("Exception while running stopInactiveSolrAds", e);
        }
    }

}
