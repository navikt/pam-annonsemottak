package no.nav.pam.annonsemottak.receivers.solr;


import no.nav.pam.annonsemottak.receivers.solr.fetch.StillingSolrBeanFieldNames;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;

public class SolrSearchTest {

    @Test
    @Ignore
    public void searchSolrTest() throws Exception {
        String url = "https://itjenester-q1.oera.no/stilling-solr/maincore";
        HttpClient httpClient = HttpClientBuilder.create().disableCookieManagement().build();
        SolrServer solrServer = new HttpSolrServer(url, httpClient);
        SolrRepository repository = new SolrRepository(solrServer);
        SolrService solrService = new SolrService(repository);
        HashMap<String,String> params = new HashMap<>();
        params.put(StillingSolrBeanFieldNames.ARBEIDSGIVERNAVN, "Gjøvik kommune");
        List<StillingSolrBean> stillingSolrBeans = solrService.searchStillinger(params);
        stillingSolrBeans.stream().forEach(s -> {
            System.out.println(s.getArbeidsgivernavn());
            System.out.println(s.getTittel());
        });

    }
}
