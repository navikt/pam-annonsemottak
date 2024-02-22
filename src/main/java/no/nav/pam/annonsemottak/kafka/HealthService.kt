package no.nav.pam.annonsemottak.kafka

import org.springframework.stereotype.Service
import java.util.concurrent.atomic.AtomicInteger

@Service
class HealthService {
    private val unhealthyVotes = AtomicInteger(0)
    fun addUnhealthyVote() = unhealthyVotes.addAndGet(1)
    fun isHealthy() = unhealthyVotes.get() == 0
}
