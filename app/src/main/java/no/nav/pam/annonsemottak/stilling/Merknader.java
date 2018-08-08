package no.nav.pam.annonsemottak.stilling;

import java.io.Serializable;
import java.util.Optional;

public class Merknader implements Serializable {

    private final String merknader;

    static Optional<Merknader> ofNullable(String merknader) {
        return merknader != null ? Optional.of(new Merknader(merknader)) : empty();
    }

    static Optional<Merknader> empty() {
        return Optional.empty();
    }

    public Merknader(String merknader) {
        if (merknader == null) {
            throw new IllegalArgumentException("Merknader kan ikke være null. Bruk Optional om du har behov for å uttrykke ingen merknader.");
        }
        this.merknader = merknader;
    }

    boolean notEmpty() {
        return true;
    }

    public String asString() {
        return merknader;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Merknader that = (Merknader) o;

        return merknader.equals(that.merknader);
    }

    @Override
    public int hashCode() {
        return merknader.hashCode();
    }

    public enum Merknad {
        IKKE_KAPASITET(5),
        DISKRIMINERENDE(4),
        DUPLIKAT(3),
        IKKE_ANSATT(2),
        IKKE_GODKJENT(1);

        private final int kodeverdi;

        public String getKodeAsString() {
            return String.valueOf(kodeverdi);
        }

        Merknad(int kodeverdi) {
            this.kodeverdi = kodeverdi;
        }
    }

    static NullMerknader nullMerknader() {
        return new NullMerknader();
    }

    /**
     * Null object for Merknader.
     */
    static class NullMerknader extends Merknader {

        private NullMerknader() {
            super("");
        }

        @Override
        boolean notEmpty() {
            return false;
        }

        @Override
        public String asString() {
            return null;
        }
    }
}
