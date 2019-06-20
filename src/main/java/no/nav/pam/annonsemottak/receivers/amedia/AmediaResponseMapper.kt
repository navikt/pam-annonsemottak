package no.nav.pam.annonsemottak.receivers.amedia

import com.fasterxml.jackson.databind.JsonNode
import no.nav.pam.annonsemottak.stilling.Stilling

/**
 * Mapper respons fra Amedia til en liste med stillinger
 */
internal object AmediaResponseMapper {

    @JvmStatic
    fun mapResponse(amediaResponse: JsonNode): List<Stilling> = hitlist(amediaResponse)
            .map(::AmediaStillingMapper)
            .filter { !it.isFromNav }
            .map { it.getStilling() }

    @JvmStatic
    fun mapEksternIder(amediaResponse: JsonNode): List<String> = hitlist(amediaResponse)
            .map { h -> text(h.path("_id")) }

    private fun hitlist(amediaResponse: JsonNode) = amediaResponse.path("hits").path("hits")

    @JvmStatic
    fun text(node: JsonNode?) = node?.asText()?.convertNullToBlank() ?: ""

    private fun String.convertNullToBlank() = if(this == "null") "" else this
}
