package no.nav.pam.annonsemottak.outbox

import no.nav.pam.annonsemottak.Application
import no.nav.pam.annonsemottak.stilling.StillingTestdataBuilder
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.annotation.Transactional

@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [Application::class])
@Transactional
@Rollback
@ContextConfiguration(classes = [Application::class])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
open class StillingOutboxServiceTest(
    @Autowired private val stillingOutboxService: StillingOutboxService,
    @Autowired private val stillingOutboxRepository: StillingOutboxRepository,
) {
    @Test
    fun `StillingOutboxService prosesserer og markerer outboxelementer riktig`() {
        val stilling = StillingTestdataBuilder.stilling().build()

        stillingOutboxService.lagreTilOutbox(stilling)

        val initiellAdOutbox = stillingOutboxRepository.hentAlle().also { assertEquals(1, it.size) }.first()
        assertNull(initiellAdOutbox.prosessertDato)
        assertNull(initiellAdOutbox.sisteForsøkDato)
        assertFalse(initiellAdOutbox.harFeilet)

        val initiellUprosessert = stillingOutboxRepository.hentUprosesserteMeldinger(outboxDelay = 0).also { assertEquals(1, it.size) }.first()
        assertEquals(initiellAdOutbox, initiellUprosessert)

        stillingOutboxService.markerSomFeilet(initiellAdOutbox)

        val feiletAdOutbox = stillingOutboxRepository.hentAlle().also { assertEquals(1, it.size) }.first()
        assertNull(feiletAdOutbox.prosessertDato)
        assertNotNull(feiletAdOutbox.sisteForsøkDato)
        assertTrue(feiletAdOutbox.harFeilet)
        assertEquals(initiellAdOutbox.uuid, feiletAdOutbox.uuid)
        assertEquals(initiellAdOutbox.id, feiletAdOutbox.id)
        assertEquals(initiellAdOutbox.payload, feiletAdOutbox.payload)

        stillingOutboxService.markerSomProsesert(feiletAdOutbox)
        val prosessertAdOubox = stillingOutboxRepository.hentAlle().also { assertEquals(1, it.size) }.first()
        assertNotNull(prosessertAdOubox.prosessertDato)
        assertNotNull(prosessertAdOubox.sisteForsøkDato)
        assertTrue(prosessertAdOubox.harFeilet)
        assertEquals(initiellAdOutbox.uuid, prosessertAdOubox.uuid)
        assertEquals(initiellAdOutbox.id, prosessertAdOubox.id)
        assertEquals(initiellAdOutbox.payload, prosessertAdOubox.payload)

        assertEquals(0, stillingOutboxRepository.hentUprosesserteMeldinger(outboxDelay = 0).size)
    }
}
