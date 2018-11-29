package no.nav.pam.annonsemottak.annonsemottak.polaris;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import no.nav.pam.annonsemottak.annonsemottak.HttpClientProxy;
import no.nav.pam.annonsemottak.annonsemottak.polaris.model.PolarisAd;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PolarisConnectorTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(7012);

    @Autowired
    HttpClientProxy httpClientProxy;

    @Value("${polaris.url}")
    String apiEndpoint;

    PolarisConnector polarisConnector;

    @Before
    public void init() {
        polarisConnector = new PolarisConnector(httpClientProxy, apiEndpoint, new ObjectMapper());
    }

    @Test
    public void testFetch() throws IOException {
        wireMockRule.stubFor(get(urlEqualTo(
                "/api/nav.json?datetime=2018-11-21T13:03:46.95"))
                .willReturn(aResponse().withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(getMockPolarisResponse("result"))));


        List<PolarisAd> polarisAdList = polarisConnector.fetchData(LocalDateTime.parse("2018-11-21T13:03:46.95"));
        assertEquals(23, polarisAdList.size());
    }

    private String getMockPolarisResponse(String fil) {
        return new BufferedReader(
                new InputStreamReader(PolarisConnectorTest.class.getClassLoader()
                        .getResourceAsStream("polaris.samples/" + fil + ".json")))
                .lines().collect(Collectors.joining("\n"));
    }
}
