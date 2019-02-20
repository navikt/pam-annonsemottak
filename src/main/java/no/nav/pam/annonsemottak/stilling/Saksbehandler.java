package no.nav.pam.annonsemottak.stilling;

import java.io.Serializable;
import java.util.Optional;

public class Saksbehandler implements Serializable {

    private final String saksbehandler;

    public static Optional<Saksbehandler> ofNullable(String saksbehandler) {
        return saksbehandler != null ? Optional.of(new Saksbehandler(saksbehandler)) : Optional.empty();
    }

    Saksbehandler(String saksbehandler) {
        if (saksbehandler == null) {
            throw new IllegalArgumentException("Saksbehandler kan ikke være null. Bruk Optional om du har behov for å uttrykke ingen/blank saksbehandler.");
        }
        this.saksbehandler = saksbehandler;
    }

    public String asString() {
        return saksbehandler;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Saksbehandler that = (Saksbehandler) o;

        return saksbehandler.equals(that.saksbehandler);
    }

    @Override
    public int hashCode() {
        return saksbehandler.hashCode();
    }

    static NullSaksbehandler nullSaksbehandler() {
        return new NullSaksbehandler();
    }

    /**
     * Null object for Saksbehandler.
     */
    static class NullSaksbehandler extends Saksbehandler {

        private NullSaksbehandler() {
            super("");
        }

        @Override
        public String asString() {
            return null;
        }
    }
}
