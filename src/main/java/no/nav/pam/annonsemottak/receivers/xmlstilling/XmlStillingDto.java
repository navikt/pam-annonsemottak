package no.nav.pam.annonsemottak.receivers.xmlstilling;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
class XmlStillingDto {

    @JsonProperty
    private String arbeidsgiver;

    @JsonProperty
    private String eksternBrukerRef;

    @JsonProperty
    private String arbeidsgiverBedriftspresentasjon;

    @JsonProperty
    private String stillingsbeskrivelse;

    @JsonProperty
    private String stillingstittel;

    @JsonProperty
    private LocalDateTime soknadsfrist;

    @JsonProperty
    private String eksternId;

    @JsonProperty
    private LocalDateTime publiseresFra;

    @JsonProperty
    private LocalDateTime sistePubliseringsdato;

    @JsonProperty
    private LocalDateTime mottattTidspunkt;

    @JsonProperty
    private Integer antallStillinger;

    @JsonProperty
    private String arbeidssted;

    @JsonProperty
    private Float stillingsprosent;

    @JsonProperty
    private String kontaktinfoPerson;

    @JsonProperty
    private String kontaktinfoTelefon;

    @JsonProperty
    private String kontaktinfoEpost;

    @JsonProperty
    private String arbeidsgiverAdresse;

    @JsonProperty
    private String arbeidsgiverPostnummer;

    @JsonProperty
    private String arbeidsgiverWebadresse;

    @JsonProperty
    private LocalDateTime ledigFra;

    String getArbeidsgiver() {
        return arbeidsgiver;
    }

    String getEksternBrukerRef() {
        return eksternBrukerRef;
    }

    String getArbeidsgiverBedriftspresentasjon() {
        return arbeidsgiverBedriftspresentasjon;
    }

    String getStillingsbeskrivelse() {
        return stillingsbeskrivelse;
    }

    Optional<LocalDateTime> getSoknadsfrist() {
        return Optional.ofNullable(soknadsfrist);
    }

    String getEksternId() {
        return eksternId;
    }

    String getStillingstittel() {
        return stillingstittel;
    }

    LocalDateTime getMottattTidspunkt() {
        return mottattTidspunkt;
    }


    LocalDateTime getPubliseresFra() {
        return publiseresFra;
    }

    LocalDateTime getSistePubliseringsdato() {
        return sistePubliseringsdato;
    }

    Integer getAntallStillinger() {
        return antallStillinger;
    }

    String getArbeidssted() {
        return arbeidssted;
    }

    Float getStillingsprosent() {
        return stillingsprosent;
    }

    String getKontaktinfoPerson() {
        return kontaktinfoPerson;
    }

    String getKontaktinfoTelefon() {
        return kontaktinfoTelefon;
    }

    String getKontaktinfoEpost() {
        return kontaktinfoEpost;
    }

    String getArbeidsgiverAdresse() {
        return arbeidsgiverAdresse;
    }

    String getArbeidsgiverPostnummer() {
        return arbeidsgiverPostnummer;
    }

    String getArbeidsgiverWebadresse() {
        return arbeidsgiverWebadresse;
    }

    LocalDateTime getLedigFra() {
        return ledigFra;
    }

    public void setStillingstittel(String stillingstittel) {
        this.stillingstittel = stillingstittel;
    }
}
