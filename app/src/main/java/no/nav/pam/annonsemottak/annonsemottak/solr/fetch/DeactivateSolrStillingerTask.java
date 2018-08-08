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
public class DeactivateSolrStillingerTask {
    private static final Logger LOG = LoggerFactory.getLogger(DeactivateSolrStillingerTask.class);

    private final DeactivateSolrStillingerService deactivateSolrStillingerService;

    @Autowired
    public DeactivateSolrStillingerTask(DeactivateSolrStillingerService deactivateSolrStillingerService) {
        this.deactivateSolrStillingerService = deactivateSolrStillingerService;
    }

    @Scheduled(cron = "0 00 2 * * *")
    @SchedulerLock(name = "deactivateInactiveSolrAds")
    public void deactivateInactiveSolrAds() {
        LOG.info("Running scheduled job for finding inactive ads in the solr index and deactivating them");

        try {
            deactivateSolrStillingerService.findAndDeactivateOldSolrStillinger();
            LOG.info("Finished job");
        } catch (Exception e) {
            LOG.error("Exception while running deactivateInactiveSolrAds", e);
        }
    }

}
