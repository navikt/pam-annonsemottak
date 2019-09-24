package no.nav.pam.annonsemottak.receivers.amedia;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
class AmediaUrl {

    private static final LocalDateTime START_OF_TIME = LocalDateTime.of(2000, 1, 1, 0, 0, 0);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");
    private static final ZoneId DEFAULT_ZONE = ZoneId.of("Z");

    private final String url;

    AmediaUrl(@Value("${amedia.url}") final String amediaUrl) {
        this.url = amediaUrl;
    }

    String all() {
        return url + "/all";
    }

    String modifiedAfter(final LocalDateTime localDateTime) {
        return url + "?modified=" + toStringUrlEncoded(localDateTime);
    }

    String ping() {
        return url;
    }

    static String toStringUrlEncoded(final LocalDateTime date) {
        final LocalDateTime nullSafeDate = date == null ? START_OF_TIME : date;
        return urlEncode(nullSafeDate.atZone(DEFAULT_ZONE).format(DATE_TIME_FORMATTER));
    }

    private static String urlEncode(final String s) {
        return s.replace("+0000", "Z").replace(":", "%5C:");
    }
}
