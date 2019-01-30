package no.nav.pam.annonsemottak.receivers.xmlstilling.rest;

import no.nav.pam.annonsemottak.PathDefinition;
import no.nav.pam.annonsemottak.receivers.amedia.AmediaService;
import no.nav.pam.annonsemottak.receivers.common.rest.payloads.ResultsOnSave;
import no.nav.pam.annonsemottak.receivers.xmlstilling.XmlStillingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

/**
 * Henter stillinger fra xml-stilling og legger de inn i stillingsdatabasen
 */
@RestController
@RequestMapping(PathDefinition.XML_STILLING)
public class XmlStillingApi {

    private static final Logger LOG = LoggerFactory.getLogger(
        XmlStillingApi.class);

    private final XmlStillingService service;

    @Inject
    public XmlStillingApi(XmlStillingService service) {
        this.service = service;
    }

    @RequestMapping(
        value = "/results/save",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateLatest() {
        try {
            service.updateLatest();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            LOG.error("Unable to save results from xml-stilling using specified collections", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
