package no.nav.pam.annonsemottak.outbox

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import no.nav.pam.annonsemottak.kafka.MessageProducer
import no.nav.pam.annonsemottak.markdown.MarkdownToHtmlConverter
import no.nav.pam.annonsemottak.stilling.Stilling
import org.apache.kafka.common.KafkaException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
open class StillingOutboxService(
    private val stillingOutboxMessageProducer: MessageProducer,
    private val stillingOutboxRepository: StillingOutboxRepository,
) {
    companion object {
        private val LOG = LoggerFactory.getLogger(StillingOutboxService::class.java)
        private val jacksonMapper = jacksonObjectMapper()
            .registerModule(JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .setTimeZone(TimeZone.getTimeZone("Europe/Oslo"))
    }

    private fun Stilling.tilPayload() = jacksonMapper.writeValueAsString(this.let {
        jobDescription = MarkdownToHtmlConverter.parse(jobDescription)
        employerDescription = MarkdownToHtmlConverter.parse(employerDescription)
    })

    fun lagreTilOutbox(stilling: Stilling) = stillingOutboxRepository.lagre(StillingOutbox(uuid = stilling.uuid, payload = stilling.tilPayload()))

    fun lagreFlereTilOutbox(stillinger: Iterable<Stilling>) = stillinger
        .map { StillingOutbox(uuid = it.uuid, payload = it.tilPayload()) }
        .let { stillingOutboxRepository.lagreFlere(it) }

    fun markerSomProsesert(stillingOutbox: StillingOutbox): StillingOutbox {
        val newValue = stillingOutbox.copy(harFeilet = false, prosessertDato = LocalDateTime.now())
        stillingOutboxRepository.markerSomProsessert(newValue)
        return newValue
    }

    fun markerSomFeilet(stillingOutbox: StillingOutbox): StillingOutbox {
        val newValue = stillingOutbox.copy(harFeilet = true, antallForsøk = stillingOutbox.antallForsøk + 1, sisteForsøkDato = LocalDateTime.now())
        stillingOutboxRepository.markerSomFeilet(newValue)
        return newValue
    }

    fun hentUprosesserteMeldinger(batchSize: Int = 1000, outboxDelay: Long = 30): List<StillingOutbox> =
        stillingOutboxRepository.hentUprosesserteMeldinger(batchSize, outboxDelay)

    fun prosesserAdOutboxMeldinger() {
        val uprossesserteMeldinger = hentUprosesserteMeldinger()
        var feilede = 0

        LOG.info("Publiserer ${uprossesserteMeldinger.size} stillingOutbox-meldinger")

        uprossesserteMeldinger.forEach { stillingOutbox ->
            try {
                stillingOutboxMessageProducer.publishMessage(stillingOutbox.uuid, stillingOutbox.payload.toByteArray())
                markerSomProsesert(stillingOutbox)
            } catch (e: KafkaException) {
                LOG.warn("Feil ved prosessering av stillingOutbox med id ${stillingOutbox.id} - Stilling ${stillingOutbox.uuid} - $e")
                if (markerSomFeilet(stillingOutbox).antallForsøk > 5) stillingOutboxMessageProducer.unhealthy()
                feilede++
            } catch (e: Exception) {
                LOG.error("Uventet feil ved prosessering av stillingOutbox med id ${stillingOutbox.id} - Stilling ${stillingOutbox.uuid} - $e")
                if (markerSomFeilet(stillingOutbox).antallForsøk > 5) stillingOutboxMessageProducer.unhealthy()
                feilede++
            }
        }

        LOG.info("Ferdig med å publiseere ${uprossesserteMeldinger.size} - Suksess: ${uprossesserteMeldinger.size - feilede} - Feilet: $feilede")
    }
}
