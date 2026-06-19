package no.nav.pam.annonsemottak.app.rest

import no.nav.pam.annonsemottak.Application
import no.nav.pam.annonsemottak.app.rest.StatusController.InternalException
import no.nav.pam.annonsemottak.kafka.HealthService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.context.WebApplicationContext


@SpringBootTest(classes = [Application::class])
@Transactional
@Rollback
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
open class StatusControllerTest(
    @Autowired private val statusController: StatusController,
    @Autowired private val healthService: HealthService,
    @Autowired private val webApplicationContext: WebApplicationContext
) {
    @Test
    fun `API-kall til isAlive svarer med 500 når healthService er trist`() {
        val mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build()
        assertEquals("OK", statusController.isAlive)
        mockMvc.perform(MockMvcRequestBuilders.get("/isAlive")).andExpect(status().isOk).andReturn()
        healthService.addUnhealthyVote()
        assertThrows<InternalException> { statusController.isAlive }
        mockMvc.perform(MockMvcRequestBuilders.get("/isAlive")).andExpect(status().isInternalServerError).andReturn()
    }
}
