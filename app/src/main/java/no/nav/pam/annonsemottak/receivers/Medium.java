package no.nav.pam.annonsemottak.receivers;

public enum Medium {
    SBL("Stillingsregistrering"),
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
