package no.nav.pam.annonsemottak.receivers.xmlstilling;

import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class EndpointProviderTest {

    private EndpointProvider endpointProvider = new EndpointProvider("http://pam-xml-stilling.default/");

    @Test
    public void that_ping_url_is_correct() {
        assertThat(endpointProvider.forPing()).isEqualTo("http://pam-xml-stilling.default/isAlive");
    }

    @Test
    public void that_fetch_url_is_constructed_correctly() {
        assertThat(endpointProvider.forFetchWithStartingId(5)).isEqualTo("http://pam-xml-stilling.default/load/5/count/10" );
    }

}
