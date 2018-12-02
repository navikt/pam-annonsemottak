package no.nav.pam.annonsemottak.receivers.finn;

import no.nav.pam.annonsemottak.receivers.HttpClientProxy;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

public class FinnConnector {

    private static final Logger LOG = LoggerFactory.getLogger(FinnConnector.class);

    private final HttpClientProxy proxy;
    private final String serviceDocumentUrl;
    private final String apiKey;
    private final int politeRequestDelayInMillis;

    public FinnConnector(HttpClientProxy proxy, String serviceDocumentUrl, String apiKey, int politeRequestDelayInMillis) {
        this.proxy = proxy;
        this.serviceDocumentUrl = serviceDocumentUrl;
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

    /**
     * Retrieves all the currently active job ad heads from the search result
     */
    public Set<FinnAdHead> fetchSearchResult(String... collections)
            throws FinnConnectorException {
        try {
            // 1. Parse service document (using XPath), as recommended by documentation.
            FinnServiceDocument serviceDocument = getServiceDocument();

            // 2. Parse multi-page search results (using SAX) for all workspaces and collections we're interested in.
            HttpUrl[] initialUrls = new HttpUrl[collections.length];
            for (int i = 0; i < collections.length; i++) {
                initialUrls[i] = serviceDocument.getHrefFromCollectionInWorkspace("searches", collections[i]);
            }

            return collectAdHeads(initialUrls);
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
            searchResultsHandler.getNextPageUrl().ifPresent(pendingUrls::push);
        }

        return collectedAdHeads;
    }

    /**
     * Retrieves a list of fully detailed ads from a given list of ad heads
     */
    public Set<FinnAd> fetchFullAds(Set<FinnAdHead> finnAdHeadList) throws FinnConnectorException {
        Set<FinnAd> fullAdSet = new HashSet<>();

        for (FinnAdHead head : finnAdHeadList) {
            try (Response response = executeRequest(createRequest(head.getLink()))) {
                fullAdSet.add(new FinnAd(parseReaderToDocument(response.body().charStream())));
            } catch (Exception e) {
                LOG.error("Failed to parse ad from URL {}", head.getLink(), e);
            }
        }

        return fullAdSet;
    }

    private FinnServiceDocument getServiceDocument()
            throws ParserConfigurationException, SAXException, IOException {
        return new FinnServiceDocument(
                parseReaderToDocument(
                        executeRequest(
                                createRequest(HttpUrl.parse(serviceDocumentUrl)))
                                .body()
                                .charStream()
                )
        );
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
        Response response = proxy.getHttpClient().newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected response code " + response.code());
        }
        return response;
    }

    public boolean isPingSuccessful() {
        try {
            Response response = proxy.getHttpClient().newCall(createRequest(HttpUrl.parse(serviceDocumentUrl))).execute();
            return response.isSuccessful();
        } catch (IOException e) {
            LOG.error("Error while pinging connection to Finn.", e);
            return false;
        }
    }
}
