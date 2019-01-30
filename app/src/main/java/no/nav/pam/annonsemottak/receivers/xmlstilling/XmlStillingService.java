package no.nav.pam.annonsemottak.receivers.xmlstilling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class XmlStillingService {

    private static final Logger log = LoggerFactory.getLogger(XmlStillingService.class);
    private final XmlStillingConnector connector;
    private final XmlStillingExternalRun xmlStillingExternalRun;

    @Inject
    public XmlStillingService(
            final XmlStillingConnector connector,
            XmlStillingExternalRun xmlStillingExternalRun) {

        this.connector = connector;
        this.xmlStillingExternalRun = xmlStillingExternalRun;
    }

    public Stillinger updateLatest() {

        Stillinger stillinger = connector.fetchStillinger(xmlStillingExternalRun.lastRunSupplier().get());

        log.debug("Fikk {} stillinger fra pam-xml-stilling. Siste stilling mottat {} ", stillinger.asList().size(), stillinger.latestDate());

        stillinger.latestDate().ifPresent(xmlStillingExternalRun::updateLatest);

        return stillinger;

    }


}
