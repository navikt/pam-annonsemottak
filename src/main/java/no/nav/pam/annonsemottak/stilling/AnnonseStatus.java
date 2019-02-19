package no.nav.pam.annonsemottak.stilling;

/**
 * AKTIV: Annonsen kommer inn for første gang, og klar for publisering.
 * INAKTIV: Annonsen har vært AKTIV, men kanskje søknadsfristen har gått ut, eller at den har vært i systemet en god stund
 * STOPPET: Betyr at annonsen har blitt stoppet/fjernet eksternt eller internt.
 */
public enum AnnonseStatus {
    INAKTIV(0),
    AKTIV(1),
    STOPPET(2);

    private final int code;

    AnnonseStatus(int code) {
        this.code = code;
    }

    public static AnnonseStatus valueOfAnnonseStatusCode(String annonseStatusCode) {

        for (AnnonseStatus status : values()) {
            if (status.code == Integer.parseInt(annonseStatusCode)) {
                return status;
            }
        }

        throw new IllegalArgumentException("Annonse status kode = " + annonseStatusCode + " er ikke støttet.");
    }

    public String getCodeAsString() {
        return String.valueOf(code);
    }
}
