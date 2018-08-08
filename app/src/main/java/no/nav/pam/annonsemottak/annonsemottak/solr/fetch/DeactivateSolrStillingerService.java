package no.nav.pam.annonsemottak.annonsemottak.solr.fetch;

import com.google.common.collect.ImmutableMap;
import no.nav.pam.annonsemottak.annonsemottak.Kilde;
import no.nav.pam.annonsemottak.annonsemottak.solr.SolrRepository;
import no.nav.pam.annonsemottak.app.sensu.SensuClient;
import no.nav.pam.annonsemottak.stilling.AnnonseStatus;
import no.nav.pam.annonsemottak.stilling.Stilling;
import no.nav.pam.annonsemottak.stilling.StillingRepository;
import org.apache.solr.client.solrj.SolrQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeactivateSolrStillingerService {

    private static final Logger LOG = LoggerFactory.getLogger(DeactivateSolrStillingerService.class);

    private final SolrRepository solrRepository;
    private final StillingRepository stillingRepository;

    @Autowired
    public DeactivateSolrStillingerService(SolrRepository solrRepository,
                                           StillingRepository stillingRepository) {
        this.solrRepository = solrRepository;
        this.stillingRepository = stillingRepository;
    }

    @Transactional
    public void findAndDeactivateOldSolrStillinger() {
        List<Stilling> activeAds = stillingRepository.findByKildeAndAnnonseStatus(Kilde.STILLINGSOLR.value(), AnnonseStatus.AKTIV);

        List<Stilling> deactivatedAds = activeAds.stream()
                .filter(s -> doesNotExist(s.getExternalId()))
                .map(Stilling::deactivate)
                .collect(Collectors.toList());

        stillingRepository.saveAll(deactivatedAds);

        SensuClient.sendEvent(
                "solrStillingerDeaktivert.event",
                Collections.emptyMap(),
                ImmutableMap.of("deactivated", deactivatedAds.size()));
        LOG.info("Deactivated {} inactive ads from solr", deactivatedAds.size());
    }

    private boolean doesNotExist(String id) {
        return solrRepository.query(buildSolrQueryForSearch(id)).getResults().getNumFound() == 0;
    }

    private SolrQuery buildSolrQueryForSearch(String externalId) {
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery("ID:" + externalId);

        solrQuery.setFacet(false);
        solrQuery.setStart(0);
        solrQuery.setRows(1);

        solrQuery.add("pam", "pam");

        return solrQuery;
    }
}
