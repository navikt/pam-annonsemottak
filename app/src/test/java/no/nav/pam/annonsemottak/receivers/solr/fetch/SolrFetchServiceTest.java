package no.nav.pam.annonsemottak.receivers.solr.fetch;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import no.nav.pam.annonsemottak.receivers.solr.SolrRepository;
import no.nav.pam.annonsemottak.receivers.solr.StillingSolrBean;
import no.nav.pam.annonsemottak.stilling.Stilling;
import no.nav.pam.annonsemottak.stilling.StillingRepository;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.assertj.core.api.SoftAssertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SolrFetchServiceTest {


    private static final int ID = 1;
    private static final String KILDE = "nav.no";
    private static final String ARBEIDSGIVER = "Arbeid AS";

    private SolrFetchService solrFetchService;

    @Mock
    private StillingRepository stillingRepository;
    @Mock
    private QueryResponse queryResponse;
    @Mock
    private SolrRepository solrRepository;


    @Before
    public void before() {
        solrFetchService = new SolrFetchService(solrRepository, stillingRepository, new SimpleMeterRegistry());
    }

    @Test
    public void should_return_new_stillinger() {
        SolrDocumentList solrDocuments = buildFakeSolrDocumentList(1);
        StillingSolrBean solrBean = buildFakeSolrBean(ID, KILDE, ARBEIDSGIVER, "");
        List<Object> solrBeans = Collections.singletonList(solrBean);

        when(solrRepository.query(any(SolrQuery.class))).thenReturn(queryResponse);
        when(queryResponse.getResults()).thenReturn(solrDocuments);
        when(queryResponse.getBeans(any())).thenReturn(solrBeans);

        List<Stilling> stillingList = solrFetchService.searchForStillinger();

        assertThat(stillingList).hasSize(1);
        Stilling convertedStilling = stillingList.get(0);
        assertThat(convertedStilling.getArbeidsgiver()).isPresent();
        assertThat(convertedStilling.getArbeidsgiver().get().asString()).isEqualTo(ARBEIDSGIVER);
    }

    @Test
    public void should_not_include_pam_dir_stillinger() {
        when(solrRepository.query(any(SolrQuery.class))).thenReturn(queryResponse);
        when(queryResponse.getResults()).thenReturn(buildFakeSolrDocumentList(4));
        when(queryResponse.getBeans(any())).thenReturn(
                Stream.of(
                        buildFakeSolrBean(1, KILDE, "A", "B"),
                        buildFakeSolrBean(2, KILDE, "AA", "BB <p hidden></p> BB"),
                        buildFakeSolrBean(3, KILDE, "AAA", null),
                        buildFakeSolrBean(4, KILDE, "AAAA", "PAM is a great place to work"),
                        buildFakeSolrBean(5, "DIR", "AAAA", "BBBB" + "<p hidden>PAM</p>"),
                        buildFakeSolrBean(6, "DIR", "AAAA", "BBBB" + "...<BR> PAM")
                ).collect(Collectors.toList())
        );

        List<Stilling> stillinger = solrFetchService.searchForStillinger();

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(stillinger).hasSize(4);
        softly.assertThat(stillinger.get(0).getExternalId()).isEqualTo("1");
        softly.assertThat(stillinger.get(1).getExternalId()).isEqualTo("2");
        softly.assertThat(stillinger.get(2).getExternalId()).isEqualTo("3");
        softly.assertThat(stillinger.get(3).getExternalId()).isEqualTo("4");
        softly.assertAll();
    }

    private SolrDocumentList buildFakeSolrDocumentList(int numFound) {
        SolrDocumentList solrDocuments = new SolrDocumentList();
        solrDocuments.setNumFound(numFound);
        return solrDocuments;
    }

    private StillingSolrBean buildFakeSolrBean(Integer id, String kildetekst, String arbeidsgivernavn, String stillingsbeskrivelse) {
        StillingSolrBean solrBean = new StillingSolrBean();

        solrBean.setId(id);
        solrBean.setKildetekst(kildetekst);
        solrBean.setArbeidsgivernavn(arbeidsgivernavn);
        solrBean.setStillingsbeskrivelse(stillingsbeskrivelse);

        return solrBean;
    }
}
