package no.nav.pam.annonsemottak.receivers.dexi;

public class DexiException extends Exception {

    private final String currentRobotName;

    DexiException(String currentRobotName, String message) {
        super(message);
        this.currentRobotName = currentRobotName;
    }

    DexiException(String currentRobotName, Exception e) {
        super(e);
        this.currentRobotName = currentRobotName;
    }

    public String getCurrentRobotName() {
        return currentRobotName;
    }
}
