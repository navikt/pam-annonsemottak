package no.nav.pam.annonsemottak.receivers.amedia

import com.fasterxml.jackson.databind.JsonNode

class AmediaFieldTransformer {

    private val lastSlashRegex = ".*/\\s*(.*)".toRegex()

    fun finnSted(sted: String?): String {
        if (sted.isNullOrBlank()) return IKKE_OPPGITT

        return lastSlashRegex.find(sted)?.groupValues?.last() ?: sted
    }

    fun reservefelt(vararg tekst: String?) = tekst.filterNotNull()
            .filter { it.isNotBlank() }
            .firstOrNull() ?: "Ikke oppgitt"

    fun hentListeSomStreng(node: JsonNode?): String = node?.toString() ?: IKKE_OPPGITT

    fun hentListeSomStrenger(node: JsonNode?) = node?.map { it.toString() } ?: emptyList()

    fun hentListeSomJsonnoder(node: JsonNode?) = node?.toList() ?: emptyList()

    companion object {

        const val IKKE_OPPGITT = "Ikke oppgitt"

    }

}