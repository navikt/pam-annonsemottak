package no.nav.pam.annonsemottak.feed;

import com.fasterxml.jackson.annotation.JsonValue;

public abstract class OptionalValueMixIn {

    @JsonValue
    public abstract String asString();
}
