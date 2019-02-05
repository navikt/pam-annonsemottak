package no.nav.pam.annonsemottak.receivers.xmlstilling;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import no.nav.pam.annonsemottak.receivers.HttpClientProxy;
import no.nav.pam.annonsemottak.stilling.Stilling;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
class XmlStillingConnector {

    private static final Logger log = LoggerFactory.getLogger(XmlStillingConnector.class);
    private static final TypeReference<List<XmlStillingDto>> JSON_TYPE = new TypeReference<List<XmlStillingDto>>() {
    };

    private final HttpClientProxy proxy;
    private final EndpointProvider uri;
    private final ObjectMapper objectMapper;

    @Inject
    public XmlStillingConnector(
            HttpClientProxy proxy,
            EndpointProvider uri
    ) {

        this.proxy = proxy;
        this.uri = uri;
        objectMapper = new ObjectMapper()
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    boolean isPingSuccessful() {
        try {
            Response response = proxy.getHttpClient().newCall(requestFor(uri.forPing())).execute();
            return response.isSuccessful();
        } catch (IOException e) {
            log.error("Error while pinging connection to xml-stilling", e);
            return false;
        }
    }

    List<Stilling> fetchFrom(LocalDateTime lastRun) {
        try {
            List<Stilling> stillinger = fetchData(lastRun);

            log.debug("Fikk {} stillinger fra pam-xml-stilling.", stillinger.size());

            return stillinger;

        } catch (IOException e) {
            throw new RuntimeException("Unexpected error while fetching stillinger from XmlStilling", e);
        }
    }

    private List<Stilling> fetchData(LocalDateTime lastRun) throws IOException {

        Request request = requestFor(uri.forFetchWithStartingId(lastRun));
        log.debug("{}", request);

        Response response = proxy.getHttpClient().newCall(request).execute();

        if (!response.isSuccessful()) {
            throw new RuntimeException("Unexpected response code " + response.code());
        }

        try (Reader body = response.body().charStream()) {
            return objectMapper.<List<XmlStillingDto>>readValue(body, JSON_TYPE).stream()
                    .map(XmlStillingMapper::fromDto)
                    .collect(toList());
        }

    }

    private Request requestFor(String endpoint) {
        return new Request.Builder()
                .url(HttpUrl.parse(endpoint))
                .build();
    }

}
