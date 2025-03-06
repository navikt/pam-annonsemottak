package no.nav.pam.annonsemottak.scheduler.deactivate;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "scheduler.enabled", matchIfMissing = true)
public class DeactivateScheduledTask {

    private static final Logger LOG = LoggerFactory.getLogger(DeactivateScheduledTask.class);

    private final DeactivateService service;

    @Autowired
    public DeactivateScheduledTask(DeactivateService service){
        this.service = service;
    }

    @Scheduled(cron="0 30 1 * * *")
    @SchedulerLock(name = "deactivateExpiredActiveAds")
    public void deactivateExpiredActiveAds() {
        LOG.info("Running scheduled job for deactivating expired active ads in the database.");

        try {
            service.deactivateExpired();
        } catch (Exception e) {
            LOG.error("Exception while running deactivateExpiredActiveAds", e);
        }
    }

}
