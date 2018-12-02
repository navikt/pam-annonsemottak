package no.nav.pam.annonsemottak.stilling;

import java.io.Serializable;
import java.util.Optional;

public class Kommentarer implements Serializable {

    private final String kommentarer;

    static Optional<Kommentarer> ofNullable(String kommentarer) {
        return kommentarer != null ? Optional.of(new Kommentarer(kommentarer)) : empty();
    }

    private static Optional<Kommentarer> empty() {
        return Optional.empty();
    }

    Kommentarer(String kommentarer) {
        if (kommentarer == null) {
            throw new IllegalArgumentException("Kommentarer kan ikke være null. Bruk Optional om du har behov for å uttrykke ingen/blank kommentar.");
        }
        this.kommentarer = kommentarer;
    }

    public String asString() {
        return kommentarer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Kommentarer that = (Kommentarer) o;

        return kommentarer.equals(that.kommentarer);
    }

    @Override
    public int hashCode() {
        return kommentarer.hashCode();
    }

    static NullKommentarer nullKommentarer() {
        return new NullKommentarer();
    }

    /**
     * Null object for Kommentarer.
     */
    static class NullKommentarer extends Kommentarer {

        private NullKommentarer() {
            super("");
        }

        @Override
        public String asString() {
            return null;
        }
    }
}
