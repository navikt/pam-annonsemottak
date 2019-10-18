package no.nav.pam.annonsemottak.app.metrics;

import org.influxdb.dto.Point;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class AnnonseMottakProbe {

    private final SensuClient sensuClient;

    @Inject
    AnnonseMottakProbe(SensuClient sensuClient) {
        this.sensuClient = sensuClient;
    }

    public void duplicateAdPoint(Long count, String kilde, String medium) {
        influxPoint(MetricNames.ADS_COLLECTED_DUPLICATED,
                Map.of("counter", count),
                Map.of("source", kilde, "origin", medium));
    }
    public void newAdPoint(Long count, String kilde, String medium) {
        influxPoint(MetricNames.ADS_COLLECTED_NEW,
                Map.of("counter", count),
                Map.of("source", kilde, "origin", medium));
    }
    public void stoppedAdPoint(Long count, String kilde, String medium) {
        influxPoint(MetricNames.ADS_COLLECTED_STOPPED,
                Map.of("counter", count),
                Map.of("source", kilde, "origin", medium));
    }
    public void changedAdPoint(Long count, String kilde, String medium) {
        influxPoint(MetricNames.ADS_COLLECTED_CHANGED,
                Map.of("counter", count),
                Map.of("source", kilde, "origin", medium));
    }

    private static final Map<String, String> DEFAULT_TAGS = Map.of(
            "application", getenv("NAIS_APP_NAME", "pam-annonsemottak"),
            "cluster", getenv("NAIS_CLUSTER_NAME", "dev-fss"),
            "namespace", getenv("NAIS_NAMESPACE", "default")
    );

    private static String getenv(String env, String defaultValue) {
        return System.getenv(env) != null ? System.getenv(env) : defaultValue;
    }


    private void influxPoint(String measurement, Map<String, Object> fields, Map<String, String> tags) {

        Point point = Point.measurement(measurement)
                .time(TimeUnit.MILLISECONDS.toNanos(System.currentTimeMillis()), TimeUnit.NANOSECONDS)
                .tag(tags)
                .tag(DEFAULT_TAGS)
                .fields(fields)
                .build();

        sensuClient.write(new SensuClient.SensuEvent("annonsemottak-events", point.lineProtocol()));
    }


}
