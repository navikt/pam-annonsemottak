package no.nav.pam.annonsemottak.receivers.amedia;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Converts date strings from Amedia API
 */
class AmediaDateConverter {

    private static final String AMEDIA_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ssZ";

    static LocalDateTime convertDate(final String date) {
        if (StringUtils.isBlank(date)) {
            return null;
        }

        String parseableAmediaDate = date.replace("Z", "+0000");
        return LocalDateTime.parse(parseableAmediaDate, DateTimeFormatter.ofPattern(AMEDIA_DATE_PATTERN));
    }

    static LocalDateTime getInitialDate() {
        return LocalDateTime.of(1900, 1, 1, 0, 0, 0);
    }
}
