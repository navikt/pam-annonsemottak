package no.nav.pam.annonsemottak.receivers.xmlstilling;

import no.nav.pam.annonsemottak.receivers.HttpClientProxy;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class XmlStillingConnectorTest {

    private XmlStillingConnector connector;

    private MockWebServer server;

    private EndpointProvider endpointProvider = mock(EndpointProvider.class);

    @Before
    public void setUp() {
        server = new MockWebServer();
        HttpClientProxy clientProxy = new HttpClientProxy();
        clientProxy.setHttpClient(new OkHttpClient().newBuilder().build());
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

    @After
    public void shutdown() throws IOException {
        server.shutdown();
    }

    public String fileAsString(String file) throws Exception {
        java.net.URL url = XmlStillingConnectorTest.class.getClassLoader().getResource(file);
        return new java.util.Scanner(new File(url.toURI()),"UTF8").useDelimiter("\\Z").next();
    }

}
