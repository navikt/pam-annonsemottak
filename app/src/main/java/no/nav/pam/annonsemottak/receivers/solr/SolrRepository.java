package no.nav.pam.annonsemottak.receivers.solr;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

@Repository
public class SolrRepository {

    private static final Logger LOG = LoggerFactory.getLogger(SolrRepository.class);
    private SolrServer solrServer;

    @Inject
    public SolrRepository(SolrServer solrServer) {
        this.solrServer = solrServer;
    }

    public int status() {
        try {
            return solrServer.ping().getStatus();
        }
        catch (Exception e) {
            LOG.error("Got error on solr server status: " +e.getMessage());
            return -1;
        }
    }

    public QueryResponse query(SolrQuery solrQuery) {
        QueryResponse queryResponse = null;
        try {
            queryResponse = solrServer.query(solrQuery, SolrRequest.METHOD.POST);
        }
        catch (SolrServerException e) {
            LOG.error("Got error while querying solrserver",e);
        }
        return queryResponse;
    }


}
