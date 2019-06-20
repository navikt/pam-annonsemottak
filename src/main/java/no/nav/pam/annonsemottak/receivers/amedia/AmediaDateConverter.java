package no.nav.pam.annonsemottak.receivers.amedia;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Converts date strings from Amedia API
 */
class AmediaDateConverter {

    private static final String AMEDIA_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ssZ";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(AMEDIA_DATE_PATTERN);
    private static final ZoneId DEFAULT_ZONE = ZoneId.of("Z");


    static LocalDateTime convertDate(final String date) {
        if (StringUtils.isBlank(date)) {
            return null;
        }

        String parseableAmediaDate = date.replace("Z", "+0000");
        return LocalDateTime.parse(parseableAmediaDate, DateTimeFormatter.ofPattern(AMEDIA_DATE_PATTERN));
    }

    static String toStringUrlEncoded(final LocalDateTime date) {
        return urlEncode(date.atZone(DEFAULT_ZONE).format(DATE_TIME_FORMATTER));
    }

    private static String urlEncode(final String s) {
        return s.replace("+0000", "Z").replace(":", "%5C:");
    }

    static LocalDateTime getInitialDate() {
        return LocalDateTime.of(1900, 1, 1, 0, 0, 0);
    }
}
