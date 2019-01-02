package no.nav.pam.annonsemottak.receivers.solr.fetch;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import no.nav.pam.annonsemottak.receivers.Kilde;
import no.nav.pam.annonsemottak.receivers.fangst.AnnonseFangstService;
import no.nav.pam.annonsemottak.receivers.solr.SolrRepository;
import no.nav.pam.annonsemottak.receivers.solr.StillingSolrBean;
import no.nav.pam.annonsemottak.stilling.AnnonseStatus;
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
import java.util.Arrays;
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
    private static final String navServicesenter = "NAV Servicesenter";

    private final SolrRepository solrRepository;
    private final StillingRepository stillingRepository;
    private final AnnonseFangstService annonseFangstService;

    /**
     * When this string cookie occurs in ad text, the ad is to be filtered out of the fetched set.
     */
    static final String PAM_DIR_ADTEXT_COOKIE = "<p hidden>PAM</p>";

    @Inject
    public SolrFetchService(SolrRepository solrRepository,
                            StillingRepository stillingRepository,
                            AnnonseFangstService annonseFangstService) {
        this.solrRepository = solrRepository;
        this.stillingRepository = stillingRepository;
        this.annonseFangstService = annonseFangstService;
    }

    private static String buildFilterQueryKildetekst() {
        return StillingSolrBeanFieldNames.KILDETEKST + ":(" +
                "\"" + fraArbeidsgiver + "\"" +
                " OR " +
                "\"" + registrertNav + "\"" +
                " OR " +
                "\"" + meldtNavLokalt + "\"" +
                " OR " +
                "\"" + direktemeldt + "\"" +
                " OR " +
                "\"" + fraEures + "\"" +
                ")" +
                " OR (" + StillingSolrBeanFieldNames.KILDETEKST + ":\"" + navServicesenter + "\"" +
                       " AND NOT " + StillingSolrBeanFieldNames.MEDIUMTEKST + ":[* TO *])";
    }

    /**
     * Retrieves all SOLR ads and saves new and updatef ads
     * @param saveAllFetchedAds whether to save all fetched ads, or only ads that have been modified
     * @return list of all active ads in SillingSOLR
     */
    public List<Stilling> saveNewAndUpdatedStillingerFromSolr(boolean saveAllFetchedAds) {
        List<Stilling> allStillingerFromSolr = searchForStillinger();
        List<Stilling> newStillinger = new ArrayList();
        List<Stilling> changedStillinger = new ArrayList();
        List<Stilling> unchangedStillinger = new ArrayList<>();

        allStillingerFromSolr.forEach(s -> {
            Optional<Stilling> inDb = stillingRepository.findByKildeAndMediumAndExternalId(
                    s.getKilde(),
                    s.getMedium(),
                    s.getExternalId());

            if (inDb.isPresent()) {
                if (saveAllFetchedAds
                        || !inDb.get().getHash().equals(s.getHash())
                        || inDb.get().getAnnonseStatus() != AnnonseStatus.AKTIV) {

                    s.merge(inDb.get());
                    changedStillinger.add(s);
                } else {
                    unchangedStillinger.add(inDb.get());
                }
            } else {
                newStillinger.add(s);
            }
        });

        stillingRepository.saveAll(newStillinger);
        stillingRepository.saveAll(changedStillinger);
        annonseFangstService.addMetricsCounters(Kilde.STILLINGSOLR.toString(),
                newStillinger.size(), 0, 0, changedStillinger.size());

        LOG.info("Saved {} new and {} changed ads from stillingsolr total {}",
                newStillinger.size(), changedStillinger.size(), allStillingerFromSolr.size());

        List<Stilling> allStillinger = new ArrayList();
        allStillinger.addAll(newStillinger);
        allStillinger.addAll(changedStillinger);
        allStillinger.addAll(unchangedStillinger);

        return allStillinger;
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
        solrQuery.addFilterQuery(buildFilterQueryKildetekst());
        solrQuery.setFacet(false);
        solrQuery.setStart(0);
        solrQuery.setRows(100);

        solrQuery.add("pam", "pam");

        return solrQuery;
    }

    private static boolean notPamDirStillinger(StillingSolrBean b) {
        return b.getStillingsbeskrivelse() == null || !b.getStillingsbeskrivelse().contains(PAM_DIR_ADTEXT_COOKIE);
    }

}
