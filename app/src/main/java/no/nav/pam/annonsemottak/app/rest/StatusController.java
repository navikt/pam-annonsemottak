package no.nav.pam.annonsemottak.app.rest;

import no.nav.pam.annonsemottak.annonsemottak.amedia.AmediaConnector;
import no.nav.pam.annonsemottak.annonsemottak.dexi.DexiConnector;
import no.nav.pam.annonsemottak.annonsemottak.finn.FinnConnector;
import no.nav.pam.annonsemottak.annonsemottak.solr.SolrRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class StatusController {

    private static final Logger LOG = LoggerFactory.getLogger(StatusController.class);

    @Autowired
    private SolrRepository solrRepository;

    @Autowired
    private FinnConnector finnConnector;

    @Autowired
    private DexiConnector dexiConnector;

    @Autowired
    private AmediaConnector amediaConnector;


    @GetMapping(path = "/isAlive")
    public String isAlive() {
        return "OK";
    }

    @GetMapping(path = "/isReady")
    public String isReady() {
        return "OK";
    }

    @GetMapping(path = "/amIOK")
    public String amIOk() {

        if (isSolrOK()
                && isDexiOK()
                && isAmediaOK()
                && isFinnOK()) {
            return "OK";
        }

        return "NOT OK";
    }

    @GetMapping(path = "/isSourcePingOK")
    public ResponseEntity pingSourcesAndGetStatus() {

        Map<String, String> statusMap = new HashMap();
        statusMap.put("Stillingsolr", statusToString(isSolrOK()));
        statusMap.put("DEXI", statusToString(isDexiOK()));
        statusMap.put("Finn", statusToString(isFinnOK()));
        statusMap.put("Amedia", statusToString(isAmediaOK()));

        return ResponseEntity.ok(statusMap);
    }

    private String statusToString(boolean value) {
        return (value) ? "OK" : "NOT OK";
    }

    private boolean isSolrOK() {
        return (solrRepository.status() > -1);
    }

    private boolean isFinnOK() {
        return finnConnector.isPingSuccessful();
    }

    private boolean isDexiOK() {
        return dexiConnector.isPingSuccessful();
    }

    private boolean isAmediaOK() {
        return amediaConnector.isPingSuccessful();
    }
}
