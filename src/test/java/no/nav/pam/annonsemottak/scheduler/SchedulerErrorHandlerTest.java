package no.nav.pam.annonsemottak.scheduler;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SchedulerErrorHandlerTest {

    @Test
    void skalLoggeStacktrace() {
        SchedulerErrorHandler handler = new SchedulerErrorHandler();
        handler.handleError(new Throwable("Simulert feil"));
    }

    @Test
    void skalHaandtereAtDenFaarInnNull() {
        SchedulerErrorHandler handler = new SchedulerErrorHandler();
        assertDoesNotThrow(() -> handler.handleError(null));
    }

}
