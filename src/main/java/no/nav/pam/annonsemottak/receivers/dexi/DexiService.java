package no.nav.pam.annonsemottak.receivers.dexi;

import no.nav.pam.annonsemottak.app.metrics.AnnonseMottakProbe;
import no.nav.pam.annonsemottak.receivers.Kilde;
import no.nav.pam.annonsemottak.receivers.common.rest.payloads.ResultsOnSave;
import no.nav.pam.annonsemottak.receivers.fangst.AnnonseResult;
import no.nav.pam.annonsemottak.receivers.fangst.DexiAnnonseFangstService;
import no.nav.pam.annonsemottak.stilling.Stilling;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.inject.Inject;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class DexiService {

    private static final Logger LOG = LoggerFactory.getLogger(DexiService.class);

    private final AnnonseMottakProbe probe;
    private final DexiConnector dexiConnector;
    private final DexiAnnonseFangstService annonseFangstService;

    @Inject
    public DexiService(
            DexiConnector dexiConnector,
            DexiAnnonseFangstService annonseFangstService,
            AnnonseMottakProbe probe
    ) {
        this.dexiConnector = dexiConnector;
        this.annonseFangstService = annonseFangstService;
        this.probe = probe;
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
            } catch (IOException ioe) {
                LOG.warn("Failed to get entries from robot " + currentRobotName, ioe);

            } catch (Exception e) {
                LOG.error("An unexpected failure occurred while fetching entries from robot " + currentRobotName, e);
                probe.newFailedPoint(Kilde.DEXI.toString(), currentRobotName);
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
                .filter(map -> !(map.containsKey("error") && map.get("error") != null))
                .collect(Collectors.toList());

        if (nonErrorResult.isEmpty()) {
            throw new IOException("Robot " + robotName + " returned an error response");
        } else if (nonErrorResult.size() < dexiResult.size()) {
            LOG.warn("There was an error among the results for robot {}", robotName);
        }

        // Convert dexi result to Stilling
        List<Stilling> mapped = nonErrorResult.stream()
                .map(m -> DexiModel.toStilling(m, robotName))
                .filter(Objects::nonNull)
                .filter(s -> StringUtils.isNoneBlank(s.getExternalId()))
                .collect(Collectors.toList());

        LOG.info("Retrieved {} ads, {} after filtering empty or null", nonErrorResult.size(), mapped.size());

        // Sort results into lists of "New", "Modified", "Stopped" and then persist all lists in one transaction.
        AnnonseResult annonseResult = annonseFangstService.retrieveAnnonseLists(mapped, DexiConfiguration.KILDE, robotName);
        annonseFangstService.saveAll(annonseResult);

        probe.addMetricsCounters(
                Kilde.DEXI.toString(), robotName, annonseResult.getNewList().size(),
                annonseResult.getStopList().size(),
                annonseResult.getDuplicateList().size(),
                annonseResult.getModifyList().size()
        );

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
