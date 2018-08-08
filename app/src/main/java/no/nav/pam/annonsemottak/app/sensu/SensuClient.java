package no.nav.pam.annonsemottak.app.sensu;

import com.google.common.base.Joiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/**
 * SensuClient is used for sending event-based application metrics to a database (InfluxDB).
 * @see <a href="https://sensuapp.org/docs/0.23/overview/what-is-sensu.html">https://sensuapp.org/docs/0.23/overview/what-is-sensu.html</a>
 */
public class SensuClient {

    private static final Logger log = LoggerFactory.getLogger(SensuClient.class);

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void sendEvent(String measurement, Map tags, Map fields) {
        try {
            final String dataPoint = toLineProtocol(measurement, addDefaultTags(tags), fields);
            String sensuEvent = createSensuEvent(measurement, dataPoint);
            writeToSocket(sensuEvent, Integer.parseInt(System.getProperty("sensu_client_port", "3030")));
            log.debug("Sent event with output {} to InfluxDB via sensu-client", dataPoint);
        } catch (RuntimeException e) {
            log.error("Unable to send event to InfluxDB via sensu-client", e);
        }
    }

    private static Map<String, Object> addDefaultTags(Map<String, Object> tags) {
        Map<String, Object> map = new HashMap<>();
        map.putAll(tags);
        map.put("application", "pam-stilling");
        map.put("environment", System.getProperty("environment.name", "dev"));
        map.put("hostname", getHostname());
        return map;
    }

    static String createSensuEvent(String eventName, String output) {
        return "{\"name\":\"" + eventName + "\",\"type\":\"metric\",\"handlers\":[\"events\"],\"output\":\"" + output + "\"}";
    }

    static String toLineProtocol(String measurement, Map<String, Object> tags, Map<String, Object> fields) {
        return String.format("%s%s %s %d", measurement, tags != null ? "," + toCSV(tags) : "", transformFields(fields), System.currentTimeMillis() / 1000);
    }

    private static String transformFields(Map<String, Object> fields) {

        if (fields == null) {
            throw new RuntimeException("InfluxDB datapoint fields can't be null!");
        }

        String fieldsString = "";
        for (Map.Entry<String, Object> field : fields.entrySet()) {
            String key = field.getKey();
            Object value = field.getValue();
            if (value instanceof String) {
                fieldsString += "," + key + "=" + escape((String) value);
            } else {
                fieldsString += "," + key + "=" + value;
            }
        }
        return fieldsString.substring(1);

    }

    private static String escape(String string) {
        return "\\\"" + string + "\\\"";
    }

    private static String toCSV(Map<String, Object> map) {
        if (map != null) {
            return Joiner.on(",").withKeyValueSeparator("=").join(map);
        } else {
            return "";
        }
    }

    private static void writeToSocket(String data, int port) {
        try {
            try (Socket socket = new Socket("localhost", port)) {
                OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream(), "UTF-8");
                osw.write(data, 0, data.length());
                osw.flush();
                log.debug("Wrote {} to socket with port {}", data, port);
            }
        } catch (ConnectException e) {
            // for å slippe full stacktrace i enhetstester mm.
            log.error("Unable to connect to localhost:{} {}", port, e.getMessage());
        } catch (IOException e) {
            log.error("Unable to write data {} to socket with port {}", data, port, e);
        }
    }

    private static String getHostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            log.error("Unknown host " + e);
            return "Unknown host";
        }
    }

}
