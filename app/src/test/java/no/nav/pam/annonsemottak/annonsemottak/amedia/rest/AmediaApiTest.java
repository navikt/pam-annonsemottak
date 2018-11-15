package no.nav.pam.annonsemottak.annonsemottak.amedia.rest;

import no.nav.pam.annonsemottak.annonsemottak.solr.SolrRepository;
import no.nav.pam.annonsemottak.annonsemottak.solr.SolrService;
import no.nav.pam.annonsemottak.api.PathDefinition;
import no.nav.pam.annonsemottak.stilling.StillingRepository;
import org.assertj.core.api.SoftAssertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@MockBean(SolrService.class)
@MockBean(SolrRepository.class)
@MockBean(StillingRepository.class)
@AutoConfigureMockMvc
@ActiveProfiles({"test", "wiremock"})
public class AmediaApiTest {

    static {
        System.setProperty("PROXY_URL", "");
        System.setProperty("DEXI_URL", "");
        System.setProperty("DEXI_API_USERNAME", "");
        System.setProperty("DEXI_API_PASSWORD", "");
    }

    @Autowired
    protected MockMvc mvc;

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
