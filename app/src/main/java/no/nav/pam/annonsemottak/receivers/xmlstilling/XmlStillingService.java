package no.nav.pam.annonsemottak.receivers.xmlstilling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class XmlStillingService {

    private static final Logger log = LoggerFactory.getLogger(XmlStillingService.class);
    private final XmlStillingConnector connector;

    @Inject
    public XmlStillingService(
            final XmlStillingConnector connector) {

        this.connector = connector;
    }

    public void updateLatest() {
        connector.fetchStillinger(0);
    }


}
