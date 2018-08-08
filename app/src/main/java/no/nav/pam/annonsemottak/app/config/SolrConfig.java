package no.nav.pam.annonsemottak.app.config;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty("solr.url")
public class SolrConfig {

    private static final Logger LOG = LoggerFactory.getLogger(SolrConfig.class);

    @Bean
    public SolrServer createSolrServer(@Value("${solr.url}") String solrUrl, @Value("${solr.core}") String core) {
        LOG.info("Using solr server: "+solrUrl+core );
        HttpClient httpClient = HttpClientBuilder.create().disableCookieManagement().build();
        SolrServer solrServer = new HttpSolrServer(solrUrl+core, httpClient);
        return solrServer;
    }


}
