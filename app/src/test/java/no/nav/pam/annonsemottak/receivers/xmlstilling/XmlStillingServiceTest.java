package no.nav.pam.annonsemottak.receivers.xmlstilling;

import no.nav.pam.annonsemottak.stilling.Stilling;
import no.nav.pam.annonsemottak.stilling.StillingTestdataBuilder;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class XmlStillingServiceTest {

    private static final LocalDateTime JAN_28_2019 = LocalDateTime.of(2019, 1, 28, 18, 4, 5);

    private XmlStillingService service;

    private XmlStillingConnector connector = mock(XmlStillingConnector.class);

    private ExternalRunFacade externalRun = mock(ExternalRunFacade.class);

    private XmlStillingMetrics metrics = mock(XmlStillingMetrics.class);

    private StillingRepositoryFacade stillingRepository = mock(StillingRepositoryFacade.class);


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

    @Test
    public void update_callback() {

        Stillinger result = new Stillinger(null, null);

        List<Stilling> stillinger = Arrays.asList(
                StillingTestdataBuilder.enkelStilling().build(),
                StillingTestdataBuilder.enkelStilling().build());

        when(connector.fetchFrom(JAN_28_2019)).thenReturn(stillinger);
        when(stillingRepository.updateStillinger(any(), any())).thenReturn(result);

        Function<LocalDateTime, Stillinger> updateCallback = service.update(true);

        assertThat(updateCallback.apply(JAN_28_2019)).isEqualTo(result);

        verify(stillingRepository).updateStillinger(eq(stillinger), any());;


    }

}
