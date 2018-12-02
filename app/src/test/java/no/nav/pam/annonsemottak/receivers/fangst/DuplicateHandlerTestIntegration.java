package no.nav.pam.annonsemottak.receivers.fangst;

import no.nav.pam.annonsemottak.receivers.solr.SolrRepository;
import no.nav.pam.annonsemottak.receivers.solr.SolrService;
import no.nav.pam.annonsemottak.stilling.Merknader;
import no.nav.pam.annonsemottak.stilling.Status;
import no.nav.pam.annonsemottak.stilling.Stilling;
import no.nav.pam.annonsemottak.stilling.StillingTestdataBuilder;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
@Ignore
public class DuplicateHandlerTestIntegration {


    @Test
    public void testDuplicates() throws Exception {
        Stilling noMatch = StillingTestdataBuilder.enkelStilling().build();
        Stilling dupe = StillingTestdataBuilder.stilling().arbeidsgiver("SPIRONORGE AS").tittel("lager/sjøfør/ produksjonsmedarbeider").build();
        AnnonseResult result = new AnnonseResult();
        result.getNewList().add(noMatch);
        result.getNewList().add(dupe);
        String url = "https://itjenester-t1.oera.no/stilling-solr/maincore";
        HttpClient httpClient = HttpClientBuilder.create().disableCookieManagement().build();
        SolrServer solrServer = new HttpSolrServer(url, httpClient);
        SolrRepository repository = new SolrRepository(solrServer);
        SolrService solrService = new SolrService(repository);
        DuplicateHandler duplicateHandler = new DuplicateHandler(solrService);
        duplicateHandler.markDuplicates(result);
        Assert.assertEquals(1, result.getDuplicateList().size());
        Assert.assertEquals(Status.AVVIST, dupe.getStatus());
        Assert.assertEquals(result.getNewList().size(), 1);
        Assert.assertEquals(Merknader.Merknad.DUPLIKAT.getKodeAsString(), dupe.getMerknader().get().asString());
        System.out.println(dupe.getStatus().toString() + " " + dupe.getMerknader().get().asString() + " "+dupe.getKommentarer().get().asString());
    }
}
