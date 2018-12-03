package no.nav.pam.annonsemottak.receivers.finn.scheduler;

import net.javacrumbs.shedlock.core.SchedulerLock;
import no.nav.pam.annonsemottak.receivers.finn.FinnService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
@Profile("prod")
@ConditionalOnProperty(name = "scheduler.enabled", matchIfMissing = true)
public class FinnSchedulerTask {

    private static final Logger LOG = LoggerFactory.getLogger(FinnSchedulerTask.class);

    private final FinnService finnService;

    @Inject
    public FinnSchedulerTask(FinnService finnService) {
        this.finnService = finnService;
    }

    @Scheduled(cron = "0 0 7 * * *")
    @SchedulerLock(name = "saveLatestAdsFromFinn")
    public void saveLatestAdsFromFinn() {
        LOG.info("Running scheduled job for saving the latest job ads fetched from Finn.");

        try {
            finnService.saveAndUpdateFromCollection(null);
        } catch (Exception e) {
            LOG.error("Unable to save results from Finn using specified collections", e);
        }
    }
}
