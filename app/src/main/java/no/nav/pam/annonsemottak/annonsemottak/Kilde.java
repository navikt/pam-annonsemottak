package no.nav.pam.annonsemottak.annonsemottak;

public enum Kilde {
    SBL("Stillingsregistrering"),
    FINN("FINN"),
    DEXI("webcrawl"),
    AMEDIA("AMEDIA"),
    STILLINGSOLR("stillingsolr");

    private String kilde;

    Kilde(String kilde) {
        this.kilde = kilde;
    }

    public String value() {
        return kilde;
    }
}
