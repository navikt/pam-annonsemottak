package no.nav.pam.annonsemottak.receivers.xmlstilling;

import no.nav.pam.annonsemottak.stilling.StillingRepository;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class XmlStillingServiceTest {

    private XmlStillingService service;

    private XmlStillingConnector connector = mock(XmlStillingConnector.class);

    private XmlStillingExternalRun externalRun = mock(XmlStillingExternalRun.class);

    private XmlStillingMetrics metrics = mock(XmlStillingMetrics.class);

    private StillingRepository stillingRepository = mock(StillingRepository.class);


    @Before
    public void setUp() {
        service = new XmlStillingService(connector, externalRun, stillingRepository, metrics);

        when(externalRun.decorate(any())).thenReturn(new Stillinger(null, null));
    }

    @Test
    public void that_that_external_run_is_fetched_and_updated() {

        service.updateLatest(true);

        verify(externalRun).decorate(any());
    }

}
