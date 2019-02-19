package no.nav.pam.annonsemottak.receivers;

public enum Kilde {
    SBL("Stillingsregistrering"),
    FINN("FINN"),
    DEXI("webcrawl"),
    AMEDIA("AMEDIA"),
    STILLINGSOLR("stillingsolr"),
    POLARIS("POLARIS"),
    XML_STILLING("xmlstilling");

    private final String kilde;

    Kilde(String kilde) {
        this.kilde = kilde;
    }

    public String value() {
        return kilde;
    }
}
