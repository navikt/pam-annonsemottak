package no.nav.pam.annonsemottak.rest.payloads;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * The basic class describing payloads split into {@code data} and {@code meta} fields.
 *
 * @param <T> The basic {@code data} payload.
 */
@JsonPropertyOrder({"data", "meta"})
public class BasicPayload<T> {

    private final T data;

    BasicPayload(T data) {
        this.data = data;
    }

    @JsonProperty("data")
    public T getData() {
        return data;
    }

    @JsonProperty("meta")
    public Meta getMeta() {
        return new Meta();
    }

    /**
     * Empty implementation of {@code meta} field contents.
     */
    class Meta {

        Meta() {

        }

    }

}
