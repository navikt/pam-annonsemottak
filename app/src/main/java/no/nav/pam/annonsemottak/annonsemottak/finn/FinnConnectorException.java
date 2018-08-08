package no.nav.pam.annonsemottak.annonsemottak.finn;

class FinnConnectorException extends Exception {

    FinnConnectorException(Exception cause) {
        super(cause);
    }

    FinnConnectorException(String message) {
        super(message);
    }

}
