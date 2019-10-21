package no.nav.pam.annonsemottak.app.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Map;

@Service
public class AnnonseMottakProbe {

    private final InfluxMetricReporter influxMetricReporter;
    private final MeterRegistry meterRegistry;

    @Inject
    AnnonseMottakProbe(InfluxMetricReporter influxMetricReporter, MeterRegistry meterRegistry) {
        this.influxMetricReporter = influxMetricReporter;
        this.meterRegistry = meterRegistry;
    }

    public void duplicateAdPoint(Long count, String kilde, String medium) {
        influxMetricReporter.registerPoint(MetricNames.ADS_COLLECTED_DUPLICATED,
                Map.of("counter", count),
                Map.of("source", kilde, "origin", medium));
    }
    public void newAdPoint(Long count, String kilde, String medium) {
        influxMetricReporter.registerPoint(MetricNames.ADS_COLLECTED_NEW,
                Map.of("counter", count),
                Map.of("source", kilde, "origin", medium));
    }
    public void stoppedAdPoint(Long count, String kilde, String medium) {
        influxMetricReporter.registerPoint(MetricNames.ADS_COLLECTED_STOPPED,
                Map.of("counter", count),
                Map.of("source", kilde, "origin", medium));
    }
    public void changedAdPoint(Long count, String kilde, String medium) {
        influxMetricReporter.registerPoint(MetricNames.ADS_COLLECTED_CHANGED,
                Map.of("counter", count),
                Map.of("source", kilde, "origin", medium));
    }

}


