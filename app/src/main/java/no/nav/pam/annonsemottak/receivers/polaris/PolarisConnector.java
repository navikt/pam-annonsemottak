package no.nav.pam.annonsemottak.receivers.polaris;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.pam.annonsemottak.receivers.HttpClientProxy;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.IOException;
import java.time.LocalDateTime;

@Configurable
public class PolarisConnector {
    private static final Logger LOG = LoggerFactory.getLogger(PolarisConnector.class);

    @Qualifier("jacksonMapper")
    @Autowired
    private ObjectMapper objectMapper;

    private final String apiEndpoint;
    private final HttpClientProxy proxy;

    public PolarisConnector(HttpClientProxy proxy, String apiEndpoint) {
        this.proxy = proxy;
        this.apiEndpoint = apiEndpoint;
    }


    public JsonNode fetchData(LocalDateTime lastUpdated) {
        try {
            return executeRequest(createRequest(apiEndpoint));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private JsonNode executeRequest(Request request) throws IOException {
        LOG.debug("{}", request);
        Response response = proxy.getHttpClient().newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected response code " + response.code());
        }

        //TODO: remove this later
        if (response.isSuccessful()) {
            LOG.info("pinged polaris ok");
        }

        return objectMapper.readValue(response.body().charStream(), JsonNode.class);
    }

    private Request createRequest(String url) {
        return new Request.Builder()
                .url(url)
                .build();
    }
}
