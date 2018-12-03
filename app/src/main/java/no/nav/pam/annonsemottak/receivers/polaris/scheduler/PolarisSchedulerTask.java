package no.nav.pam.annonsemottak.receivers.polaris.scheduler;

import net.javacrumbs.shedlock.core.SchedulerLock;
import no.nav.pam.annonsemottak.receivers.polaris.PolarisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
@Profile("prod")
//@ConditionalOnProperty(name = "scheduler.enabled", matchIfMissing = true)
public class PolarisSchedulerTask {

    public static final String CRON = "0 30 5 * * *";
    private static final Logger LOG = LoggerFactory.getLogger(PolarisSchedulerTask.class);

    private final PolarisService polarisService;

    @Inject
    public PolarisSchedulerTask(PolarisService polarisService) {
        this.polarisService = polarisService;
    }

    //@Scheduled(cron = CRON)
    @SchedulerLock(name = "saveLatestAdsFromPolaris")
    public void saveLatestAdsFromFinn() {
        LOG.info("Running scheduled job for saving the latest job ads fetched from Polaris.");

        try {
            polarisService.fetchAndSaveLatest();
            LOG.info("Scheduler for Polaris is finished");
        } catch (Exception e) {
            LOG.error("Unable to save results from Polaris", e);
        }
    }
}
