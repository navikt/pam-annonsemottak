package no.nav.pam.annonsemottak.annonsemottak;

public enum Medium {
    FINN("FINN"),
    DEXI("DEXI"),
    AMEDIA("AMEDIA"),
    UNKNOWN("UNKNOWN");

    private String kilde;

    Medium(String kilde) {
        this.kilde = kilde;
    }

    public String value() {
        return kilde;
    }
}