package no.nav.pam.annonsemottak.receivers.amedia;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.pam.annonsemottak.receivers.HttpClientProvider;
import no.nav.pam.annonsemottak.stilling.Stilling;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.inject.Named;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Kaller amedia sitt Api
 */
@Component
public class AmediaConnector {

    private static final Logger LOG = LoggerFactory.getLogger(AmediaConnector.class);

    private final ObjectMapper objectMapper;
    private final AmediaUrl url;
    private final String apiKey;
    private final HttpClientProvider clientProvider;

    public AmediaConnector(
            final HttpClientProvider clientProvider,
            final AmediaUrl url,
            @Value("${amedia.apikey}") final String apiKey,
            final ObjectMapper jacksonMapper) {
        this.objectMapper = jacksonMapper;
        this.clientProvider = clientProvider;
        this.apiKey = apiKey;
        this.url = url;
    }

    List<String> fetchAllEksternId() {
        try {
            Response response = call(url.all());
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code " + response.code());
            }
            return AmediaResponseMapper.mapEksternIder(objectMapper.readValue(response.body().charStream(), JsonNode.class));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    List<Stilling> hentData(final LocalDateTime sisteModifiserteDato) {
        try {
            Response response = call(url.modifiedAfter(sisteModifiserteDato));
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code " + response.code());
            }
            return AmediaResponseMapper.mapResponse(objectMapper.readValue(response.body().charStream(), JsonNode.class));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public boolean isPingSuccessful() {
        try {
            return call(url.ping()).isSuccessful();
        } catch (IOException e) {
            LOG.error("Error while pinging connection to Amedia", e);
            return false;
        }
    }

    private OkHttpClient client() {
        return clientProvider.get();
    }

    private Response call(final String url)
            throws IOException {
        Request request = request(url);
        LOG.debug("{}", request);
        return client().newCall(request).execute();
    }

    private Request request(final String url) {
        return new Request.Builder().url(url).addHeader("api_key", apiKey).build();
    }


}
