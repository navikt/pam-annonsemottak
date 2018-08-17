package no.nav.pam.annonsemottak.temp.feedclient.rest;

import no.nav.pam.annonsemottak.temp.feedclient.FeedClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/internal/pamstilling/feedclient")
public class FeedClientController {

    private static final Logger LOG = LoggerFactory.getLogger(FeedClientController.class);

    private final FeedClientService feedClientService;

    @Autowired
    public FeedClientController(FeedClientService feedClientService) {
        this.feedClientService = feedClientService;
    }

    @PostMapping("/{uuid}")
    public ResponseEntity saveOneAd(@PathVariable("uuid") String uuid) {
        LOG.debug("REST request to save ad with uuid: {} from feed", uuid);

        try {
            feedClientService.fetchAndSaveOneAd(uuid);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            LOG.error("Error while saving ad with uuid {} from feed", uuid);
            return ResponseEntity.status(500).build();
        }
    }
}
