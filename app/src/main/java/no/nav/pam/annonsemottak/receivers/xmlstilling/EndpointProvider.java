package no.nav.pam.annonsemottak.receivers.xmlstilling;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
class EndpointProvider {

    private final String url;

    EndpointProvider(@Value("${xmlstilling.url}") String url) {
        this.url = url;
    }

    String forFetchWithStartingId(int latestId) {
        return url + "load/" + latestId + "/count/" + 10;
    }

    String forPing() {
        return url + "isAlive";
    }

}
