package no.nav.pam.annonsemottak.temp.feedclient.scheduler;

import net.javacrumbs.shedlock.core.SchedulerLock;
import no.nav.pam.annonsemottak.temp.feedclient.FeedClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
@ConditionalOnProperty(name = "feed.scheduler.enabled", matchIfMissing = true)
public class FeedClientScheduledTask {

    private static final Logger LOG = LoggerFactory.getLogger(FeedClientScheduledTask.class);
    private static final String CRON = "0 */5 * * * *";

    @Value("${feed.scheduler.enabled}")
    private String schedulerEnabled;

    private final FeedClientService feedClientService;

    private boolean once = false;

    @Autowired
    public FeedClientScheduledTask(FeedClientService feedClientService) {
        this.feedClientService = feedClientService;
        LOG.info("Using cron {}", CRON);
    }

    @Scheduled(cron = CRON)
    @SchedulerLock(name = "saveLatestAdsFromFeed")
    public void saveLatestAdsFromFeed() {

        if (once) {
            return;
        }
        try {
            LOG.info("Running scheduled job for fetching and saving ads from Annonsemottak feed.");
            feedClientService.fetchAndSaveLatestAds();
        } catch (Exception e) {
            LOG.error("Scheduled task AnnonsemottakScheduledTask failed. {}", e);
        }
        once = schedulerEnabled.equalsIgnoreCase("once");

    }
}
