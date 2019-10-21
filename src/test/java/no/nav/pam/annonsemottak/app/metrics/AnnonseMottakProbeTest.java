package no.nav.pam.annonsemottak.app.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import org.junit.Ignore;
import org.junit.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AnnonseMottakProbeTest {

    private InfluxMetricReporter influxMetricReporter = mock(InfluxMetricReporter.class);
    private MeterRegistry meterRegistry = mock(MeterRegistry.class);

    private AnnonseMottakProbe annonseMottakProbe = new AnnonseMottakProbe(influxMetricReporter, meterRegistry);

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
        verifyZeroInteractions(meterRegistry);
    }

    @Test
    public void verify_that_changed_ad_events_are_registered_with_prometheus() {
        annonseMottakProbe.changedAdPoint(5L, "DEXI", "Molde kommune");
        verifyZeroInteractions(meterRegistry);
    }

    @Test
    public void verify_that_stopped_ad_events_are_registered_with_prometheus() {
        annonseMottakProbe.stoppedAdPoint(5L, "DEXI", "Molde kommune");
        verifyZeroInteractions(meterRegistry);
    }

    @Test
    public void verify_that_duplicated_ad_events_are_registered_with_prometheus() {
        annonseMottakProbe.duplicateAdPoint(5L, "DEXI", "Molde kommune");
        verifyZeroInteractions(meterRegistry);
    }


}
