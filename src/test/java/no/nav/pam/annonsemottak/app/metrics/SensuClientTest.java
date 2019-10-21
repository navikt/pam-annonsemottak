package no.nav.pam.annonsemottak.app.metrics;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SensuClientTest {

    @Test
    public void that_events_produce_valid_json() {
        String json = new SensuClient.SensuEvent("myEvent", "some output").getJson();

        assertThat(json).isEqualToIgnoringCase(
                "{" +
                        "\"name\":\"myEvent\"," +
                        "\"type\":\"metric\"," +
                        "\"handlers\":[\"events_nano\"]," +
                        "\"output\":\"some output\"," +
                        "\"status\":0" +
                        "}"
        );
    }
}
