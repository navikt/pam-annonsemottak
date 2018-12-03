package no.nav.pam.annonsemottak.receivers.polaris;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.pam.annonsemottak.receivers.HttpClientProxy;
import no.nav.pam.annonsemottak.receivers.polaris.model.PolarisAd;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class PolarisConnector {
    private static final Logger LOG = LoggerFactory.getLogger(PolarisConnector.class);


    private ObjectMapper objectMapper;
    private final String apiEndpoint;
    private final HttpClientProxy proxy;

    public PolarisConnector(HttpClientProxy proxy, String apiEndpoint, ObjectMapper objectMapper) {
        this.proxy = proxy;
        this.apiEndpoint = apiEndpoint;
        this.objectMapper = objectMapper;
    }

    public boolean isPingSuccessful() {
        try {
            Response response = proxy.getHttpClient().newCall(createRequest(apiEndpoint)).execute();

            return response.isSuccessful();
        } catch (Exception e) {
            return false;
        }
    }

    public List<PolarisAd> fetchData(LocalDateTime lastUpdated) throws IOException {
        String url = apiEndpoint + "?datetime=" + lastUpdated.format(DateTimeFormatter.ISO_DATE_TIME);

        return executeRequest(createRequest(url));
    }

    private List<PolarisAd> executeRequest(Request request) throws IOException {
        LOG.debug("{}", request);
        Response response = proxy.getHttpClient().newCall(request).execute();

        if (!response.isSuccessful()) {
            throw new IOException("Unexpected response code " + response.code());
        }

        return objectMapper.readValue(response.body().charStream(), new TypeReference<List<PolarisAd>>() {
        });
    }

    private Request createRequest(String url) {
        return new Request.Builder()
                .url(url)
                .build();
    }
}
