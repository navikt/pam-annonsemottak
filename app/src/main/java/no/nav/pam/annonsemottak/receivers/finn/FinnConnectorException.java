package no.nav.pam.annonsemottak.receivers.finn;

class FinnConnectorException extends Exception {

    FinnConnectorException(Exception cause) {
        super(cause);
    }

    FinnConnectorException(String message) {
        super(message);
    }

}
