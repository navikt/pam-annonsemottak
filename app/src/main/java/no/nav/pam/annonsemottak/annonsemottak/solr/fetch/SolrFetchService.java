package no.nav.pam.annonsemottak.annonsemottak.solr.fetch;

import io.micrometer.core.instrument.MeterRegistry;
import no.nav.pam.annonsemottak.annonsemottak.solr.SolrRepository;
import no.nav.pam.annonsemottak.annonsemottak.solr.StillingSolrBean;
import no.nav.pam.annonsemottak.stilling.Stilling;
import no.nav.pam.annonsemottak.stilling.StillingRepository;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static no.nav.pam.annonsemottak.app.metrics.MetricNames.*;

@Service
public class SolrFetchService {

    private static final Logger LOG = LoggerFactory.getLogger(SolrFetchService.class);

    private static final String fraArbeidsgiver = "Overført fra arbeidsgiver";
    private static final String registrertNav = "Reg. av arb.giver på nav.no";
    private static final String meldtNavLokalt = "Meldt til NAV lokalt";
    private static final String direktemeldt = "Direktemeldt stilling (Nav.no)";

    private final MeterRegistry meterRegistry;
    private final SolrRepository solrRepository;
    private final StillingRepository stillingRepository;
    private final String filterQueryKildetekst;

    @Inject
    public SolrFetchService(SolrRepository solrRepository,
                            StillingRepository stillingRepository,
                            MeterRegistry meterRegistry) {
        this.solrRepository = solrRepository;
        this.stillingRepository = stillingRepository;
        this.meterRegistry = meterRegistry;

        filterQueryKildetekst = buildFilterQueryKildetekst();
    }

    private static String buildFilterQueryKildetekst() {
        return "(" +
                "\"" + fraArbeidsgiver + "\"" +
                " OR " +
                "\"" + registrertNav + "\"" +
                " OR " +
                "\"" + meldtNavLokalt + "\"" +
                " OR " +
                "\"" + direktemeldt + "\"" +
                ")";
    }

    public List<Stilling> saveStillingerFromSolr() {
        List<Stilling> stillingerList = searchForStillinger();
        AtomicInteger updatedStillingCounter = new AtomicInteger(0);

        stillingerList.stream().forEach(s -> {
            Optional<Stilling> inDb = stillingRepository.findByKildeAndMediumAndExternalId(
                    s.getKilde(),
                    s.getMedium(),
                    s.getExternalId());
            if (inDb.isPresent()) {
                s.merge(inDb.get());
                updatedStillingCounter.incrementAndGet();
            }
        });

        List<Stilling> savedStillinger = (List<Stilling>) stillingRepository.saveAll(stillingerList);

        meterRegistry.gauge(ADS_COLLECTED_SOLR_TOTAL, savedStillinger.size());
        meterRegistry.gauge(ADS_COLLECTED_SOLR_NEW, savedStillinger.size() - updatedStillingCounter.get());
        meterRegistry.gauge(ADS_COLLECTED_SOLR_CHANGED, updatedStillingCounter.get());

        LOG.info("Saved {} new and {} changed ads from solr", savedStillinger.size() - updatedStillingCounter.get(), updatedStillingCounter.get());
        return savedStillinger;
    }

    List<Stilling> searchForStillinger() {
        SolrQuery solrQuery = buildSolrQueryForSearch();
        QueryResponse response = solrRepository.query(solrQuery);
        SolrDocumentList result = response.getResults();

        long numFound = result.getNumFound();
        LOG.debug("Total hits: {}", numFound);
        int current = 0;

        List<Stilling> newStillinger = new ArrayList<>();

        while (current < numFound) {
            newStillinger.addAll(extractStillingerFromBeans(response));

            current += solrQuery.getRows();
            if (current > numFound) {
                break;
            }

            solrQuery.setStart(current);
            response = solrRepository.query(solrQuery);
        }

        LOG.debug("Found {} new ads from stillingsolr", newStillinger.size());
        return newStillinger;
    }

    private List<Stilling> extractStillingerFromBeans(QueryResponse response) {
        return response.getBeans(StillingSolrBean.class).stream()
                .map(StillingSolrBeanMapper::mapToStilling)
                .collect(Collectors.toList());
    }

    private SolrQuery buildSolrQueryForSearch() {
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery("*:*");

        solrQuery.addFilterQuery(StillingSolrBeanFieldNames.KILDETEKST + ":" + filterQueryKildetekst);

        solrQuery.setFacet(false);
        solrQuery.setStart(0);
        solrQuery.setRows(100);

        solrQuery.add("pam", "pam");

        return solrQuery;
    }
}
