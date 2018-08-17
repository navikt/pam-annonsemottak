package no.nav.pam.annonsemottak.temp.feedclient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import no.nav.pam.annonsemottak.stilling.AnnonseStatus;
import no.nav.pam.annonsemottak.stilling.Status;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StillingFeedItem {

    @JsonDeserialize(using = LocalDateTimeFromEpochDeserializer.class)
    public LocalDateTime created;
    public String createdBy;
    public String createdByDisplayName;

    @JsonDeserialize(using = LocalDateTimeFromEpochDeserializer.class)
    public LocalDateTime updated;
    public String updatedBy;
    public String updatedByDisplayName;
    public String uuid;
    public String kilde;
    public String medium;
    public String hash;
    public String url;
    public String externalId;

    @JsonDeserialize(using = LocalDateTimeFromEpochDeserializer.class)
    public LocalDateTime published;

    public AnnonseStatus annonseStatus;

    @JsonDeserialize(using = LocalDateTimeFromEpochDeserializer.class)
    public LocalDateTime expires;

    @JsonDeserialize(using = LocalDateTimeFromEpochDeserializer.class)
    public LocalDateTime systemModifiedDate;

    public String arbeidsgiver;
    public String stillingstittel;
    public String annonsetekst;
    public String arbeidsgiveromtale;
    public String arbeidssted;
    public String soeknadsfrist;

    //Saksbehandling
    public String saksbehandler;
    public Status status;
    public String merknader;
    public String kommentarer;

    public Map<String, String> properties = new HashMap<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StillingFeedItem that = (StillingFeedItem) o;

        return uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}
