package no.nav.pam.annonsemottak.stilling;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class StillingBuilder {

    private String uuid;
    private String employer;
    private String employerDescription;
    private String title;
    private String place;
    private String jobDescription;
    private String dueDate;
    private String kilde;
    private String medium;
    private String url;
    private String externalId;
    private LocalDateTime expires;
    private Map<String, String> properties = new HashMap<>();
    private LocalDateTime systemModifiedDate;
    private LocalDateTime published;

    public Stilling build() {

        Stilling stilling = new Stilling(
                title,
                place,
                employer,
                employerDescription,
                jobDescription,
                dueDate,
                kilde,
                medium,
                url,
                externalId
        );

        stilling.setUuid(uuid != null ? uuid : UUID.randomUUID().toString());
        stilling.setExpires(expires);
        stilling.addProperties(properties);
        stilling.setSystemModifiedDate(systemModifiedDate);
        stilling.setPublished(published);

        stilling.calculateHash();

        return stilling;
    }


    public StillingBuilder withProperties(Map<String, String> properties) {
        if(properties != null) this.properties.putAll(properties);
        return this;
    }

    public StillingBuilder uuid(String uuid) {
        this.uuid = uuid;
        return this;
    }
    
    public StillingBuilder employer(String employer) {
        this.employer = employer;
        return this;
    }

    public StillingBuilder employerDescription(String employerDescription) {
        this.employerDescription = employerDescription;
        return this;
    }

    public StillingBuilder title(String title) {
        this.title = title;
        return this;
    }

    public StillingBuilder place(String place) {
        this.place = place;
        return this;
    }

    public StillingBuilder jobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
        return this;
    }

    public StillingBuilder dueDate(String dueDate) {
        this.dueDate = dueDate;
        return this;
    }

    public StillingBuilder kilde(String kilde) {
        this.kilde = kilde;
        return this;
    }

    public StillingBuilder medium(String medium) {
        this.medium = medium;
        return this;
    }

    public StillingBuilder url(String url) {
        this.url = url;
        return this;
    }

    public StillingBuilder externalId(String externalId) {
        this.externalId = externalId;
        return this;
    }

    public StillingBuilder expires(LocalDateTime expires) {
        this.expires = expires;
        return this;
    }

    public StillingBuilder systemModifiedDate(LocalDateTime systemModifiedDate) {
        this.systemModifiedDate = systemModifiedDate;
        return this;
    }

    public StillingBuilder published(LocalDateTime published) {
        this.published = published;
        return this;
    }
}
