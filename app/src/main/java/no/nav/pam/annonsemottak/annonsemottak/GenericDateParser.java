package no.nav.pam.annonsemottak.annonsemottak;


import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import static java.time.temporal.ChronoField.YEAR_OF_ERA;

public class GenericDateParser {

    private static Map<String, String> dateFormatMap = new HashMap();

    static {
        dateFormatMap.put("^\\d{4}\\.\\d{1,2}\\.\\d{1,2}$", "yyyy.M.dd");
        dateFormatMap.put("^\\d{1,2}\\.\\d{1,2}\\.\\d{2,4}$", "dd.M.[yyyy][yy]");
        dateFormatMap.put("^\\d{1,2}\\.[a-z]+\\.\\d{2,4}$", "dd.[LLLL][LLL].[yyyy][yy]");

        // No year dates, default to the current year
        dateFormatMap.put("^\\d{1,2}\\.[a-z]+$", "dd.[LLLL][LLL]");
        dateFormatMap.put("^\\d{1,2}\\.\\d{1,2}$","dd.M");
    }

    /**
     * Attempts to parse a generic free text date
     * @param rawDate
     * @return
     */
    public static LocalDateTime parseDate(String rawDate) {
        if (rawDate==null) {
            return null;
        }
        String dateString = sanitize(rawDate);
        Optional<String> format = dateFormatMap.keySet().stream().filter(regex -> Pattern.matches(regex, dateString)).findFirst();
        if (!format.isPresent()) {
            return null;
        }

        // NOTE: uuuu - year of era, yyyy - year. Defaulting will cause conflict if year of era is already set
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendPattern(dateFormatMap.get(format.get()))
                .parseDefaulting(YEAR_OF_ERA,  ZonedDateTime.now().getYear())
                .toFormatter(Locale.forLanguageTag("no"));

        return LocalDate.parse(dateString, formatter).atStartOfDay();
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
