package no.nav.pam.annonsemottak.receivers.xmlstilling;

import no.nav.pam.annonsemottak.stilling.StillingTestdataBuilder;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static java.util.Collections.singletonList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class XmlStillingServiceTest {

    private XmlStillingService service;

    private XmlStillingConnector connector = mock(XmlStillingConnector.class);

    private XmlStillingExternalRun externalRun = mock(XmlStillingExternalRun.class);


    @Before
    public void setUp() {
        service = new XmlStillingService(connector, externalRun);
    }

    @Test
    public void that_that_external_run_is_fetched_and_updated() {
        LocalDateTime lastUpdate = LocalDateTime.of(2019, 1, 11, 14, 51, 11);

        when(externalRun.lastRunSupplier()).thenReturn(() -> LocalDateTime.now().withYear(2018));
        when(connector.fetchStillinger(any())).thenReturn(new Stillinger(
                singletonList(StillingTestdataBuilder.enkelStilling().systemModifiedDate(lastUpdate).build())));

        service.updateLatest();

        verify(externalRun).lastRunSupplier();
        verify(externalRun).updateLatest(eq(lastUpdate));
    }
}
