package no.nav.pam.annonsemottak.receivers.amedia;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.pam.annonsemottak.receivers.HttpClientProxy;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Kaller amedia sitt Api
 */
public class AmediaConnector {

    private static final Logger LOG = LoggerFactory.getLogger(AmediaConnector.class);

    private final ObjectMapper objectMapper;
    private final String apiEndpoint;
    private final HttpClientProxy proxy;

    public AmediaConnector(HttpClientProxy proxy, String amediaEndpoint, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.proxy = proxy;
        this.apiEndpoint = amediaEndpoint;
    }

    JsonNode hentData(LocalDateTime sistModifisert, boolean medDetaljer, int resultSize) {
        try {
            return executeRequest(createRequest(
                    apiEndpoint + new AmediaRequestParametere(sistModifisert, medDetaljer, resultSize)
                            .asString()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private JsonNode executeRequest(Request request)
            throws IOException {
        LOG.debug("{}", request);
        Response response = proxy.getHttpClient().newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected response code " + response.code());
        }
        return objectMapper.readValue(response.body().charStream(), JsonNode.class);
    }

    private Request createRequest(String url) {
        return new Request.Builder()
                .url(url)
                .build();
    }

    public boolean isPingSuccessful() {
        try {
            AmediaRequestParametere params = new AmediaRequestParametere(LocalDateTime.now(), false, 1);
            Response response = proxy.getHttpClient().newCall(createRequest(apiEndpoint + params.asString())).execute();
            return response.isSuccessful();
        } catch (IOException e) {
            LOG.error("Error while pinging connection to Amedia", e);
            return false;
        }
    }

}
