package no.nav.pam.annonsemottak.receivers.polaris;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.pam.annonsemottak.receivers.HttpClientProvider;
import no.nav.pam.annonsemottak.receivers.polaris.model.PolarisAd;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.inject.Named;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;

@Component
public class PolarisConnector {
    private static final Logger LOG = LoggerFactory.getLogger(PolarisConnector.class);


    private final ObjectMapper objectMapper;
    private final String apiEndpoint;
    private final HttpClientProvider clientProvider;
    private final String basicAuth;

    @Autowired
    public PolarisConnector(
            final HttpClientProvider clientProvider,
            @Value("${polaris.url}") final String apiEndpoint,
            @Value("${polaris.user}") final String user,
            @Value("${polaris.password}") final String password,
            final ObjectMapper jacksonMapper) {
        this.clientProvider = clientProvider;
        this.apiEndpoint = apiEndpoint;
        this.objectMapper = jacksonMapper;
        String userpluspass = user+":"+password;
        this.basicAuth = Base64.getEncoder().encodeToString(userpluspass.getBytes());
    }

    public boolean isPingSuccessful() {
        try {
            Response response = clientProvider.get().newCall(createRequest(apiEndpoint)).execute();

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
        Response response = clientProvider.get().newCall(request).execute();

        if (!response.isSuccessful()) {
            throw new IOException("Unexpected response code " + response.code());
        }

        return objectMapper.readValue(response.body().charStream(), new TypeReference<List<PolarisAd>>() {
        });
    }

    private Request createRequest(String url) {

        return new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Basic "+basicAuth)
                .build();
    }
}
