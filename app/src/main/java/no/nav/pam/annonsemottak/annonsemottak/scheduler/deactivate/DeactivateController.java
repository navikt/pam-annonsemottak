package no.nav.pam.annonsemottak.annonsemottak.scheduler.deactivate;

import no.nav.pam.annonsemottak.api.PathDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(PathDefinition.INTERNAL + "/deactivateExpired")
public class DeactivateController {

    private static final Logger LOG = LoggerFactory.getLogger(DeactivateController.class);

    private final DeactivateService service;

    @Autowired
    public DeactivateController(DeactivateService service) {
        this.service = service;
    }

    @PostMapping
    public void deactivateExpiredActiveAds() {
        LOG.info("REST request for deactivating expired active ads in the database.");

        try {
            service.deactivateExpired();
        } catch (Exception e) {
            LOG.error("Exception while running deactivateExpiredActiveAds from REST", e);
        }
    }
}
