package no.nav.pam.annonsemottak.stilling;

import java.util.Optional;

public class Arbeidsgiver {

    private final String arbeidsgiver;

    public static Optional<Arbeidsgiver> ofNullable(String arbeidsgiver) {
        return arbeidsgiver != null ? Optional.of(new Arbeidsgiver(arbeidsgiver)) : empty();
    }

    static Optional<Arbeidsgiver> empty() {
        return Optional.empty();
    }

    /**
     * Oppretter en gyldig arbeidsgiver. Sørger samtidig for å trimme string.
     */
    private Arbeidsgiver(String arbeidsgiver) {
        if (arbeidsgiver == null) {
            throw new IllegalArgumentException("Arbeidsgiver kan ikke være null. Bruk Optional om du har behov for å uttrykke ingen/blank kommentar.");
        }
        this.arbeidsgiver = arbeidsgiver.trim();
    }

    public String asString() {
        return arbeidsgiver;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Arbeidsgiver that = (Arbeidsgiver) o;

        return arbeidsgiver.equals(that.arbeidsgiver);
    }

    @Override
    public String toString() {
        return "Arbeidsgiver{" +
            "arbeidsgiver='" + arbeidsgiver + '\'' +
            '}';
    }

    @Override
    public int hashCode() {
        return arbeidsgiver.hashCode();
    }
}
