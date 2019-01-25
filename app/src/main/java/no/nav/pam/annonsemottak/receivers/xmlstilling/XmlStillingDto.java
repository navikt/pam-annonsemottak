package no.nav.pam.annonsemottak.receivers.xmlstilling;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

class XmlStillingDto {

    @JsonProperty
    private String employer;

    @JsonProperty
    private String externalUser; // EKSTERN_BRUKER_REF - Mappes til Medium

    @JsonProperty
    private String employerDescription;

    @JsonProperty
    private String jobDescription;

    @JsonProperty
    private String title;

    @JsonProperty
    private LocalDateTime dueDate;

    @JsonProperty
    private String externalId;

    @JsonProperty
    private LocalDateTime published = LocalDateTime.now();

    @JsonProperty
    private LocalDateTime expires = LocalDateTime.now().plusDays(10);

    private String AannonseStatus = "INAKTIV"; // default inaktiv

    // private LocalDateTime systemModifiedDate;


    String getEmployer() {
        return employer;
    }

    String getExternalUser() {
        return externalUser;
    }

    String getEmployerDescription() {
        return employerDescription;
    }

    String getJobDescription() {
        return jobDescription;
    }

    LocalDateTime getDueDate() {
        return dueDate;
    }

    String getExternalId() {
        return externalId;
    }

    String getTitle() {
        return title;
    }
}
