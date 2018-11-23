package no.nav.pam.annonsemottak.feed;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class StillingMixIn {

    @JsonProperty("arbeidsgiveromtale")
    String employerDescription;

    @JsonProperty("annonsetekst")
    String jobDescription;

    @JsonProperty("soeknadsfrist")
    String dueDate;

    @JsonProperty("arbeidssted")
    String place;

    @JsonProperty("stillingstittel")
    String title;
}
