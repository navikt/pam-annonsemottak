package no.nav.pam.annonsemottak.scheduler

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import no.nav.pam.annonsemottak.outbox.StillingOutboxService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

interface StillingOutboxSchedulerTask {
    fun prosesserStillingOutboxMeldinger()
}

@Component
@ConditionalOnProperty(value = ["outbox.scheduler.enabled"], havingValue = "true")
open class DefaultStillingOutboxSchedulerTask @Autowired constructor(
    private val stillingOutboxService: StillingOutboxService
) : StillingOutboxSchedulerTask {
    companion object {
        private val LOG = LoggerFactory.getLogger(DefaultStillingOutboxSchedulerTask::class.java)
    }

    @Scheduled(cron = "*/15 * * * * *")
    @SchedulerLock(name = "processStillingOutbox")
    override fun prosesserStillingOutboxMeldinger() {
        try {
            LOG.info("Prosesserer StillingOutbox-meldinger")
            stillingOutboxService.prosesserAdOutboxMeldinger()

        } catch (e: Exception) {
            LOG.error("Uventet feil ved utføring av jobben", e)
        }
    }
}
