package no.nav.pam.annonsemottak.annonsemottak.dexi.scheduler;

import net.javacrumbs.shedlock.core.SchedulerLock;
import no.nav.pam.annonsemottak.annonsemottak.dexi.DexiException;
import no.nav.pam.annonsemottak.annonsemottak.dexi.DexiService;
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
public class DexiScheduledTask {

    private static final Logger LOG = LoggerFactory.getLogger(DexiScheduledTask.class);

    private final DexiService dexiService;

    @Inject
    public DexiScheduledTask(DexiService dexiService){
        this.dexiService = dexiService;
    }

    @Scheduled(cron="0 0 5 * * *")
    @SchedulerLock(name = "saveLatestResultsFromAllDexiJobs")
    public void saveLatestResultsFromAllDexiJobs() {
        LOG.info("Running scheduled job for saving resuls from all Dexi-jobs.");

        try {
            dexiService.saveLatestResultsFromAllJobs();
        } catch (DexiException e) {
            LOG.error("Unable to save latest results for all robots, latest attempted robot was '{}'", e.getCurrentRobotName(), e);
        }
    }

}
