package no.nav.pam.annonsemottak.receivers.amedia.rest;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import no.nav.pam.annonsemottak.Application;
import no.nav.pam.annonsemottak.PathDefinition;
import no.nav.pam.annonsemottak.app.config.TestConfig;
import no.nav.pam.annonsemottak.receivers.amedia.AmediaResponseMapperTest;
import no.nav.pam.annonsemottak.receivers.amedia.AmediaService;
import no.nav.pam.annonsemottak.stilling.StillingRepository;
import org.assertj.core.api.SoftAssertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@MockBean(StillingRepository.class)
@AutoConfigureMockMvc
@ContextConfiguration(classes = TestConfig.class)
@ActiveProfiles("test")
public class AmediaApiTest {

    static {
        System.setProperty("PROXY_URL", "");
        System.setProperty("DEXI_URL", "");
        System.setProperty("DEXI_API_USERNAME", "");
        System.setProperty("DEXI_API_PASSWORD", "");
    }

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(7010);

    @Autowired
    protected MockMvc mvc;

    @Before
    public void initStubs() {
        wireMockRule.stubFor(get(urlMatching(
                ".*?all"))
                .willReturn(aResponse().withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(getMockResponse("idResultat"))));

        wireMockRule.stubFor(get(urlMatching(
                ".*?\\?modified=.*?"))
                .willReturn(aResponse().withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(getMockResponse("enkeltResultat"))));
    }

    private String getMockResponse(String fil) {
        return new BufferedReader(
                new InputStreamReader(AmediaResponseMapperTest.class.getClassLoader()
                        .getResourceAsStream("amedia.io.samples/" + fil + ".json")))
                .lines().collect(Collectors.joining("\n"));
    }

    /*
        Tester api, paramtereren til Amedia, og at alt utenom repoene er wiret opp.
     */
    @Test
    public void en_ny_amedia_stilling() throws Exception {
        MvcResult mvcResult = this.mvc.perform(
                post(PathDefinition.AMEDIA + "/results/save").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String content = mvcResult.getResponse().getContentAsString();

        SoftAssertions s = new SoftAssertions();
        s.assertThat(content).contains("\"stillingerHentet\":1");
        s.assertThat(content).contains("\"stillingerLagret\":1");
        s.assertThat(content).contains("millisekunderBrukt\":");

        s.assertAll();
    }
}
