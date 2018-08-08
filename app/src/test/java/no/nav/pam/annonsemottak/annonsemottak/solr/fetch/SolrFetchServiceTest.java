package no.nav.pam.annonsemottak.annonsemottak.solr.fetch;

import no.nav.pam.annonsemottak.annonsemottak.solr.SolrRepository;
import no.nav.pam.annonsemottak.annonsemottak.solr.StillingSolrBean;
import no.nav.pam.annonsemottak.stilling.Stilling;
import no.nav.pam.annonsemottak.stilling.StillingRepository;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SolrFetchServiceTest {


    private static final int ID = 1;
    private static final String KILDE = "nav.no";
    private static final String ARBEIDSGIVER = "Arbeid AS";
    private static final DateTime DATO = DateTime.now();

    private SolrFetchService solrFetchService;

    @Mock
    private StillingRepository stillingRepository;
    @Mock
    private QueryResponse queryResponse;
    @Mock
    private SolrRepository solrRepository;


    @Before
    public void before() {
        solrFetchService = new SolrFetchService(solrRepository, stillingRepository);
    }

    @Test
    public void should_return_new_stillinger() {
        SolrDocumentList solrDocuments = buildFakeSolrDocumentList();
        StillingSolrBean solrBean = buildFakeSolrBean();
        List<Object> solrBeans = Collections.singletonList(solrBean);

        when(solrRepository.query(any(SolrQuery.class))).thenReturn(queryResponse);
        when(queryResponse.getResults()).thenReturn(solrDocuments);
        when(queryResponse.getBeans(any())).thenReturn(solrBeans);
        when(stillingRepository.findByKildeAndMediumAndExternalId(anyString(), anyString(), anyString())).thenReturn(null);

        List<Stilling> stillingList = solrFetchService.searchForNewStillinger(DATO);

        assertThat(stillingList).hasSize(1);
        Stilling convertedStilling = stillingList.get(0);
        assertThat(convertedStilling.getArbeidsgiver()).isPresent();
        assertThat(convertedStilling.getArbeidsgiver().get().asString()).isEqualTo(ARBEIDSGIVER);
    }

    @Test
    public void should_not_return_existing_stilling() {
        SolrDocumentList solrDocuments = buildFakeSolrDocumentList();
        StillingSolrBean solrBean = buildFakeSolrBean();
        List<Object> solrBeans = Collections.singletonList(solrBean);

        when(solrRepository.query(any(SolrQuery.class))).thenReturn(queryResponse);
        when(queryResponse.getResults()).thenReturn(solrDocuments);
        when(queryResponse.getBeans(any())).thenReturn(solrBeans);
        when(stillingRepository.findByKildeAndMediumAndExternalId(anyString(), anyString(), anyString()))
                .thenReturn(mock(Stilling.class));

        List<Stilling> stillingList = solrFetchService.searchForNewStillinger(DATO);

        assertThat(stillingList).isEmpty();
    }


    private SolrDocumentList buildFakeSolrDocumentList() {
        SolrDocumentList solrDocuments = new SolrDocumentList();
        solrDocuments.setNumFound(1);
        return solrDocuments;
    }

    private StillingSolrBean buildFakeSolrBean() {
        StillingSolrBean solrBean = new StillingSolrBean();

        solrBean.setId(ID);
        solrBean.setKildetekst(KILDE);
        solrBean.setArbeidsgivernavn(ARBEIDSGIVER);

        return solrBean;
    }
}
