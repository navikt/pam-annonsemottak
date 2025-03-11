package no.nav.pam.annonsemottak.kafka

import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.header.internals.RecordHeader
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service

@Service
@ConditionalOnProperty(value = ["outbox.kafka.enabled"], havingValue = "true")
open class StillingOutboxMessageProducer(
    kafkaConfig: KafkaConfig,
    @Value("\${outbox.kafka.topic}") private val topic: String,
    private val healthService: HealthService
) : MessageProducer() {
    private val kafkaProducer = kafkaConfig.kafkaProducer()
    private val headers = listOf(RecordHeader("@meldingstype", Meldingstype.ANNONSEMOTTAK.name.toByteArray()))

    override fun unhealthy() = healthService.addUnhealthyVote()

    override fun publishMessage(uuid: String, payload: ByteArray): RecordMetadata =
        kafkaProducer.send(ProducerRecord(topic, null, uuid, payload, headers)).get()

    enum class Meldingstype {
        IMPORT_API, ANNONSEMOTTAK
    }
}

@Service
@ConditionalOnProperty(value = ["outbox.kafka.enabled"], havingValue = "false", matchIfMissing = true)
open class MockOutboxMessageProducer : MessageProducer() {
    override fun unhealthy() = 1
    override fun publishMessage(uuid: String, payload: ByteArray): RecordMetadata =
        RecordMetadata(TopicPartition("empty", 1), 0, 0, 0, 0, 0)
}
