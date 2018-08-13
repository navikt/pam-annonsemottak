package no.nav.pam.annonsemottak.annonsemottak.finn;

import com.google.common.collect.ImmutableMap;
import no.nav.pam.annonsemottak.annonsemottak.Kilde;
import no.nav.pam.annonsemottak.annonsemottak.Medium;
import no.nav.pam.annonsemottak.annonsemottak.common.PropertyNames;
import no.nav.pam.annonsemottak.annonsemottak.common.rest.payloads.ResultsOnSave;
import no.nav.pam.annonsemottak.annonsemottak.externalRun.ExternalRun;
import no.nav.pam.annonsemottak.annonsemottak.externalRun.ExternalRunService;
import no.nav.pam.annonsemottak.annonsemottak.fangst.AnnonseFangstService;
import no.nav.pam.annonsemottak.annonsemottak.fangst.AnnonseResult;
import no.nav.pam.annonsemottak.app.sensu.SensuClient;
import no.nav.pam.annonsemottak.stilling.Stilling;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A service class for fetching jobs ad from Finn with FinnConnector and persisting
 */
@Service
public class FinnService {
    private static final Logger LOG = LoggerFactory.getLogger(FinnService.class);

    private static final String[] KNOWN_COLLECTIONS = {"job-full-time", "job-part-time", "job-management"};

    private final FinnConnector connector;
    private final AnnonseFangstService finnAnnonseFangstService;
    private final ExternalRunService externalRunService;

    @Inject
    public FinnService(AnnonseFangstService finnAnnonseFangstService, FinnConnector connector, ExternalRunService externalRunService) {
        this.finnAnnonseFangstService = finnAnnonseFangstService;
        this.connector = connector;
        this.externalRunService = externalRunService;
    }

    /**
     * Will retrieve all the active job ads from finn, and save new or changed ads.
     *
     * @param collection
     * @return
     * @throws FinnConnectorException
     */
    public ResultsOnSave saveAndUpdateFromCollection(String collection) throws FinnConnectorException {
        String[] collections = collection == null ? KNOWN_COLLECTIONS : new String[]{collection};

        //Retrieve the date for last successful run
        LocalDateTime lastRun;
        long start = System.currentTimeMillis();
        LOG.info("starting finn fetch {} ", Arrays.toString(collections));
        ExternalRun externalRun = externalRunService.findByNameAndMedium(Kilde.FINN.toString(), Medium.FINN.toString());
        if (externalRun != null && externalRun.getLastRun() != null) {
            lastRun = externalRun.getLastRun();
            externalRun.setLastRun(LocalDateTime.now());
        } else {
            LOG.info("First time fetching ads from FINN");
            lastRun = null;
            externalRun = new ExternalRun(Kilde.FINN.toString(), Medium.FINN.toString(), LocalDateTime.now());
        }


        // Retrieve the search result from finn
        Set<FinnAdHead> searchResult = connector.fetchSearchResult(collections);
        LOG.info("Fetched {} ad heads from search in Finn", searchResult.size());

        Set<FinnAd> retrievedAds;
        if (lastRun != null) {
            //Filter ads that are new (published after last run) or changed after last run
            Set<FinnAdHead> filteredAdHeads = searchResult.stream()
                    //TODO: temporarily switched off, replaced with 1 days ads
                    //.filter(adHead -> adHead.getPublished().compareTo(lastRun) >= 0 || adHead.getUpdated().compareTo(lastRun) >= 0)
                    .filter(adHead -> adHead.getPublished().toLocalDate().isAfter(lastRun.minusDays(1).toLocalDate())
                            || adHead.getUpdated().toLocalDate().isAfter(lastRun.minusDays(1).toLocalDate()))
                    .collect(Collectors.toSet());
            LOG.debug("Filtered search results from Finn. Found {} new and changed ads since last run on {}",
                    filteredAdHeads.size(), lastRun.toString());
            retrievedAds = connector.fetchFullAds(filteredAdHeads);
        } else {
            retrievedAds = connector.fetchFullAds(searchResult);
        }

        // Retrieve filtered ads in detail
        List<Stilling> filteredStillingList = retrievedAds.stream()
                .map(FinnAdMapper::toStilling)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // Create a set of externalIds for all active ads from Finn. Used to determine stopped ads
        Set<String> allExternalIds = searchResult.stream().map(FinnAdHead::getId).collect(Collectors.toSet());

        // Persist retrieved ads
        AnnonseResult annonseResult = finnAnnonseFangstService.retrieveAnnonseLists(filteredStillingList, allExternalIds, Kilde.FINN.toString(), Medium.FINN.toString());
        finnAnnonseFangstService.handleDuplicates(annonseResult);

        // Filter out adecco, webcruiter and other sources
        List<Stilling> rest = annonseResult.getNewList().stream().filter(st -> !ifOneOfFilteredAds(st)).collect(Collectors.toList());
        LOG.info("Excluded {} webcruiter ads ", annonseResult.getNewList().size() - rest.size());

        // Save with filtered new list
        finnAnnonseFangstService.saveAll(new AnnonseResult(annonseResult.getModifyList(), annonseResult.getStopList(),
                annonseResult.getExpiredList(), rest, annonseResult.getDuplicateList()));

        //Save new or update last run time
        externalRunService.save(externalRun);

        SensuClient.sendEvent("finnStillingerHentet.event", Collections.emptyMap(), ImmutableMap.of(
                "total", searchResult.size(),
                //"new", annonseResult.getNewList().size(),
                "new", rest.size(),
                "rejected",  annonseResult.getModifyList().size() - rest.size(),
                "changed", annonseResult.getModifyList().size(),
                "stopped", annonseResult.getStopList().size()));

        return new ResultsOnSave(filteredStillingList.size(), annonseResult.getNewList().size(), System.currentTimeMillis() - start);
    }


    // TODO: Should be removed when temporary exclusion of webcruiter ads is not necessary
    private boolean ifOneOfFilteredAds(Stilling stilling){
        return (isWebcruiterAd(stilling) || isJobbNorgeAd(stilling) || isAdeccoAd(stilling));
    }

    // TODO: Should be removed when temporary exclusion of adecco ads is not necessary
    private boolean isAdeccoAd(Stilling stilling){
        if(stilling.getArbeidsgiver().isPresent()){
            return stilling.getArbeidsgiver().get().asString().toUpperCase().contains("ADECCO");
        } else {
            return false;
        }
    }

    private boolean isWebcruiterAd(Stilling stilling){
        if(stilling.getProperties().containsKey(PropertyNames.SOKNADSLENKE)){
            return stilling.getProperties().get(PropertyNames.SOKNADSLENKE).contains("webcruiter");
        }
        return false;
    }

    private boolean isJobbNorgeAd(Stilling stilling){
        if(stilling.getProperties().containsKey(PropertyNames.SOKNADSLENKE)){
            return stilling.getProperties().get(PropertyNames.SOKNADSLENKE).contains("jobbnorge");
        }
        return false;
    }
}
