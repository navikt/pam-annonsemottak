package no.nav.pam.annonsemottak.receivers.externalRun;

import no.nav.pam.annonsemottak.PathDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(PathDefinition.INTERNAL + "/externalRun")
public class ExternalRunApi {

    private static final Logger LOG = LoggerFactory.getLogger(ExternalRunApi.class);

    private final ExternalRunService externalRunService;

    public ExternalRunApi(ExternalRunService externalRunService) {
        this.externalRunService = externalRunService;
    }


    @GetMapping()
    public ResponseEntity<Map> fetchAllExternalRuns() {
        Map<String, LocalDateTime> externalRunsMap = new HashMap();

        Iterable<ExternalRun> iterable = externalRunService.retrieveAll();
        iterable.forEach(s -> externalRunsMap.put(s.getName(), s.getLastRun()));


        return ResponseEntity.ok(externalRunsMap);
    }

    @GetMapping("/{name}")
    public ResponseEntity<LocalDateTime> fetch(@PathVariable("name") String name) {
        return ResponseEntity.ok(externalRunService.findLastRunForRunName(name));
    }

    @PostMapping("/{name}/date/{date}")
    public ResponseEntity saveDate(@PathVariable("name") String name,
                                   @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime lastRunDate) {

        ExternalRun run = externalRunService.retrieveExternalRun(name);

        if (run != null) {
            run.setLastRun(lastRunDate);
        } else {
            run = new ExternalRun(name, name, lastRunDate);
        }

        LOG.info("Saving external run for {} with date {}", name, lastRunDate);
        externalRunService.save(run);

        return ResponseEntity.ok().build();
    }
}
