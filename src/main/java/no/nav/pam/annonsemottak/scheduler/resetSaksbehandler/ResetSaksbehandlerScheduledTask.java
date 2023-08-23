package no.nav.pam.annonsemottak.scheduler.resetSaksbehandler;


import net.javacrumbs.shedlock.core.SchedulerLock;
import no.nav.pam.annonsemottak.scheduler.deactivate.DeactivateScheduledTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import jakarta.inject.Inject;

@Component
@ConditionalOnProperty(name = "scheduler.enabled", matchIfMissing = true)
public class ResetSaksbehandlerScheduledTask {

    private static final Logger LOG = LoggerFactory.getLogger(DeactivateScheduledTask.class);

    private final ResetSaksbehandlerService service;

    @Inject
    public ResetSaksbehandlerScheduledTask(ResetSaksbehandlerService service){
        this.service = service;
    }

    @Scheduled(cron="0 0 1 * * *")
    @SchedulerLock(name = "resetSaksbehandler")
    public void resetSaksbehandlerStatus() {
        LOG.info("Running scheduled job for resetting ad statuses in the database.");

        try {
            service.resetSaksbehandler();
        } catch (Exception e) {
            LOG.error("Exception while running resetting ad statuses", e);
        }
    }
}
