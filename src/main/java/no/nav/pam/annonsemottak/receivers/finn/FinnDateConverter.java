package no.nav.pam.annonsemottak.receivers.finn;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Converts date strings from FINN API
 */
class FinnDateConverter {

    private static final String FINN_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss[.SSS]Z";


    public static LocalDateTime convertDate(String date) {
        if (StringUtils.isBlank(date)) {
            return null;
        }

        date = date.replace("Z", "+0000");
        return LocalDateTime.parse(date, DateTimeFormatter.ofPattern(FINN_DATE_PATTERN));
    }

    public static String toString(LocalDateTime date) {

        String dateValue = date.atZone(ZoneId.of("Z"))
                .format(DateTimeFormatter.ofPattern(FINN_DATE_PATTERN));
        return dateValue.replace("+0000", "Z");
    }
}
