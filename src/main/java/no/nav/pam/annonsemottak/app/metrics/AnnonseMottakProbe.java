package no.nav.pam.annonsemottak.app.metrics;

import org.influxdb.dto.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class AnnonseMottakProbe {

    private static final Logger LOG = LoggerFactory.getLogger(AnnonseMottakProbe.class);

    private final String hostname;
    private final int port;

    public void duplicateAdPoint(Long count, String kilde, String medium) {
        sendPoint(MetricNames.ADS_COLLECTED_DUPLICATED,
                Map.of("counter", count),
                Map.of("source", kilde, "origin", medium));
    }
    public void newAdPoint(Long count, String kilde, String medium) {
        sendPoint(MetricNames.ADS_COLLECTED_NEW,
                Map.of("counter", count),
                Map.of("source", kilde, "origin", medium));
    }
    public void stoppedAdPoint(Long count, String kilde, String medium) {
        sendPoint(MetricNames.ADS_COLLECTED_STOPPED,
                Map.of("counter", count),
                Map.of("source", kilde, "origin", medium));
    }
    public void changedAdPoint(Long count, String kilde, String medium) {
        sendPoint(MetricNames.ADS_COLLECTED_CHANGED,
                Map.of("counter", count),
                Map.of("source", kilde, "origin", medium));
    }


    @Inject
    AnnonseMottakProbe(@Value("SENSU_HOST") String hostname, @Value("SENSU_PORT") int port) {

        this.hostname = hostname;
        this.port = port;
    }

    public void sendPoint(String measurement, Map<String, Object> fields, Map<String, String> tags) {

        Point point = Point.measurement(measurement)
                .tag(tags)
                .fields(fields)
                .build();

        writeToSensu(hostname, port, point.lineProtocol());
    }

    private static void writeToSensu(String hostname, int port, String data) {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {

            try (Socket socket = new Socket(hostname, port)) {

                try (OutputStreamWriter writer = new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8)) {
                    writer.write(data, 0, data.length());
                    writer.flush();
                    LOG.debug("wrote {} bytes of data", data.length());
                } catch (IOException e) {
                    LOG.error("Unable to send event {}", data, e);
                }

            } catch (UnknownHostException e) {
                LOG.error("Unknow host: {}:{} {}", hostname, port, e.getMessage());
            } catch (IOException e) {
                LOG.error("Unable to send event to {}:{}", hostname, port, e);
            } catch (Exception e) {
                LOG.error("Unable to send event", e);
            }
        });

    }

}
