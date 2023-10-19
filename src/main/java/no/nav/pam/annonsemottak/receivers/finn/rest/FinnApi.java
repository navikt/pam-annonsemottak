package no.nav.pam.annonsemottak.receivers.finn.rest;

import jakarta.inject.Inject;
import no.nav.pam.annonsemottak.receivers.common.rest.payloads.ResultsOnSave;
import no.nav.pam.annonsemottak.receivers.finn.FinnService;
import no.nav.pam.annonsemottak.PathDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(PathDefinition.FINN)
public class FinnApi {

    private static final Logger LOG = LoggerFactory.getLogger(FinnApi.class);

    private final FinnService service;

    @Inject
    public FinnApi(FinnService service) {
        this.service = service;
    }

    @RequestMapping(
            value = "/results/save",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity saveAndUpdate() {
        return saveAndUpdateFromCollection(null);
    }

    @RequestMapping(
            value = "/{collection}/results/save",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity saveAndUpdateFromCollection(
            @PathVariable("collection") String collection
    ) {
        try {
            ResultsOnSave result = service.saveAndUpdateFromCollection();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            LOG.error("Unable to save results from Finn using specified collections", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
