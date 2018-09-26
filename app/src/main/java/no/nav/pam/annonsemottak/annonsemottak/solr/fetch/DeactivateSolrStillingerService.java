package no.nav.pam.annonsemottak.annonsemottak.solr.fetch;

import io.micrometer.core.instrument.MeterRegistry;
import no.nav.pam.annonsemottak.annonsemottak.Kilde;
import no.nav.pam.annonsemottak.annonsemottak.solr.SolrRepository;
import no.nav.pam.annonsemottak.app.metrics.MetricNames;
import no.nav.pam.annonsemottak.stilling.AnnonseStatus;
import no.nav.pam.annonsemottak.stilling.Stilling;
import no.nav.pam.annonsemottak.stilling.StillingRepository;
import org.apache.solr.client.solrj.SolrQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

import static no.nav.pam.annonsemottak.app.metrics.MetricNames.ADS_DEACTIVATED_SOLR;

@Service
public class DeactivateSolrStillingerService {

    private static final Logger LOG = LoggerFactory.getLogger(DeactivateSolrStillingerService.class);

    private final MeterRegistry meterRegistry;
    private final SolrRepository solrRepository;
    private final StillingRepository stillingRepository;

    @Autowired
    public DeactivateSolrStillingerService(SolrRepository solrRepository,
                                           StillingRepository stillingRepository,
                                           MeterRegistry meterRegistry) {
        this.solrRepository = solrRepository;
        this.stillingRepository = stillingRepository;
        this.meterRegistry = meterRegistry;
    }

    @Transactional
    public void findAndDeactivateOldSolrStillinger() {
        List<Stilling> activeAds = stillingRepository.findByKildeAndAnnonseStatus(Kilde.STILLINGSOLR.value(), AnnonseStatus.AKTIV);

        List<Stilling> deactivatedAds = activeAds.stream()
                .filter(s -> doesNotExist(s.getExternalId()))
                .map(Stilling::deactivate)
                .collect(Collectors.toList());

        stillingRepository.saveAll(deactivatedAds);

        meterRegistry.gauge(ADS_DEACTIVATED_SOLR, deactivatedAds.size());

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
