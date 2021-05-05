package no.nav.pam.annonsemottak.receivers.xmlstilling;

import no.nav.pam.annonsemottak.receivers.HttpClientProvider;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class XmlStillingConnectorTest {

    private XmlStillingConnector connector;

    private MockWebServer server;

    private EndpointProvider endpointProvider = mock(EndpointProvider.class);

    @BeforeEach
    public  void setUp() {
        server = new MockWebServer();
        HttpClientProvider clientProxy = new HttpClientProvider(new OkHttpClient().newBuilder().build());
        connector = new XmlStillingConnector(clientProxy, endpointProvider);
    }

    @Test
    public void basicDeserialization()
            throws Exception {

        server.enqueue(new MockResponse().setBody(fileAsString("xmlstilling.samples/sample1.json")));
        server.start();

        when(endpointProvider.forFetchWithStartingId(any())).thenReturn(server.url("").toString());

        connector.fetchFrom(LocalDateTime.now());

    }

    @AfterEach
    public void shutdown() throws IOException {
        server.shutdown();
    }

    public String fileAsString(String file) throws Exception {
        java.net.URL url = XmlStillingConnectorTest.class.getClassLoader().getResource(file);
        return new java.util.Scanner(new File(url.toURI()),"UTF8").useDelimiter("\\Z").next();
    }

}
