package no.nav.pam.annonsemottak.scheduler.deactivate;

import no.nav.pam.annonsemottak.PathDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(PathDefinition.INTERNAL)
public class DeactivateController {

    private static final Logger LOG = LoggerFactory.getLogger(DeactivateController.class);

    private final DeactivateService service;

    @Autowired
    public DeactivateController(DeactivateService service) {
        this.service = service;
    }

    // Denne er ikke i bruk. Den kan brukes som en slags administrativ dødmannsknapp for å deaktivere alle
    // stillingsannonser fra en kilde, men mest sannsynlig så kan den slettes
    @PutMapping("/stopAds/{source}/{medium}")
    public void stopAdsBySourceMedium(@PathVariable String source, @PathVariable String medium,
                                      @RequestParam(required = false, name = "dryRun", defaultValue = "false") Boolean dryRun) {
        LOG.info("REST request for stopping ads for {} {} and dryRun is {}", source, medium, dryRun);
        try {
            service.stopAds(source,medium,dryRun);
        }
        catch (Exception e) {
            LOG.error("Exception while running stopAds from REST", e);
        }
    }

}
