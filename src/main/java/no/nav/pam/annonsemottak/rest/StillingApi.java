package no.nav.pam.annonsemottak.rest;

import no.nav.pam.annonsemottak.app.metrics.AnnonseMottakProbe;
import no.nav.pam.annonsemottak.outbox.StillingOutboxService;
import no.nav.pam.annonsemottak.receivers.Kilde;
import no.nav.pam.annonsemottak.receivers.common.PropertyNames;
import no.nav.pam.annonsemottak.PathDefinition;
import no.nav.pam.annonsemottak.receivers.fangst.AnnonseFangstService;
import no.nav.pam.annonsemottak.stilling.*;
import no.nav.pam.annonsemottak.rest.dto.StillingDTO;
import no.nav.pam.annonsemottak.rest.payloads.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static no.nav.pam.annonsemottak.stilling.AnnonsehodePageRequest.withPageRequest;
import static no.nav.pam.annonsemottak.stilling.AnnonsehodeSpecification.withSpecification;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@RestController
@RequestMapping(PathDefinition.STILLINGER)
@Transactional
public class StillingApi {

    private static final Logger LOG = LoggerFactory.getLogger(StillingApi.class);

    private final StillingRepository stillingRepository;
    private final StillingOutboxService stillingOutboxService;
    private final AnnonseMottakProbe probe;


    @Autowired
    public StillingApi(
            StillingRepository stillingRepository,
            AnnonseFangstService annonseFangstService,
            StillingOutboxService stillingOutboxService,
            AnnonseMottakProbe probe) {
        this.stillingRepository = stillingRepository;
        this.stillingOutboxService = stillingOutboxService;
        this.probe = probe;
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity getListOfAnnonsehode(
            @RequestParam(value = "arbeidsgiver", required = false) String arbeidsgiver,
            @RequestParam(value = "arbeidssted", required = false) String arbeidssted,
            @RequestParam(value = "stillingstittel", required = false) String stillingstittel,
            @RequestParam(value = "saksbehandler", required = false) String saksbehandler,
            @RequestParam(value = "kilde", required = false) String kilde,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "annonsestatus", required = false) String annonsestatus,
            @RequestParam(value = "page", required = false) String page,
            @RequestParam(value = "size", required = false) String size,
            @RequestParam(value = "orderBy", required = false) String orderBy,
            @RequestParam(value = "orderDirection", required = false) String orderDirection) {
        try {

            return ResponseEntity
                    .ok(
                            new PaginatedPayload<>(
                                    stillingRepository
                                            .findAll(
                                                    withSpecification(arbeidsgiver, arbeidssted, stillingstittel, saksbehandler, kilde, status, annonsestatus),
                                                    withPageRequest(page, size, orderBy, orderDirection)
                                            )
                                            .map(new AnnonsehodeConverter())
                            )
                    );


        } catch (Exception e) {
            LOG.error("Failed to serve list of Annonsehode", e);
            return ResponseEntity
                    .status(INTERNAL_SERVER_ERROR)
                    .body(new ErrorPayload(ErrorPayload.DefinedErrors.SERVER_INTERNAL));
        }
    }

    @GetMapping(value = "/{uuid}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<BasicPayload> getAnnonse(@PathVariable("uuid") String uuid) {
        try {

            Optional<Stilling> stilling = stillingRepository.findByUuid(uuid);

            return stilling.<ResponseEntity<BasicPayload>>map(s -> ResponseEntity
                    .ok(new EtaggedPayload<>(StillingPayload.fromStilling(s))))
                    .orElseGet(() -> ResponseEntity.status(NOT_FOUND).build());

        } catch (Exception e) {
            LOG.error("Failed to get Stilling with UUID {}", uuid, e);
            return ResponseEntity
                    .status(INTERNAL_SERVER_ERROR)
                    .body(new ErrorPayload(ErrorPayload.DefinedErrors.SERVER_INTERNAL));
        }
    }


    @GetMapping(value = "/uuids", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity getAllKnownUuids() {
        try {

            return ResponseEntity.ok(stillingRepository.findUuids());

        } catch (Exception e) {
            LOG.error("Failed to get list of all known UUIDs", e);
            return ResponseEntity
                    .status(INTERNAL_SERVER_ERROR)
                    .body(new ErrorPayload(ErrorPayload.DefinedErrors.SERVER_INTERNAL));
        }
    }

    @GetMapping(value = "/kilde/{kilde}/medium/{medium}/externalid/{externalid}",
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity retrieveByKildeMediumExternalId(@PathVariable("kilde") String kilde,
                                                          @PathVariable("medium") String medium,
                                                          @PathVariable("externalid") String externalid) {
        try {
            Optional<Stilling> stilling = stillingRepository.findByKildeAndMediumAndExternalId(kilde, medium, externalid);

            return stilling.<ResponseEntity>map(s -> ResponseEntity
                    .ok(new EtaggedPayload<>(StillingPayload.fromStilling(s))))
                    .orElseGet(() -> ResponseEntity.status(NOT_FOUND).build());

        } catch (Exception e) {
            LOG.error("Failed to get Stilling {} {} {} ", kilde, medium, externalid);
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new ErrorPayload(ErrorPayload.DefinedErrors.SERVER_INTERNAL));
        }

    }

    @GetMapping(value = "/antallUnderBehandling", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity antallUnderBehandling(String saksbehandler) {
        Long antall = stillingRepository.numberOfActiv(saksbehandler, Status.UNDER_ARBEID);
        return ResponseEntity.ok(antall);
    }

    @PutMapping(consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity addStilling(@RequestBody StillingDTO adDto) {
        try {
            Stilling nyStilling = createEntityFromDto(adDto);
            if ("STOPPET".equals(adDto.getStatus())) {
                nyStilling = nyStilling.stop();
                LOG.info("Saved 1 stopped ad from Stillingsregistrering/SBL");
                probe.addMetricsCounters(Kilde.SBL.toString(), Kilde.SBL.toString(), 0, 1, 0, 0);
            }
            Optional<Stilling> gammelStilling = stillingRepository.findByUuid(adDto.getUuid());
            if (gammelStilling.isPresent()) {
                nyStilling.merge(gammelStilling.get());
                LOG.info("Saved changed ad from Stillingsregistrering/SBL");
                probe.addMetricsCounters(Kilde.SBL.toString(), Kilde.SBL.toString(), 0, 0, 0, 1);
            }
            else {
                LOG.info("Saved new ad from Stillingsregistrering/SBL");
                probe.addMetricsCounters(Kilde.SBL.toString(), Kilde.SBL.toString(), 1, 0, 0, 0);
            }
            Stilling adEntity = stillingRepository.save(nyStilling);
            stillingOutboxService.lagreTilOutbox(adEntity);
            Link linkToCreatedResouce = WebMvcLinkBuilder.linkTo(methodOn(StillingApi.class).getAnnonse(adEntity.getUuid())).withSelfRel();

            LOG.info("Created ad as resource {}", linkToCreatedResouce.getHref());
            return ResponseEntity
                    .created(URI.create(linkToCreatedResouce.getHref()))
                    .build(); // HTTP 201.

        } catch (Exception e) {
            LOG.error("Failed to create ad with incoming UUID {}", null, e);
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).build(); // HTTP 500.
        }
    }

    // NOTE: No HTML-to-Markdown conversion in this case; values assumed to be non-HTML from source.
    private static Stilling createEntityFromDto(StillingDTO ad) {

        Map<String, String> nonEmptyProperties = new HashMap<>(ad.getProperties().size());
        nonEmptyProperties.putAll(
                ad
                        .getProperties()
                        .entrySet()
                        .stream()
                        .filter(entry -> entry.getValue() != null && !entry.getValue().isEmpty())
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
        );
        nonEmptyProperties.put(PropertyNames.EMPLOYER_ORGNR, ad.getOrgNummer());
        nonEmptyProperties.put(PropertyNames.ANTALL_STILLINGER, ad.getAntallStillinger().toString());

        return new StillingBuilder()
                .title(ad.getJobTitle())
                .place(ad.getJobLocation())
                .employer(ad.getEmployerName())
                .employerDescription(ad.getEmployerDescription())
                .jobDescription(ad.getJobDescription())
                .dueDate(ad.getApplicationDeadline())
                .kilde(ad.getKilde())
                .medium(ad.getMedium())
                .url(null)
                .externalId(ad.getUuid())
                .withProperties(nonEmptyProperties)
                .expires(ad.getSistePubliseringsDato())
                .uuid(ad.getUuid())
                .published(ad.getPubliserFra())
                .build();
    }
}
