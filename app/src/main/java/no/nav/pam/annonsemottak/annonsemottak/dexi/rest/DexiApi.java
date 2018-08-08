package no.nav.pam.annonsemottak.annonsemottak.dexi.rest;

import no.nav.pam.annonsemottak.annonsemottak.common.rest.payloads.ResultsOnSave;
import no.nav.pam.annonsemottak.annonsemottak.dexi.DexiConfiguration;
import no.nav.pam.annonsemottak.annonsemottak.dexi.DexiException;
import no.nav.pam.annonsemottak.annonsemottak.dexi.DexiService;
import no.nav.pam.annonsemottak.api.PathDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(PathDefinition.DEXI)
public class DexiApi {

    private static final Logger LOG = LoggerFactory.getLogger(DexiApi.class);

    private final DexiService dexiService;

    @Inject
    public DexiApi(DexiService dexiService) {
        this.dexiService = dexiService;
    }

    @RequestMapping(value = "/configurations", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getConfigurations() {
        try {
            List<DexiConfiguration> dexiConfigurations = dexiService.getProductionConfigurations().stream()
                    .map(m -> new DexiConfiguration(
                            m.getRobotId(),
                            m.getRobotName(),
                            m.getJobId(),
                            m.getJobName()))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(dexiConfigurations);
        } catch (Exception e) {
            LOG.error("Unable to get configurations", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @RequestMapping(value = "/configurations/results/save", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity saveLatestResultsFromAllJobs() {
        try {
            ResultsOnSave dexiResult = dexiService.saveLatestResultsFromAllJobs();
            return ResponseEntity.ok(dexiResult);
        } catch (DexiException e) {
            LOG.error("Unable to save latest results for all robots, latest attempted robot was '{}'", e.getCurrentRobotName(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @RequestMapping(value = "/robots/{robotName}/configurations/{configuration}/results",
            method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity saveLatestRobotRunResult(@PathVariable("robotName") String robotName,
                                                   @PathVariable("configuration") String configuration) {
        LOG.info("Got save request for '{}' with configuration '{}'",robotName, configuration);
        try {
            ResultsOnSave dexiResult = dexiService.saveLatestResultsForRobot(robotName, configuration);
            return ResponseEntity.ok(dexiResult);
        }
        catch (Exception e) {
            LOG.error("Unable to save result for robot {}", robotName, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}