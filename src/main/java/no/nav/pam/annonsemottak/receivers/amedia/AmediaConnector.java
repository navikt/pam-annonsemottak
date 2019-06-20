package no.nav.pam.annonsemottak.receivers.amedia;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.pam.annonsemottak.receivers.HttpClientProvider;
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

/**
 * Kaller amedia sitt Api
 */
@Component
public class AmediaConnector {

    private static final Logger LOG = LoggerFactory.getLogger(AmediaConnector.class);

    private final ObjectMapper objectMapper;
    private final String amediaUrl;
    private final HttpClientProvider clientProvider;

    public AmediaConnector(
            @Named("proxyHttpClient") final HttpClientProvider clientProvider,
            @Value("${amedia.url}") final String amediaUrl,
            final ObjectMapper jacksonMapper) {
        this.objectMapper = jacksonMapper;
        this.clientProvider = clientProvider;
        this.amediaUrl = amediaUrl;
    }

    //JsonNode hentData(LocalDateTime sistModifisert, boolean medDetaljer, int resultSize) {
    JsonNode hentData(final AmediaRequestParametere requestParametere) {
        try {
            String url = urlFrom(requestParametere);
            Response response = call(url);
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code " + response.code());
            }
            return objectMapper.readValue(response.body().charStream(), JsonNode.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isPingSuccessful() {
        try {
            Response response = call(pingUrl());
            return response.isSuccessful();
        } catch (IOException e) {
            LOG.error("Error while pinging connection to Amedia", e);
            return false;
        }
    }

    private OkHttpClient client() {
        return clientProvider.get();
    }

    private String urlFrom(final AmediaRequestParametere amediaRequestParametere) {
        return amediaUrl + amediaRequestParametere.asString();
    }

    private String pingUrl() {
        return urlFrom(AmediaRequestParametere.PING);
    }

    private Response call(final String url)
            throws IOException {
        Request request = request(url);
        LOG.debug("{}", request);
        return client().newCall(request).execute();
    }

    private Request request(final String url) {
        return new Request.Builder().url(url).build();
    }


}
