package no.nav.pam.annonsemottak.annonsemottak.amedia;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.pam.annonsemottak.annonsemottak.HttpClientProxy;
import okhttp3.Request;
import okhttp3.Response;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Kaller amedia sitt Api
 */
public class AmediaConnector {

    private static final Logger LOG = LoggerFactory.getLogger(AmediaConnector.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final String apiEndpoint;
    private final HttpClientProxy proxy;

    public AmediaConnector(HttpClientProxy proxy, String amediaEndpoint) {
        this.proxy = proxy;
        this.apiEndpoint = amediaEndpoint;
    }

    JsonNode hentData(DateTime sistModifisert, boolean medDetaljer, int resultSize) {
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


}
