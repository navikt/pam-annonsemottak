package no.nav.pam.annonsemottak.receivers.xmlstilling;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
class EndpointProvider {

    private final String url;

    private DateTimeFormatter urlDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd/HH/mm/ss");

    EndpointProvider(@Value("${xmlstilling.url}") String url) {
        this.url = url;
    }

    String forFetchWithStartingId(LocalDateTime lastRun) {
        return url + "/load/" + urlDateTimeFormatter.format(lastRun);
    }

    String forPing() {
        return url + "isAlive";
    }

}
