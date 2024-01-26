package no.nav.pam.annonsemottak.receivers.finn;

import no.nav.pam.annonsemottak.receivers.HttpClientProvider;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

@Component
public class FinnConnector {

    private static final Logger LOG = LoggerFactory.getLogger(FinnConnector.class);

    private final HttpClientProvider clientProvider;
    private final String serviceDocumentUrl;
    private final String jobFullTimeUrl;
    private final String apiKey;
    private final int politeRequestDelayInMillis;

    public FinnConnector(
            final HttpClientProvider clientProvider,
            @Value("${finn.url}") final String serviceDocumentUrl,
            @Value("${finn.job-fulltime-url}") final String jobFullTimeUrl,
            @Value("${finn.api.password}") final String apiKey,
            @Value("${finn.polite.delay.millis:200}") final int politeRequestDelayInMillis) {
        this.clientProvider = clientProvider;
        this.serviceDocumentUrl = serviceDocumentUrl;
        this.jobFullTimeUrl = jobFullTimeUrl;
        this.apiKey = apiKey;
        this.politeRequestDelayInMillis = politeRequestDelayInMillis;
    }

    private Request createRequest(HttpUrl url) {
        return new Request.Builder()
                .url(url)
                .addHeader("x-FINN-apikey", apiKey)
                .build();
    }

    private void addPoliteDelayBetweenRequests() {
        try {
            Thread.sleep(politeRequestDelayInMillis);
        } catch (InterruptedException e) {
            // Ignored.
        }
    }

    public Set<FinnAdHead> fetchSearchResult()
            throws FinnConnectorException {
        try {
            // Default får man et paginert treff med 30 rader om gangen. Da virker det som det blir for mange treff og resultatet kuttes.
            // Med mer en 100 rader virker det som vi får alt, og 400 virker som et OK antall
            String initialUrl = jobFullTimeUrl + "?rows=400";
            HttpUrl httpUrl = HttpUrl.parse(initialUrl);
            return collectAdHeads(httpUrl);
        } catch (Exception e) {
            throw new FinnConnectorException(e);
        }
    }

    /**
     * From the search result collects all ad head entries, by going through all the pages of the result.
     */
    private Set<FinnAdHead> collectAdHeads(HttpUrl... initialUrls) throws
            IOException, ParserConfigurationException, SAXException {
        Set<FinnAdHead> collectedAdHeads = new HashSet<>();

        Deque<HttpUrl> pendingUrls = new ArrayDeque<>();
        for (HttpUrl url : initialUrls) {
            pendingUrls.push(url);
        }

        while (!pendingUrls.isEmpty()) {
            HttpUrl currentUrl = pendingUrls.pop();

            FinnSearchResultsHandler searchResultsHandler = new FinnSearchResultsHandler();
            try (Response response = executeRequest(createRequest(currentUrl))) {
                parseReaderWithHandler(response.body().charStream(), searchResultsHandler);
            }
            collectedAdHeads.addAll(searchResultsHandler.getFinnAdHeads());
            LOG.info("Found {} FINN heads - all {}", searchResultsHandler.getFinnAdHeads().size(), collectedAdHeads.size());
            searchResultsHandler.getNextPageUrl().ifPresent(pendingUrls::push);
        }

        return collectedAdHeads;
    }

    /**
     * Retrieves a list of fully detailed ads from a given list of ad heads
     */
    public Set<FinnAd> fetchFullAds(Set<FinnAdHead> finnAdHeadList) {
        Set<FinnAd> fullAdSet = new HashSet<>();

        for (FinnAdHead head : finnAdHeadList) {
            try (Response response = executeRequest(createRequest(head.getLink()))) {
                fullAdSet.add(new FinnAd(parseReaderToDocument(response.body().charStream())));
            } catch (IOException ioe) {
                LOG.info("Failed to parse ad from URL {}", head.getLink(), ioe);

            } catch (Exception e) {
                LOG.error("An unexpected failure occurred while parsing ad from URL {}", head.getLink(), e);
            }
        }

        return fullAdSet;
    }

    Document parseReaderToDocument(Reader reader)
            throws ParserConfigurationException, SAXException, IOException {
        return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(reader));
    }

    void parseReaderWithHandler(Reader reader, DefaultHandler handler)
            throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory.newInstance().newSAXParser().parse(new InputSource(reader), handler);
    }

    private Response executeRequest(Request request)
            throws IOException {
        addPoliteDelayBetweenRequests();
        LOG.debug("{}", request);
        Response response = clientProvider.get().newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected response code " + response.code());
        }
        return response;
    }

    public boolean isPingSuccessful() {
        try {
            Response response = clientProvider.get().newCall(createRequest(HttpUrl.parse(serviceDocumentUrl))).execute();
            return response.isSuccessful();
        } catch (IOException e) {
            LOG.error("Error while pinging connection to Finn.", e);
            return false;
        }
    }
}
