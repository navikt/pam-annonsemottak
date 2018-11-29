package no.nav.pam.annonsemottak.annonsemottak;

public enum Kilde {
    FINN("FINN"),
    DEXI("webcrawl"),
    AMEDIA("AMEDIA"),
    STILLINGSOLR("stillingsolr"),
    POLARIS("POLARIS");

    private String kilde;

    Kilde(String kilde) {
        this.kilde = kilde;
    }

    public String value() {
        return kilde;
    }
}
