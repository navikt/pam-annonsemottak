package no.nav.pam.annonsemottak.annonsemottak;


import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

public class GenericDateParser {

    private static Map<String, String> dateFormatMap = new HashMap();

    static {
        //dateFormatMap.put("^\\d{2,}\\.\\d{2,}\\.\\d{4}$", "dd.M.yyyy");
        dateFormatMap.put("^\\d{1,2}\\.\\d{1,2}$","dd.MM");
        dateFormatMap.put("^\\d{1,2}\\.\\d{1,2}\\.\\d{2,4}$", "dd.M.yy");
        dateFormatMap.put("^\\d{4}\\.\\d{1,2}\\.\\d{1,2}$", "yyyy.M.dd");
        dateFormatMap.put("^\\d{1,2}\\.[a-z]+$", "dd.MMM");
        dateFormatMap.put("^\\d{1,2}\\.[a-z]+\\.\\d{2,4}$", "dd.MMM.yy");
    }

    /**
     * Attempts to parse a generic free text date
     * @param rawDate
     * @return
     */
    public static DateTime parseDate(String rawDate) {
        if (rawDate==null) {
            return null;
        }
        String dateString = sanitize(rawDate);
        Optional<String> format = dateFormatMap.keySet().stream().filter(regex -> Pattern.matches(regex, dateString)).findFirst();
        if (!format.isPresent()) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormat.forPattern(dateFormatMap.get(format.get()))
                .withLocale(new Locale("no"))
                .withDefaultYear(DateTime.now().getYear());  //Default year is the current year.

        return formatter.parseDateTime(dateString);
    }

    // Sanitize possible date string into a parseable dd.MM.yyyy format
    private static String sanitize(String rawDate) {
        String sanitized = rawDate
                .replace("/", ".")
                .replace("-", ".")
                .replace(" ", ".");

        // dates of format dd. MMM will be changed to dd.MMM as it is easier to handle
        sanitized = StringUtils.replacePattern(sanitized, "\\.{2,}", ".");

        return sanitized.toLowerCase().trim();
    }
}
