package no.nav.pam.annonsemottak.outbox

import no.nav.pam.annonsemottak.Application
import no.nav.pam.annonsemottak.TestcontainersConfiguration
import no.nav.pam.annonsemottak.kafka.StillingOutboxMessageProducer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.Disabled
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.kafka.KafkaContainer
import org.testcontainers.utility.DockerImageName
import java.util.*


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = [Application::class])
@Import(TestcontainersConfiguration::class)
@Rollback
@Testcontainers
@Disabled
class StillingOutboxKafkaProducerTest(@Autowired private val stillingOutboxMessageProducer: StillingOutboxMessageProducer) {
    companion object {
        @JvmStatic
        val kafkaContainer = KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka")).also { it.start() }

        @DynamicPropertySource
        @JvmStatic
        fun dynamicProperties(registry: DynamicPropertyRegistry) {
            registry.add("kafka.brokers", kafkaContainer::getBootstrapServers)
            registry.add("outbox.kafka.enabled") { "true" }
        }
    }

    @Test
    fun `StillingOutboxKafkaProducer starter og klarer å produsere melding`() {
        val recordMetadata =
            stillingOutboxMessageProducer.publishMessage(UUID.randomUUID().toString(), "hallo :)".toByteArray())
        assertEquals("hallo :)".toByteArray().size, recordMetadata.serializedValueSize())
    }
}
