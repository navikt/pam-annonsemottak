package no.nav.pam.annonsemottak.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ErrorHandler;

public class SchedulerErrorHandler implements ErrorHandler {

    private static final Logger LOG = LoggerFactory.getLogger(SchedulerErrorHandler.class);

    @Override
    public void handleError(Throwable t) {
        if(t != null) {
            String melding = t.getMessage();
            LOG.error("Fikk uventet feil i en jobb: '{}'.", melding, t);
        } else {
            LOG.error("Fikk uventet feil i en jobb.", t);
        }
    }
}
