package no.nav.pam.annonsemottak.receivers.common.rest.payloads;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResultsOnSave {

    private final int received;
    private final int saved;
    private final long milliseconds;

    public ResultsOnSave(int received, int saved, long milliseconds) {
        this.received = received;
        this.saved = saved;
        this.milliseconds = milliseconds;
    }

    @JsonProperty("stillingerHentet")
    public int getReceived() {
        return received;
    }

    @JsonProperty("stillingerLagret")
    public int getSaved() {
        return saved;
    }

    @JsonProperty("millisekunderBrukt")
    public long getDurationMillis() {
        return milliseconds;
    }

}
