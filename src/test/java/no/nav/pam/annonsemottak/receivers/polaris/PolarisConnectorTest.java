package no.nav.pam.annonsemottak.receivers.polaris;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import no.nav.pam.annonsemottak.Application;
import no.nav.pam.annonsemottak.app.metrics.AnnonseMottakProbe;
import no.nav.pam.annonsemottak.receivers.HttpClientProvider;
import no.nav.pam.annonsemottak.receivers.Kilde;
import no.nav.pam.annonsemottak.receivers.common.rest.payloads.ResultsOnSave;
import no.nav.pam.annonsemottak.receivers.externalRun.ExternalRunService;
import no.nav.pam.annonsemottak.receivers.fangst.AnnonseFangstService;
import no.nav.pam.annonsemottak.receivers.polaris.model.PolarisAd;
import no.nav.pam.annonsemottak.stilling.StillingRepository;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@MockBean(StillingRepository.class)
@Transactional
@Rollback
@ActiveProfiles("test")
public class PolarisConnectorTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(7012);

    @Inject
    @Named("internalHttpClient")
    HttpClientProvider httpClientProvider;

    @Autowired
    ExternalRunService externalRunService;

    @Autowired
    AnnonseFangstService annonseFangstService;

    @Value("${polaris.url}")
    String apiEndpoint;

    private PolarisConnector polarisConnector;

    private PolarisService polarisService;

    private AnnonseMottakProbe probe = mock(AnnonseMottakProbe.class);

    @Before
    public void init() {
        polarisConnector = new PolarisConnector(httpClientProvider, apiEndpoint, "user", "password", new ObjectMapper());
        polarisService = new PolarisService(externalRunService, polarisConnector, annonseFangstService, probe);

        wireMockRule.stubFor(get(urlPathMatching(
                "/api/nav.json"))
                .willReturn(aResponse().withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(getMockPolarisResponse("result"))));
    }

    private String getMockPolarisResponse(String fil) {
        return new BufferedReader(
                new InputStreamReader(PolarisConnectorTest.class.getClassLoader()
                        .getResourceAsStream("polaris.samples/" + fil + ".json")))
                .lines().collect(Collectors.joining("\n"));
    }

    @Test
    public void should_fetch_data_from_polaris() throws IOException {

        List<PolarisAd> polarisAdList = polarisConnector.fetchData(LocalDateTime.parse("2018-11-21T13:03:46.95"));
        assertEquals(23, polarisAdList.size());
    }

    @Test
    public void should_fetch_and_save_from_polaris() throws IOException {

        assertNull(externalRunService.findLastRunForRunName(Kilde.POLARIS.value()));

        ResultsOnSave result = polarisService.fetchAndSaveLatest();

        assertEquals(21, result.getReceived());
        assertEquals(21, result.getSaved());

        assertNotNull(externalRunService.findLastRunForRunName(Kilde.POLARIS.value()));
    }

}
