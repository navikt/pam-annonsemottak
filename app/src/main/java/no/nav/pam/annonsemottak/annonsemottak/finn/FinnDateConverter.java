package no.nav.pam.annonsemottak.annonsemottak.finn;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;

/**
 * Converts date strings from FINN API
 */
public class FinnDateConverter {

    public static final String FINN_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";


    public static DateTime convertDate(String date) {
        if(StringUtils.isBlank(date)){
            return null;
        }

        return new DateTime(date).toDateTime(DateTimeZone.UTC);
    }

    public static String toString(DateTime date) {
        return date.toString(DateTimeFormat.forPattern(FINN_DATE_PATTERN)).replace("+0000", "Z");
    }
}
