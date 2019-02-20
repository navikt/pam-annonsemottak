package no.nav.pam.annonsemottak.stilling;

/**
 * Client is trying an update that breaks business logic.
 */
public class IllegalSaksbehandlingCommandException extends Exception {

    IllegalSaksbehandlingCommandException(String message) {
        super(message);
    }

}
