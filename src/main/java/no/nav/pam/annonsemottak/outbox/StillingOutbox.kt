package no.nav.pam.annonsemottak.outbox

import java.time.LocalDateTime

data class StillingOutbox(
    var id: Long? = null,
    val uuid: String,
    val payload: String,
    val opprettetDato: LocalDateTime = LocalDateTime.now(),
    val harFeilet: Boolean = false,
    val antallForsøk: Int = 0,
    val sisteForsøkDato: LocalDateTime? = null,
    val prosessertDato: LocalDateTime? = null,
)
