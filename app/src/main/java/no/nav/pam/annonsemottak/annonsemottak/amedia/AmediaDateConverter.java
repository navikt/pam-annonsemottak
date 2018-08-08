package no.nav.pam.annonsemottak.annonsemottak.amedia;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;

/**
 * Converts date strings from Amedia API
 */
class AmediaDateConverter {

    private static final String AMEDIA_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ssZ";


    static DateTime convertDate(String date) {
        if (StringUtils.isBlank(date)) {
            return null;
        }

        return new DateTime(date, DateTimeZone.UTC);
    }

    static String toStringUrlEncoded(DateTime date) {
        return date.toString(DateTimeFormat.forPattern(AMEDIA_DATE_PATTERN))
            .replace("+0000", "Z")
            .replace(":", "%5C:");
    }

    static DateTime getInitialDate() {
        return new DateTime(1900, 1, 1, 0, 0, 0, DateTimeZone.UTC);
    }

}
