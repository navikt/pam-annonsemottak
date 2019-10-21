package no.nav.pam.annonsemottak.app.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Map;

import static no.nav.pam.annonsemottak.app.metrics.MetricNames.*;

@Service
public class AnnonseMottakProbe {

    private final InfluxMetricReporter influxMetricReporter;
    private final MeterRegistry meterRegistry;

    @Inject
    AnnonseMottakProbe(InfluxMetricReporter influxMetricReporter, MeterRegistry meterRegistry) {
        this.influxMetricReporter = influxMetricReporter;
        this.meterRegistry = meterRegistry;
    }

    public void addMetricsCounters(String kilde, String medium, int newSize, int stopSize, int dupSize, int modifySize) {

        newAdPoint((long)newSize, kilde, medium);
        stoppedAdPoint((long)stopSize, kilde, medium);
        duplicateAdPoint((long)dupSize, kilde, medium);
        changedAdPoint((long)modifySize, kilde, medium);
    }

    public void duplicateAdPoint(Long count, String kilde, String medium) {
        influxMetricReporter.registerPoint(MetricNames.ADS_COLLECTED_DUPLICATED,
                Map.of("counter", count),
                Map.of("source", kilde, "origin", medium));

        meterRegistry.counter(ADS_COLLECTED_DUPLICATED, "source", kilde, "origin", medium).increment(count);
    }
    public void newAdPoint(Long count, String kilde, String medium) {
        influxMetricReporter.registerPoint(MetricNames.ADS_COLLECTED_NEW,
                Map.of("counter", count),
                Map.of("source", kilde, "origin", medium));

        meterRegistry.counter(ADS_COLLECTED_NEW, "source", kilde, "origin", medium).increment(count);
    }
    public void stoppedAdPoint(Long count, String kilde, String medium) {
        influxMetricReporter.registerPoint(MetricNames.ADS_COLLECTED_STOPPED,
                Map.of("counter", count),
                Map.of("source", kilde, "origin", medium));

        meterRegistry.counter(ADS_COLLECTED_STOPPED, "source", kilde, "origin", medium).increment(count);
    }
    public void changedAdPoint(Long count, String kilde, String medium) {
        influxMetricReporter.registerPoint(MetricNames.ADS_COLLECTED_CHANGED,
                Map.of("counter", count),
                Map.of("source", kilde, "origin", medium));

        meterRegistry.counter(ADS_COLLECTED_CHANGED, "source", kilde, "origin", medium).increment(count);
    }

}


