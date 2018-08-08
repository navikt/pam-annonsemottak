package no.nav.pam.annonsemottak.feed;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.pam.annonsemottak.api.PathDefinition;
import no.nav.pam.annonsemottak.stilling.Arbeidsgiver;
import no.nav.pam.annonsemottak.stilling.Kommentarer;
import no.nav.pam.annonsemottak.stilling.Merknader;
import no.nav.pam.annonsemottak.stilling.Saksbehandler;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(PathDefinition.STILLINGER + "/feed")
@Transactional
public class StillingFeedApi {

    private static final Logger LOG = LoggerFactory.getLogger(StillingFeedApi.class);

    private final StillingFeedService stillingFeedService;
    private final ObjectMapper objectMapper;

    @Inject
    public StillingFeedApi(StillingFeedService stillingFeedService, ObjectMapper objectMapper) {

        this.stillingFeedService = stillingFeedService;
        this.objectMapper = objectMapper;

        // Following classes are wrapped in an Optional and result in nested objects in JSON
        // Removes nesting and assigns String value directly to the property
        this.objectMapper.addMixIn(Arbeidsgiver.class, OptionalValueMixIn.class);
        this.objectMapper.addMixIn(Kommentarer.class, OptionalValueMixIn.class);
        this.objectMapper.addMixIn(Merknader.class, OptionalValueMixIn.class);
        this.objectMapper.addMixIn(Saksbehandler.class, OptionalValueMixIn.class);
    }


    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity getFeed(
            @RequestParam(value = "millis", required = false, defaultValue = "0") long millis,
            Pageable pageable) {

        if (millis > 0) {
            DateTime updatedDate = new DateTime(millis);
            LOG.info("Fetching feed for ads updates after " + updatedDate.toString());

            return ResponseEntity.ok(
                    stillingFeedService.findStillingUpdatedAfter(updatedDate, pageable).map(objectMapper::valueToTree)
            );

        } else {
            LOG.info("Fetching feed for all ads");

            return ResponseEntity.ok(
                    stillingFeedService.findAllActive(pageable).map(objectMapper::valueToTree)
            );
        }
    }

    @GetMapping(path = "/{uuid}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity getOneAdAsFeed(@PathVariable("uuid") String uuid) {

        LOG.info("Fetching feed for an ad with uuid {}", uuid);

        return ResponseEntity.ok(stillingFeedService.findStilling(uuid));

    }
}
