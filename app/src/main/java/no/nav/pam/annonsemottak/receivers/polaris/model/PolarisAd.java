package no.nav.pam.annonsemottak.receivers.polaris.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

import java.time.LocalDateTime;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PolarisAd {

    @JsonProperty("AccessionDate")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    public LocalDateTime accessionDate;

    @JsonProperty("AccessionText")
    public String accessionText;

    @JsonProperty("ApplicationDeadlineDate")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    public LocalDateTime applicationDeadlineDate;

    @JsonProperty("ApplicationDeadlineText")
    public String applicationDeadlineText;

    @JsonProperty("ApplicationMarked")
    public String applicationMarked;

    @JsonProperty("ApplicationRecipientEmail")
    public String applicationRecipientEmail;

    @JsonProperty("CompanyInformation")
    public String companyInformation;

    @JsonProperty("CompanyLogo")
    public String companyLogo;

    @JsonProperty("CompanyName")
    public String companyName;

    @JsonProperty("CompanyWebsite")
    public String companyWebsite;

    @JsonProperty("DateTimeCreated")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    public LocalDateTime dateTimeCreated;

    @JsonProperty("DateTimeModified")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    public LocalDateTime dateTimeModified;

    @JsonProperty("EmploymentLevel")
    public String employmentLevel;

    @JsonProperty("EmploymentType")
    public String employmentType;

    @JsonProperty("ExternalSystemUrl")
    public String externalSystemUrl;

    @JsonProperty("Keywords")
    public String keywords;

    @JsonProperty("PositionId")
    public String positionId;

    @JsonProperty("Salary")
    public String salary;

    @JsonProperty("Sector")
    public String sector;

    @JsonProperty("Text")
    public String text;

    @JsonProperty("Title")
    public String title;

    @JsonProperty("Url")
    public String url;

    @JsonProperty("Vacancies")
    public Integer vacancies;

    @JsonProperty("Location")
    public PolarisLocation location;

    @JsonProperty("Bookings")
    public PolarisBookings bookings;

    @JsonProperty("ContactPeople")
    public List<PolarisContact> contacts;

    @JsonProperty("Categories")
    public List<PolarisCategory> categories;

    public PolarisAd() {
    }

}

