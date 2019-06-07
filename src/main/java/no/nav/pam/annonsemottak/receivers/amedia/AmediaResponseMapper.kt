package no.nav.pam.annonsemottak.receivers.amedia

import com.fasterxml.jackson.databind.JsonNode
import no.nav.pam.annonsemottak.stilling.Stilling

/**
 * Mapper respons fra Amedia til en liste med stillinger
 */
internal object AmediaResponseMapper {

    @JvmStatic
    fun mapResponse(amediaResponse: JsonNode): List<Stilling> = amediaResponse
            .path("hits").path("hits")
            .map { h -> AmediaStillingMapper(h) }
            .filter { amediaStilling -> !amediaStilling.isFromNav }
            .map { it.getStilling() }

    @JvmStatic
    fun mapEksternIder(amediaResponse: JsonNode): List<String> = amediaResponse
            .path("hits").path("hits")
            .map { h -> text(h.path("_id")) }

    @JvmStatic
    fun text(node: JsonNode?) = node?.asText()?.convertNullToBlank() ?: ""

    private fun String.convertNullToBlank() = if(this == "null") "" else this
}
