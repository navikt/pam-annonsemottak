package no.nav.pam.annonsemottak.receivers.amedia.rest;

import no.nav.pam.annonsemottak.receivers.amedia.AmediaService;
import no.nav.pam.annonsemottak.receivers.common.rest.payloads.ResultsOnSave;
import no.nav.pam.annonsemottak.PathDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import jakarta.inject.Inject;

/**
 * Henter stillinger fra Amedia og legger de inn i stillingsdatabasen
 */
@RestController
@RequestMapping(PathDefinition.AMEDIA)
public class AmediaApi {

    private static final Logger LOG = LoggerFactory.getLogger(
        AmediaApi.class);

    private final AmediaService service;

    @Inject
    public AmediaApi(AmediaService service) {
        this.service = service;
    }

    @RequestMapping(
        value = "/results/save",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity saveAndUpdate() {
        try {
            ResultsOnSave result = service.saveLatestResults();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            LOG.error("Unable to save results from AMEDIA using specified collections", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
