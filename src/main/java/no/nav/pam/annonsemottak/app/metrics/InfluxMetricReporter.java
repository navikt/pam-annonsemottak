package no.nav.pam.annonsemottak.app.metrics;

import org.influxdb.dto.Point;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
class InfluxMetricReporter {

    private final SensuClient sensuClient;

    InfluxMetricReporter(SensuClient sensuClient) {
        this.sensuClient = sensuClient;
    }

    void registerPoint(String measurement, Map<String, Object> fields, Map<String, String> tags) {

        Point point = Point.measurement(measurement)
                .time(TimeUnit.MILLISECONDS.toNanos(System.currentTimeMillis()), TimeUnit.NANOSECONDS)
                .tag(tags)
                .tag(DEFAULT_TAGS)
                .fields(fields)
                .build();

        sensuClient.write(createSensuEvent(point.lineProtocol()));
    }

    private static final Map<String, String> DEFAULT_TAGS = Map.of(
            "application", getenv("NAIS_APP_NAME", "pam-annonsemottak"),
            "cluster", getenv("NAIS_CLUSTER_NAME", "dev-fss"),
            "namespace", getenv("NAIS_NAMESPACE", "default")
    );

    private static String getenv(String env, String defaultValue) {
        return System.getenv(env) != null ? System.getenv(env) : defaultValue;
    }

    static SensuClient.SensuEvent createSensuEvent(String output) {
        return new SensuClient.SensuEvent("annonsemottak-events", output);
    }
}
