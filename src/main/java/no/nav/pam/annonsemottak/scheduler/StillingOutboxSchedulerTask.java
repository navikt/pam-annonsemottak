package no.nav.pam.annonsemottak.scheduler;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import no.nav.pam.annonsemottak.outbox.StillingOutboxService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(value = "outbox.scheduler.enabled", havingValue = "true")
public class StillingOutboxSchedulerTask {

    private Logger LOG = LoggerFactory.getLogger(StillingOutboxSchedulerTask.class);

    private StillingOutboxService stillingOutboxService;

    @Autowired
    public StillingOutboxSchedulerTask(StillingOutboxService stillingOutboxService) {
        this.stillingOutboxService = stillingOutboxService;
    }

    @Scheduled(cron = "*/15 * * * * *")
    @SchedulerLock(name = "processStillingOutbox")
    public void prosesserStillingOutboxMeldinger() {
        try {
            LOG.info("Prosesserer StillingOutbox-meldinger");
            stillingOutboxService.prosesserAdOutboxMeldinger();
        } catch (Exception e) {
            LOG.error("Uventet feil ved utføring av jobben", e);
        }
    }
}
