package no.nav.pam.annonsemottak.stilling.dto;

import com.fasterxml.jackson.annotation.*;
import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.Map;

/**
 * DTO representation of a {@code Stilling}.
 */
@SuppressWarnings("unused")
@JsonIgnoreProperties(ignoreUnknown = true)
public class StillingDTO {

    private String uuid;
    private String employerName;
    private String employerDescription;
    private String jobTitle;
    private String jobLocation;
    private String jobDescription;
    private String applicationDeadline;
    private String kilde;
    private String medium;
    private String orgNummer;
    private Integer antallStillinger;
    private DateTime publiserFra;
    private DateTime sistePubliseringsDato;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private String status;

    private Map<String, String> properties = new HashMap<String, String>();

    @JsonProperty(required = true)
    public String getUuid() {
        return uuid;
    }

    @JsonProperty(required = true)
    public String getEmployerName() {
        return employerName;
    }

    public String getEmployerDescription() {
        return employerDescription;
    }

    @JsonProperty(required = true)
    public String getJobTitle() {
        return jobTitle;
    }

    @JsonProperty(required = true)
    public String getJobLocation() {
        return jobLocation;
    }

    @JsonProperty(required = true)
    public String getJobDescription() {
        return jobDescription;
    }

    @JsonProperty(required = true)
    public String getApplicationDeadline() {
        return applicationDeadline;
    }

    @JsonAnySetter
    public void setProperty(String name, String value) {
        properties.put(name, value);
    }

    @JsonAnyGetter
    public Map<String, String> getProperties() {
        return properties;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setEmployerName(String employerName) {
        this.employerName = employerName;
    }

    public void setEmployerDescription(String employerDescription) {
        this.employerDescription = employerDescription;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public void setJobLocation(String jobLocation) {
        this.jobLocation = jobLocation;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public void setApplicationDeadline(String applicationDeadline) {
        this.applicationDeadline = applicationDeadline;
    }

    public String getOrgNummer() {
        return orgNummer;
    }

    public void setOrgNummer(String orgNummer) {
        this.orgNummer = orgNummer;
    }

    public Integer getAntallStillinger() {
        return antallStillinger;
    }

    public void setAntallStillinger(Integer antallStillinger) {
        this.antallStillinger = antallStillinger;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    public DateTime getPubliserFra() {
        return publiserFra;
    }

    public void setPubliserFra(DateTime publiserFra) {
        this.publiserFra = publiserFra;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    public DateTime getSistePubliseringsDato() {
        return sistePubliseringsDato;
    }

    public void setSistePubliseringsDato(DateTime sistePubliseringsDato) {
        this.sistePubliseringsDato = sistePubliseringsDato;
    }

    public String getKilde() {
        return kilde;
    }

    public void setKilde(String kilde) {
        this.kilde = kilde;
    }

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }
}
