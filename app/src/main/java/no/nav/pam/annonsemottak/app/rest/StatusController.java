package no.nav.pam.annonsemottak.app.rest;

import no.nav.pam.annonsemottak.annonsemottak.solr.SolrRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatusController {

    private static final Logger LOG = LoggerFactory.getLogger(StatusController.class);

    @Autowired
    private SolrRepository solrRepository;

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

        LOG.info("testing solr connection");
        if (solrRepository.status()<0) {
            return "NOTOK";
        }
        return "OK";
    }

}
