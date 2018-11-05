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
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static no.nav.pam.annonsemottak.app.metrics.MetricNames.ADS_COLLECTED_SOLR_NEW;

@Service
public class SolrFetchService {

    private static final Logger LOG = LoggerFactory.getLogger(SolrFetchService.class);

    private static final String fraArbeidsgiver = "Overført fra arbeidsgiver";
    private static final String registrertNav = "Reg. av arb.giver på nav.no";
    private static final String meldtNavLokalt = "Meldt til NAV lokalt";
    private static final String direktemeldt = "Direktemeldt stilling (Nav.no)";
    private static final int daysToSubtract = 7;

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

    public List<Stilling> saveNewStillingerFromSolr(LocalDateTime since) {
        List<Stilling> savedStillinger = (List<Stilling>) stillingRepository.saveAll(searchForNewStillinger(since.atOffset(ZoneOffset.UTC)));

        meterRegistry.gauge(ADS_COLLECTED_SOLR_NEW, savedStillinger.size());

        return savedStillinger;
    }

    List<Stilling> searchForNewStillinger(OffsetDateTime since) {
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

    private SolrQuery buildSolrQueryForSearch(OffsetDateTime since) {
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

    //Going five days back in time because of missed ads caused by gaps in reg_dato vs fetching time
    private String buildFilterQueryRegDato(OffsetDateTime since) {
        String newDate = since.minusDays(daysToSubtract).format(DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss.SSSX"));
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
