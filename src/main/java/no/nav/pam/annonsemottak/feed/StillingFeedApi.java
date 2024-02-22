package no.nav.pam.annonsemottak.feed;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.pam.annonsemottak.PathDefinition;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import jakarta.inject.Inject;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;

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
    }


    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity getFeed(
            @Deprecated @RequestParam(value = "millis", required = false, defaultValue = "0") long millis,
            @RequestParam(value = "updatedSince", required = false) String timestamp,
            Pageable pageable) {

        return ResponseEntity.ok(objectMapper.valueToTree(Page.empty()));

        /* TODO: Legg tilbake dette muligens
        if (StringUtils.isNotBlank(timestamp)) {
            return getFeedByTimestamp(timestamp, pageable);
        } else if (millis > 0) {
            return getFeedByMillis(millis, pageable);
        } else {
            LOG.info("Serving feed for all ads");

            return ResponseEntity.ok(
                    stillingFeedService.findAllActive(pageable).map(objectMapper::valueToTree)
            );
        }
        */
    }

    @Deprecated
    private ResponseEntity getFeedByMillis(long millis, Pageable pageable) {

        Instant instant = Instant.ofEpochMilli(millis);
        LocalDateTime updatedDate = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
        LOG.info("Serving feed for ads updated after " + updatedDate.toString());

        return ResponseEntity.ok(
                stillingFeedService.findStillingUpdatedAfter(updatedDate, pageable).map(objectMapper::valueToTree)
        );
    }

    private ResponseEntity getFeedByTimestamp(String timestamp, Pageable pageable) {
        try {
            LocalDateTime lastUpdatedDate = LocalDateTime.parse(timestamp);
            LOG.info("Serving feed for ads updated after " + lastUpdatedDate.toString());

            return ResponseEntity.ok(
                    stillingFeedService.findStillingUpdatedAfter(lastUpdatedDate, pageable).map(objectMapper::valueToTree)
            );
        } catch (DateTimeParseException dte) {
            LOG.error("Error parsing the given timestamp {}", timestamp);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping(path = "/{uuid}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity getOneAdAsFeed(@PathVariable("uuid") String uuid) {

        LOG.info("Serving feed for ad with uuid {}", uuid);

        return ResponseEntity.ok(stillingFeedService.findStilling(uuid));

    }
}
