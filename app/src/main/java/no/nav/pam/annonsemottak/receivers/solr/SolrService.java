package no.nav.pam.annonsemottak.receivers.solr;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
public class SolrService {

    private final SolrRepository solrRepository;

    @Inject
    public SolrService(SolrRepository solrRepository) {
        this.solrRepository = solrRepository;
    }

    public List<StillingSolrBean> searchStillinger(Map<String, String> parameters) {
        SolrQuery solrQuery = buildSolrQuery(parameters);
        QueryResponse response = solrRepository.query(solrQuery);
        List<StillingSolrBean> beans = response.getBeans(StillingSolrBean.class);
        return beans;
    }

    private SolrQuery buildSolrQuery(Map<String, String> parameters) {
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery("*:*");
        parameters.keySet().stream().forEach(key -> solrQuery.addFilterQuery(""+key+":\""+parameters.get(key)+"\""));
        solrQuery.setStart(0);
        solrQuery.setFacet(false);
        solrQuery.setRows(5);
        solrQuery.add("pam","pam");
        return solrQuery;
    }
}
