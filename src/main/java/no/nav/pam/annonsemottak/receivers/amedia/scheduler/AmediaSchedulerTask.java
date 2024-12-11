package no.nav.pam.annonsemottak.receivers.amedia.scheduler;

import net.javacrumbs.shedlock.core.SchedulerLock;
import no.nav.pam.annonsemottak.receivers.amedia.AmediaService;
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
public class AmediaSchedulerTask {

    private static final String CRON = "0 0 6 * * *";
    private static final Logger LOG = LoggerFactory.getLogger(AmediaSchedulerTask.class);

    private final AmediaService amediaService;

    @Autowired
    public AmediaSchedulerTask(AmediaService amediaService) {
        this.amediaService = amediaService;
    }

    @Scheduled(cron = CRON)
    @SchedulerLock(name = "saveLatestAdsFromAmedia")
    public void saveLatestAdsFromFinn() {
        LOG.info("Running scheduled job for saving the latest job ads fetched from Amedia.");

        try {
            amediaService.saveLatestResults();
            LOG.info("Scheduler for amedia ferdig");
        } catch (Exception e) {
            LOG.error("Unable to save results from Amedia", e);
        }
    }
}
