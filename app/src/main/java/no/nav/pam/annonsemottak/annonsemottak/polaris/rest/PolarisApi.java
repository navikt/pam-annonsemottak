package no.nav.pam.annonsemottak.annonsemottak.polaris.rest;


import no.nav.pam.annonsemottak.annonsemottak.polaris.PolarisService;
import no.nav.pam.annonsemottak.api.PathDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping(PathDefinition.POLARIS)
public class PolarisApi {

    private static final Logger LOG = LoggerFactory.getLogger(PolarisApi.class);

    private final PolarisService polarisService;

    @Autowired
    public PolarisApi(PolarisService polarisService) {
        this.polarisService = polarisService;
    }

    @PostMapping("/results/save")
    public ResponseEntity fetchLatestAds() {
        try {
            polarisService.fetchLatest();
            return ResponseEntity.ok("TODO");
        } catch (Exception e) {
            LOG.error("Unable to save results from Polaris", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
