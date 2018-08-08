package no.nav.pam.annonsemottak.annonsemottak.dexi;

import com.google.common.collect.ImmutableMap;
import no.nav.pam.annonsemottak.annonsemottak.common.rest.payloads.ResultsOnSave;
import no.nav.pam.annonsemottak.annonsemottak.fangst.AnnonseResult;
import no.nav.pam.annonsemottak.annonsemottak.fangst.DexiAnnonseFangstService;
import no.nav.pam.annonsemottak.app.sensu.SensuClient;
import no.nav.pam.annonsemottak.stilling.Stilling;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DexiService {

    private static final Logger LOG = LoggerFactory.getLogger(DexiService.class);
    private final DexiConnector dexiConnector;
    private DexiAnnonseFangstService annonseFangstService;

    @Inject
    public DexiService(DexiConnector dexiConnector, DexiAnnonseFangstService annonseFangstService) {
        this.dexiConnector = dexiConnector;
        this.annonseFangstService = annonseFangstService;
    }

    public ResultsOnSave saveLatestResultsFromAllJobs() throws DexiException {
        String currentRobotName = "unknown";
        int received = 0;
        int saved = 0;
        long start = System.currentTimeMillis();

        List<DexiConfiguration> dexiConfigurations;
        try {
            dexiConfigurations = getProductionConfigurations();
        } catch (IOException e) {
            throw new DexiException(currentRobotName, e);
        }

        for (DexiConfiguration configuration : dexiConfigurations) {
            currentRobotName = configuration.getRobotName();
            try {
                ResultsOnSave results = persistNewData(configuration.getJobId(), currentRobotName);
                LOG.debug("Got {} entries from robot {}, new {} entries", results.getReceived(), currentRobotName, results.getSaved());
                received += results.getReceived();
                saved += results.getSaved();
            } catch (Exception e) {
               LOG.error("Failed to get entries from robot " + currentRobotName, e);
               SensuClient.sendEvent("dexiRobotFeilet.event", Collections.emptyMap(), ImmutableMap.of("jobId", configuration.getJobId(), "robotName", currentRobotName));
            }
        }

        return new ResultsOnSave(received, saved, System.currentTimeMillis() - start);
    }

    private ResultsOnSave persistNewData(String id, String robotName)
            throws IOException {
        LOG.debug("Getting results for {}", robotName);
        long start = System.currentTimeMillis();

        List<Map<String, String>> dexiResult = dexiConnector.getLatestResultForJobID(id);
        List<Map<String, String>> nonErrorResult = dexiResult.stream()
                .filter(map -> !(map.containsKey("error") && map.get("error")!= null))
                .collect(Collectors.toList());

        if(nonErrorResult.isEmpty()){
            throw new IOException("Robot " + robotName + " returned an error response");
        } else if (nonErrorResult.size() < dexiResult.size()){
            LOG.warn("There was an error among the results for robot {}: {}", robotName);
        }

        // Convert dexi result to Stilling
        List<Stilling> mapped = nonErrorResult.stream()
                .map(m -> DexiModel.toStilling(m, robotName))
                .filter(Objects::nonNull)
                .filter(s -> StringUtils.isNoneBlank(s.getExternalId()))
                .collect(Collectors.toList());

        LOG.info("Retrieved {} ads, {} after filtering empty or null", nonErrorResult.size(), mapped.size());

        // Sort results into lists of "New", "Modified", "Stopped" and then persist all lists in one transaction.
        AnnonseResult annonseResult =  annonseFangstService.retrieveAnnonseLists(mapped, DexiConfiguration.KILDE, robotName);
        annonseFangstService.saveAll(annonseResult);

        // TODO tags on sensu should be rename, "mapped, filtered" does not say anything.
        SensuClient.sendEvent("dexiStillingerHentet.event", Collections.emptyMap(), ImmutableMap.of(
                "total", mapped.size(),
                "new", annonseResult.getNewList().size(),
                "stopped", annonseResult.getStopList().size(),
                "jobId", id,
                "robotName", robotName));
        return new ResultsOnSave(mapped.size(), annonseResult.getNewList().size(), System.currentTimeMillis() - start);
    }

    public List<DexiConfiguration> getProductionConfigurations()
            throws IOException {
        return dexiConnector.getConfigurations(DexiConfiguration.PRODUCTION);
    }

    public ResultsOnSave saveLatestResultsForRobot(String robotName, String configuration)
            throws IOException, DexiException {

        DexiConfiguration conf = dexiConnector
                .getConfigurations(configuration)
                .stream()
                .filter(c -> c.getRobotName().equals(robotName))
                .findFirst()
                .orElse(null);
        if (conf == null) {
            throw new DexiException(robotName, "Cannot get configuration");
        }
        return persistNewData(conf.getJobId(), conf.getRobotName());

    }
}
