package no.nav.pam.annonsemottak.receivers;

public enum Kilde {
    SBL("Stillingsregistrering"),
    FINN("FINN"),
    @Deprecated DEXI("webcrawl"), // deprecated
    AMEDIA("AMEDIA"),
    @Deprecated STILLINGSOLR("stillingsolr"), // deprecated
    POLARIS("POLARIS"),
    @Deprecated XML_STILLING("xmlstilling"); // deprecated

    private final String kilde;

    Kilde(String kilde) {
        this.kilde = kilde;
    }

    public String value() {
        return kilde;
    }
}
