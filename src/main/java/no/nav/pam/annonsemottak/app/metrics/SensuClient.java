package no.nav.pam.annonsemottak.app.metrics;

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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
class SensuClient {

    private static final Logger LOG = LoggerFactory.getLogger(AnnonseMottakProbe.class);

    private final String hostname;
    private final int port;

    @Inject
    SensuClient(@Value("${sensu.host}") String hostname, @Value("${sensu.port}") int port) {

        this.hostname = hostname;
        this.port = port;
    }

    static class SensuEvent {

        private final String json;

        SensuEvent(String sensuName, String output) {
            json =
                    "{" +
                            "\"name\":\"" + sensuName + "\"," +
                            "\"type\":\"metric\"," +
                            "\"handlers\":[\"events_nano\"]," +
                            "\"output\":\"" + output + "\"," +
                            "\"status\":0" +
                            "}";
        }

        String getJson() {
            return json;
        }
    }

    void write(SensuEvent data) {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            long startTime = System.currentTimeMillis();
            try (Socket socket = new Socket(hostname, port)) {

                try (OutputStreamWriter writer = new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8)) {
                    writer.write(data.getJson(), 0, data.getJson().length());
                    writer.flush();
                    LOG.debug("wrote {} bytes of data", data.getJson().length());
                } catch (IOException e) {
                    LOG.error("Unable to send event {}", data, e);
                }

            } catch (UnknownHostException e) {
                LOG.error("Unknow host: {}:{} {}", hostname, port, e.getMessage());
            } catch (IOException e) {
                LOG.error("Unable to send event to {}:{}", hostname, port, e);
            } catch (Exception e) {
                LOG.error("Unable to send event", e);
            } finally {
                LOG.info("Influx/sensu reporting time: {}", System.currentTimeMillis() - startTime);
            }
        });

    }

}