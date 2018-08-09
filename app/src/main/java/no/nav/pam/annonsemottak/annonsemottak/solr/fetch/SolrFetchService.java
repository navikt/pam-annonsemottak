package no.nav.pam.annonsemottak.annonsemottak.solr.fetch;

import com.google.common.collect.ImmutableMap;
import no.nav.pam.annonsemottak.annonsemottak.solr.SolrRepository;
import no.nav.pam.annonsemottak.annonsemottak.solr.StillingSolrBean;
import no.nav.pam.annonsemottak.app.sensu.SensuClient;
import no.nav.pam.annonsemottak.stilling.Stilling;
import no.nav.pam.annonsemottak.stilling.StillingRepository;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SolrFetchService {

    private static final Logger LOG = LoggerFactory.getLogger(SolrFetchService.class);
    private static final String fraArbeidsgiver = "Overført fra arbeidsgiver";
    private static final String registrertNav = "Reg. av arb.giver på nav.no";
    private static final String meldtNavLokalt = "Meldt til NAV lokalt";
    private static final String direktemeldt = "Direktemeldt stilling (Nav.no)";
    private final SolrRepository solrRepository;
    private final StillingRepository stillingRepository;
    private final String filterQueryKildetekst;

    @Inject
    public SolrFetchService(SolrRepository solrRepository,
                            StillingRepository stillingRepository) {
        this.solrRepository = solrRepository;
        this.stillingRepository = stillingRepository;

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

    public List<Stilling> saveNewStillingerFromSolr(LocalDateTime since) {
        List<Stilling> savedStillinger = (List<Stilling>) stillingRepository.saveAll(searchForNewStillinger(since));

        SensuClient.sendEvent(
                "solrStillingerHentet.event",
                Collections.emptyMap(),
                ImmutableMap.of("new", savedStillinger.size()));

        return savedStillinger;
    }

    List<Stilling> searchForNewStillinger(LocalDateTime since) {
        SolrQuery solrQuery = buildSolrQueryForSearch(since);
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
                .filter(this::newStillingSolrAd)
                .map(StillingSolrBeanMapper::mapToStilling)
                .collect(Collectors.toList());
    }

    private SolrQuery buildSolrQueryForSearch(LocalDateTime since) {
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery("*:*");

        solrQuery.addFilterQuery(StillingSolrBeanFieldNames.KILDETEKST + ":" + filterQueryKildetekst);
        solrQuery.addFilterQuery(StillingSolrBeanFieldNames.REG_DATO + ":" + buildFilterQueryRegDato(since));

        solrQuery.setFacet(false);
        solrQuery.setStart(0);
        solrQuery.setRows(100);

        solrQuery.add("pam", "pam");

        return solrQuery;
    }

    private String buildFilterQueryRegDato(LocalDateTime since) {
        String newDate = since.toString();
        return "[" + newDate + " TO *]";
    }

    private boolean newStillingSolrAd(StillingSolrBean solrBean) {
        return stillingRepository
                .findByKildeAndMediumAndExternalId(
                        "stillingsolr",
                        solrBean.getKildetekst(),
                        solrBean.getId().toString()) == null;
    }
}
