package no.nav.pam.annonsemottak.receivers.xmlstilling;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import static no.nav.pam.annonsemottak.receivers.xmlstilling.Stillinger.Gruppe.*;
import static org.mockito.Mockito.*;

public class XmlStillingMetricsTest {

    private XmlStillingMetrics metrics;

    private MeterRegistry meterRegistry = mock(MeterRegistry.class);

    @Before
    public void setUp() {
        metrics = new XmlStillingMetrics(meterRegistry);
    }

    @Test
    public void registrer_metrics() {
        Stillinger stillinger = mock(Stillinger.class);

        when(stillinger.size(eq(CHANGED))).thenReturn(5);
        when(stillinger.size(eq(NEW))).thenReturn(10);
        when(stillinger.size(eq(UNCHANGED))).thenReturn(15);

        when(meterRegistry.counter(anyString(), any(String.class)))
                .thenReturn(mock(Counter.class));

        metrics.registerFor(stillinger);

        // TODO burde også sjekke at rett verdi registreres på rett counter, men det er litt kinkig sånn som meterRegistry er bygd opp
        verify(meterRegistry, times(4)).counter(anyString(), any(String.class));
    }

}
