package no.nav.pam.annonsemottak.scheduler

import net.javacrumbs.shedlock.core.SchedulerLock
import no.nav.pam.annonsemottak.outbox.StillingOutboxService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(value = ["outbox.scheduler.enabled"], havingValue = "true")
class StillingOutboxSchedulerTask(@Autowired private val stillingOutboxService: StillingOutboxService) {
    companion object {
        private val LOG = LoggerFactory.getLogger(StillingOutboxSchedulerTask::class.java)
    }

    @Scheduled(cron="*/15 * * * * *")
    @SchedulerLock(name = "processStillingOutbox")
    fun prosesserStillingOutboxMeldinger() {
        LOG.info("Prosesserer StillingOutbox-meldinger")
        stillingOutboxService.prosesserAdOutboxMeldinger()
    }
}
