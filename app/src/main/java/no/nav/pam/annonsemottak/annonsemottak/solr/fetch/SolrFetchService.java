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
import java.util.stream.Collectors;

import static no.nav.pam.annonsemottak.app.metrics.MetricNames.*;

@Service
public class SolrFetchService {

    private static final Logger LOG = LoggerFactory.getLogger(SolrFetchService.class);

    private static final String fraArbeidsgiver = "Overført fra arbeidsgiver";
    private static final String registrertNav = "Reg. av arb.giver på nav.no";
    private static final String meldtNavLokalt = "Meldt til NAV lokalt";
    private static final String direktemeldt = "Direktemeldt stilling (Nav.no)";
    private static final String fraEures = "Fra Eures";

    private final MeterRegistry meterRegistry;
    private final SolrRepository solrRepository;
    private final StillingRepository stillingRepository;
    private final String filterQueryKildetekst;

    /**
     * When this string cookie occurs in ad text, the ad is to be filtered out of the fetched set.
     */
    static final String PAM_DIR_ADTEXT_COOKIE = "<p hidden>PAM</p>";

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
                " OR " +
                "\"" + fraEures + "\"" +
                ")";
    }

    public List<Stilling> saveStillingerFromSolr() {
        List<Stilling> allStillinger = searchForStillinger();
        List<Stilling> newStillinger = new ArrayList();
        List<Stilling> changedStillinger = new ArrayList();

        allStillinger.stream().forEach(s -> {
            Optional<Stilling> inDb = stillingRepository.findByKildeAndMediumAndExternalId(
                    s.getKilde(),
                    s.getMedium(),
                    s.getExternalId());

            if (inDb.isPresent()) {
                if (!inDb.get().getHash().equals(s.getHash())) {
                    s.merge(inDb.get());
                    changedStillinger.add(s);
                }
            } else {
                newStillinger.add(s);
            }
        });

        List<Stilling> savedStillinger = new ArrayList();
        savedStillinger.addAll(newStillinger);
        savedStillinger.addAll(changedStillinger);
        savedStillinger = (List<Stilling>) stillingRepository.saveAll(savedStillinger);

        meterRegistry.gauge(ADS_COLLECTED_SOLR_TOTAL, allStillinger.size());
        meterRegistry.gauge(ADS_COLLECTED_SOLR_NEW, newStillinger.size());
        meterRegistry.gauge(ADS_COLLECTED_SOLR_CHANGED, changedStillinger.size());

        LOG.info("Saved {} new and {} changed ads from stillingsolr total {}",
                newStillinger.size(), changedStillinger.size(), allStillinger.size());
        return savedStillinger;
    }

    List<Stilling> searchForStillinger() {
        SolrQuery solrQuery = buildSolrQueryForSearch();
        QueryResponse response = solrRepository.query(solrQuery);
        SolrDocumentList result = response.getResults();

        long numFound = result.getNumFound();
        LOG.debug("Total hits: {}", numFound);
        int current = 0;

        List<Stilling> solrStillinger = new ArrayList<>();

        while (current < numFound) {
            solrStillinger.addAll(extractStillingerFromBeans(response));

            current += solrQuery.getRows();
            if (current > numFound) {
                break;
            }

            solrQuery.setStart(current);
            response = solrRepository.query(solrQuery);
        }

        LOG.debug("Fetched {} ads from stillingsolr", solrStillinger.size());
        return solrStillinger;
    }

    private List<Stilling> extractStillingerFromBeans(QueryResponse response) {
        return response.getBeans(StillingSolrBean.class).stream()
                .filter(SolrFetchService::notPamDirStillinger)
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

    static boolean notPamDirStillinger(StillingSolrBean b) {
        return b.getStillingsbeskrivelse() == null || b.getStillingsbeskrivelse().indexOf(PAM_DIR_ADTEXT_COOKIE) == -1;
    }

}
