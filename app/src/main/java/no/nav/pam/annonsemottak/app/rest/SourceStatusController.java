package no.nav.pam.annonsemottak.app.rest;

import no.nav.pam.annonsemottak.annonsemottak.amedia.AmediaConnector;
import no.nav.pam.annonsemottak.annonsemottak.dexi.DexiConnector;
import no.nav.pam.annonsemottak.annonsemottak.finn.FinnConnector;
import no.nav.pam.annonsemottak.annonsemottak.solr.SolrRepository;
import no.nav.pam.annonsemottak.api.PathDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(PathDefinition.INTERNAL + "/sources/status")
public class SourceStatusController {

    @Autowired
    private SolrRepository solrRepository;

    @Autowired
    private FinnConnector finnConnector;

    @Autowired
    private DexiConnector dexiConnector;

    @Autowired
    private AmediaConnector amediaConnector;


    @GetMapping(produces = APPLICATION_JSON_VALUE)
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
