package no.nav.pam.annonsemottak.annonsemottak.solr.rest;


import no.nav.pam.annonsemottak.annonsemottak.solr.SolrService;
import no.nav.pam.annonsemottak.annonsemottak.solr.StillingSolrBean;
import no.nav.pam.annonsemottak.annonsemottak.solr.fetch.SolrFetchService;
import no.nav.pam.annonsemottak.annonsemottak.solr.fetch.StillingSolrBeanFieldNames;
import no.nav.pam.annonsemottak.api.PathDefinition;
import no.nav.pam.annonsemottak.stilling.Stilling;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping(PathDefinition.INTERNAL + "/solr")
public class SolrSearchInternal {

    private static final Logger LOG = LoggerFactory.getLogger(SolrSearchInternal.class);

    private final SolrService solrService;
    private final SolrFetchService solrFetchService;

    @Autowired
    public SolrSearchInternal(SolrService solrService, SolrFetchService solrFetchService) {
        this.solrService = solrService;
        this.solrFetchService = solrFetchService;
    }

    @RequestMapping(value = "/title/{title}/employer/{employer}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StillingSolrBean>> searchTitleAndText(@PathVariable("title") String title, @PathVariable("employer") String employer) {
        HashMap<String, String> params = new HashMap<>();
        params.put(StillingSolrBeanFieldNames.ARBEIDSGIVERNAVN, employer);
        params.put(StillingSolrBeanFieldNames.TITTEL, title);
        List<StillingSolrBean> stillingSolrBeans = solrService.searchStillinger(params);
        return ResponseEntity.ok(stillingSolrBeans);
    }

    @PostMapping(path = "/fetch/since/{time}")
    public ResponseEntity fetchStillinger(@PathVariable("time") String time) {
        LOG.debug("Starting saving new stillinger from solr since {}", time);
        List<Stilling> solrBeans = solrFetchService.saveNewStillingerFromSolr(new DateTime(time));
        LOG.debug("Finished saving stillinger");
        return ResponseEntity.ok(solrBeans);
    }
}

