package no.nav.pam.annonsemottak.receivers.polaris;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import io.micrometer.core.instrument.MeterRegistry;
import no.nav.pam.annonsemottak.receivers.HttpClientProxy;
import no.nav.pam.annonsemottak.receivers.Kilde;
import no.nav.pam.annonsemottak.receivers.common.rest.payloads.ResultsOnSave;
import no.nav.pam.annonsemottak.receivers.externalRun.ExternalRunService;
import no.nav.pam.annonsemottak.receivers.fangst.AnnonseFangstService;
import no.nav.pam.annonsemottak.receivers.polaris.model.PolarisAd;
import no.nav.pam.annonsemottak.receivers.solr.SolrRepository;
import no.nav.pam.annonsemottak.receivers.solr.SolrService;
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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@MockBean(value = SolrService.class)
@MockBean(SolrRepository.class)
@MockBean(StillingRepository.class)
@Transactional
@Rollback
public class PolarisConnectorTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(7012);

    @Autowired
    HttpClientProxy httpClientProxy;

    @Autowired
    ExternalRunService externalRunService;

    @Autowired
    AnnonseFangstService annonseFangstService;

    @Autowired
    MeterRegistry meterRegistry;

    @Value("${polaris.url}")
    String apiEndpoint;

    PolarisConnector polarisConnector;

    PolarisService polarisService;

    @Before
    public void init() {
        polarisConnector = new PolarisConnector(httpClientProxy, apiEndpoint, new ObjectMapper());
        polarisService = new PolarisService(externalRunService, meterRegistry, polarisConnector, annonseFangstService);

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

        assertEquals(23, result.getReceived());
        assertEquals(23, result.getSaved());

        assertNotNull(externalRunService.findLastRunForRunName(Kilde.POLARIS.value()));
    }


}
