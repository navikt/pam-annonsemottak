package no.nav.pam.annonsemottak.receivers;

public enum Medium {
    SBL("Stillingsregistrering"),
    FINN("FINN"),
    @Deprecated DEXI("DEXI"),
    AMEDIA("AMEDIA"),
    @Deprecated POLARIS("POLARIS"),
    UNKNOWN("UNKNOWN");

    private final String kilde;

    Medium(String kilde) {
        this.kilde = kilde;
    }

    public String value() {
        return kilde;
    }
}
