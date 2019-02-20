package no.nav.pam.annonsemottak.rest.payloads;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * JSON payload for Annonsehode.
 */
public class AnnonsehodePayload {

    private String uuid;
    private String mottattDato;
    private String arbeidsgiver;
    private String tittel;
    private String kilde;
    private String status;
    private String merknader;
    private String saksbehandler;
    private String arbeidssted;
    private String annonsestatus;
    private String soknadsfrist;
    private String kommentarer;
    private String modifisertDato;

    private AnnonsehodePayload() {
        // Use the Builder.
    }

    @JsonProperty("uuid")
    public String getUuid() {
        return uuid;
    }

    @JsonProperty("Mottattdato")
    public String getMottattDato() {
        return mottattDato;
    }

    @JsonProperty("Arbeidsgiver")
    public String getArbeidsgiver() {
        return arbeidsgiver;
    }

    @JsonProperty("Tittel")
    public String getTittel() {
        return tittel;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("kilde")
    public String getKilde() { return kilde; }

    @JsonProperty("merknader")
    public String getMerknader() {
        return merknader;
    }

    @JsonProperty("saksbehandler")
    public String getSaksbehandler() {
        return saksbehandler;
    }

    @JsonProperty("Sted")
    public String getArbeidssted() { return arbeidssted; }

    @JsonProperty("AnnonseStatus")
    public String getAnnonsestatus() { return annonsestatus; }

    @JsonProperty("Soknadsfrist")
    public String getSoknadsfrist() { return soknadsfrist; }

    @JsonProperty("Kommentarer")
    public String getKommentarer() { return kommentarer; }

    @JsonProperty("Modifisertdato")
    public String getModifisertDato() {return modifisertDato; }

    public static class Builder {

        private final AnnonsehodePayload built;

        public Builder() {
            built = new AnnonsehodePayload();
        }

        public Builder setUuid(String uuid) {
            built.uuid = uuid;
            return this;
        }

        public Builder setMottattDato(String mottattDato) {
            built.mottattDato = mottattDato;
            return this;
        }

        public Builder setArbeidsgiver(String arbeidsgiver) {
            built.arbeidsgiver = arbeidsgiver;
            return this;
        }

        public Builder setTittel(String tittel) {
            built.tittel = tittel;
            return this;
        }

        public Builder setStatus(String status) {
            built.status = status;
            return this;
        }

        public Builder setMerknader(String merknader) {
            built.merknader = merknader;
            return this;
        }

        public Builder setSaksbehandler(String saksbehandler) {
            built.saksbehandler = saksbehandler;
            return this;
        }

        public Builder setArbeidssted(String arbeidssted) {
            built.arbeidssted = arbeidssted;
            return this;
        }

        public Builder setKilde(String kilde) {
            built.kilde = kilde;
            return this;
        }

        public Builder setAnnonsestatus(String annonsestatus) {
            built.annonsestatus = annonsestatus;
            return this;
        }

        public Builder setSoknadsfrist(String soknadsfrist) {
            built.soknadsfrist = soknadsfrist;
            return this;
        }

        public Builder setKommentarer(String kommentarer) {
            built.kommentarer = kommentarer;
            return this;
        }

        public Builder setModifisertDato(String modifisertDato){
            built.modifisertDato = modifisertDato;
            return this;
        }

        public AnnonsehodePayload build() {
            return built;
        }

    }

}
