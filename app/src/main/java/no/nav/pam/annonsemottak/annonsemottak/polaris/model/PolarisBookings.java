package no.nav.pam.annonsemottak.annonsemottak.polaris.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PolarisBookings {

    @JsonProperty("EndDate")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    public LocalDateTime endDate;

    @JsonProperty("Publication")
    public String publication;

    @JsonProperty("StartDate")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    public LocalDateTime startDate;
}
