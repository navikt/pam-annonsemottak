package no.nav.pam.annonsemottak.stilling;

public enum Status {

    MOTTATT(0),
    UNDER_ARBEID(1),
    GODKJENT(2),
    AVVIST(3),
    FJERNET(4),
    OPPDATERT(5);

    private final int statuskode;

    Status(int statuskode) {
        this.statuskode = statuskode;
    }

    public static Status valueOfStatuskode(String statuskode) {
        for (Status status : values()) {
            if (status.statuskode == Integer.parseInt(statuskode)) {
                return status;
            }
        }

        throw new IllegalArgumentException("Statuskode = " + statuskode + " er ikke støttet.");
    }

    public String getKodeAsString() {
        return String.valueOf(statuskode);
    }
}
