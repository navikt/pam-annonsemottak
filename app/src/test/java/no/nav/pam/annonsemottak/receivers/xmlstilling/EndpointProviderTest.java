package no.nav.pam.annonsemottak.receivers.xmlstilling;

import org.junit.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class EndpointProviderTest {

    private EndpointProvider endpointProvider = new EndpointProvider("http://pam-xml-stilling.default");

    @Test
    public void that_ping_url_is_correct() {
        assertThat(endpointProvider.forPing()).isEqualTo("http://pam-xml-stilling.default/isAlive");
    }

    @Test
    public void that_fetch_url_is_constructed_correctly() {
        LocalDateTime lastRun = LocalDateTime.of(2015, 3, 22, 14, 11, 2);
        assertThat(endpointProvider.forFetchWithStartingId(lastRun)).isEqualTo("http://pam-xml-stilling.default/load/2015/03/22/14/11/02" );
    }

}
