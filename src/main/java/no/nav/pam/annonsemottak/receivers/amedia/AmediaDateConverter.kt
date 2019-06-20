package no.nav.pam.annonsemottak.receivers.amedia

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


/**
 * Converts date strings from Amedia API
 */
object AmediaDateConverter {

    @JvmStatic
    fun convertDate(date: String): LocalDateTime? = date
            .ifBlank { null }
            ?.replace("Z", "+0000")
            .let { LocalDateTime.parse(it, dateTimeFormatter) }

    @JvmStatic
    fun toStringUrlEncoded(date: LocalDateTime) = date.atZone(defaultZone).format(dateTimeFormatter).urlEncode()

}

private val defaultZone = ZoneId.of("Z")
private fun String.urlEncode() = this.replace("+0000", "Z").replace(":", "%5C:")
private const val amedia_date_pattern = "yyyy-MM-dd'T'HH:mm:ssZ"
private val dateTimeFormatter = DateTimeFormatter.ofPattern(amedia_date_pattern)
