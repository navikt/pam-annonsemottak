package no.nav.pam.annonsemottak.app.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AnnonseMottakProbeTest {

    private InfluxMetricReporter influxMetricReporter = mock(InfluxMetricReporter.class);
    private MeterRegistry meterRegistry = mock(MeterRegistry.class);

    private AnnonseMottakProbe annonseMottakProbe = new AnnonseMottakProbe(influxMetricReporter, meterRegistry);

    @Before
    public void setUp() {
        when(meterRegistry.counter(anyString(), any(String.class)))
                .thenReturn(mock(Counter.class));
    }

    @Test
    public void verify_that_new_ad_events_are_registered_with_influx() {
        annonseMottakProbe.newAdPoint(5L, "DEXI", "Molde kommune");
        verify(influxMetricReporter).registerPoint(any(),any(),any());
    }

    @Test
    public void verify_that_changed_ad_events_are_registered_with_influx() {
        annonseMottakProbe.changedAdPoint(5L, "DEXI", "Molde kommune");
        verify(influxMetricReporter).registerPoint(any(),any(),any());
    }

    @Test
    public void verify_that_stopped_ad_events_are_registered_with_influx() {
        annonseMottakProbe.stoppedAdPoint(5L, "DEXI", "Molde kommune");
        verify(influxMetricReporter).registerPoint(any(),any(),any());
    }

    @Test
    public void verify_that_duplicated_ad_events_are_registered_with_influx() {
        annonseMottakProbe.duplicateAdPoint(5L, "DEXI", "Molde kommune");
        verify(influxMetricReporter).registerPoint(any(),any(),any());
    }


    @Test
    public void verify_that_new_ad_events_are_registered_with_prometheus() {
        annonseMottakProbe.newAdPoint(5L, "DEXI", "Molde kommune");
        verify(meterRegistry).counter(anyString(), ArgumentMatchers.<String>any());
    }

    @Test
    public void verify_that_changed_ad_events_are_registered_with_prometheus() {
        annonseMottakProbe.changedAdPoint(5L, "DEXI", "Molde kommune");
        verify(meterRegistry).counter(anyString(), ArgumentMatchers.<String>any());
    }

    @Test
    public void verify_that_stopped_ad_events_are_registered_with_prometheus() {
        annonseMottakProbe.stoppedAdPoint(5L, "DEXI", "Molde kommune");
        verify(meterRegistry).counter(anyString(), ArgumentMatchers.<String>any());
    }

    @Test
    public void verify_that_duplicated_ad_events_are_registered_with_prometheus() {
        annonseMottakProbe.duplicateAdPoint(5L, "DEXI", "Molde kommune");
        verify(meterRegistry).counter(anyString(), ArgumentMatchers.<String>any());
    }


    @Test
    public void verify_that_counter_metrics_are_registered() {
        annonseMottakProbe.addMetricsCounters(
                "XML_STILLING",
                "XML_STILLING",
                10,
                0,
                0,
                5);

        verify(meterRegistry, times(4)).counter(anyString(), any(String.class));
        verify(influxMetricReporter, times(4)).registerPoint(any(), any(), any());
    }


}
