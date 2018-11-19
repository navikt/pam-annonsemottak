package no.nav.pam.annonsemottak.annonsemottak.solr.fetch;

import net.javacrumbs.shedlock.core.SchedulerLock;
import no.nav.pam.annonsemottak.stilling.Stilling;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("prod")
//@ConditionalOnProperty(name = "scheduler.enabled", matchIfMissing = true)
public class SolrSchedulerTask {

    private static final Logger LOG = LoggerFactory.getLogger(SolrSchedulerTask.class);
    private final SolrFetchService solrFetchService;
    private final StopSolrStillingerService stopSolrStillingerService;

    @Autowired
    SolrSchedulerTask(SolrFetchService solrFetchService,
                      StopSolrStillingerService stopSolrStillingerService) {
        this.solrFetchService = solrFetchService;
        this.stopSolrStillingerService = stopSolrStillingerService;
    }

    @Scheduled(cron = "0 0 8,12,16,20 * * *")
    @SchedulerLock(name = "saveLatestAdsFromStillingsolr")
    public void saveLatestAdsFromStillingsolr() {
        try {
            List<Stilling> allFetchedAds = solrFetchService.saveNewAndUpdatedStillingerFromSolr();

            stopSolrStillingerService.findAndStopOldSolrStillinger(allFetchedAds);

            LOG.info("Scheduler for stillingsolr ferdig");
        } catch (Exception e) {
            LOG.error("Unable to save results from stillingsolr", e);
        }
    }
}
