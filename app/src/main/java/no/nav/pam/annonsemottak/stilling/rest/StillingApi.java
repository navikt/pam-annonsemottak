package no.nav.pam.annonsemottak.stilling.rest;

import no.nav.pam.annonsemottak.annonsemottak.common.PropertyNames;
import no.nav.pam.annonsemottak.api.PathDefinition;
import no.nav.pam.annonsemottak.dto.StillingDTO;
import no.nav.pam.annonsemottak.stilling.*;
import no.nav.pam.annonsemottak.stilling.rest.payloads.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static no.nav.pam.annonsemottak.stilling.AnnonsehodePageRequest.withPageRequest;
import static no.nav.pam.annonsemottak.stilling.AnnonsehodeSpecification.withSpecification;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(PathDefinition.STILLINGER)
@Transactional
public class StillingApi {

    private static final Logger LOG = LoggerFactory.getLogger(StillingApi.class);

    private static final Map<String, String> ADREG_PROPERTY_KEYS;

    private final StillingRepository stillingRepository;

    // pam-adreg specific property names and corresponding local names
    static {
        ADREG_PROPERTY_KEYS = new HashMap<>();
        ADREG_PROPERTY_KEYS.put("oppstart", PropertyNames.TILTREDELSE);
        ADREG_PROPERTY_KEYS.put("etterAvtale", PropertyNames.TILTREDELSE);
        ADREG_PROPERTY_KEYS.put("omfang", PropertyNames.HELTIDDELTID);
        ADREG_PROPERTY_KEYS.put("stillingstype", PropertyNames.VARIGHET);
        ADREG_PROPERTY_KEYS.put("mottatSoeknadEpost", PropertyNames.APPLICATION_EMAIL);
        ADREG_PROPERTY_KEYS.put("soeknadslenke", PropertyNames.SOKNADSLENKE);
        ADREG_PROPERTY_KEYS.put("hjemmeside", PropertyNames.EMPLOYER_URL);
        ADREG_PROPERTY_KEYS.put("arbeidsstedPostnr", PropertyNames.LOCATION_POSTCODE);
        ADREG_PROPERTY_KEYS.put("arbeidsstedAdresse", PropertyNames.LOCATION_ADDRESS);
        ADREG_PROPERTY_KEYS.put("arbeidsstedSted", PropertyNames.LOCATION_CITY);
        ADREG_PROPERTY_KEYS.put("stillingstittel", PropertyNames.STILLINGSTITTEL);
        ADREG_PROPERTY_KEYS.put("kommuneFylke", PropertyNames.FYLKE);
        ADREG_PROPERTY_KEYS.put("Antall", PropertyNames.ANTALL_STILLINGER);

        //TODO: Standardize contact info
    }

    @Inject
    public StillingApi(StillingRepository stillingRepository) {
        this.stillingRepository = stillingRepository;
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
        nonEmptyProperties.put("orgnr", ad.getOrgNummer());
        nonEmptyProperties.put("publiserFra", ad.getPubliserFra().toString());
        nonEmptyProperties.put(PropertyNames.ANTALL_STILLINGER, ad.getAntallStillinger().toString());

        replacePropertyKeysBeforeSave(nonEmptyProperties);

        return new Stilling(
                ad.getUuid(),
                ad.getEmployerName(),
                ad.getEmployerDescription(),
                ad.getJobTitle(),
                ad.getJobLocation(),
                ad.getJobDescription(),
                ad.getApplicationDeadline(),
                ad.getKilde(),
                ad.getMedium(),
                ad.getSistePubliseringsDato(),
                nonEmptyProperties
        );

    }

    private static void replacePropertyKeysBeforeSave(Map<String, String> properties) {
        LOG.info("Replaces stillingsregistrering (pam-adreg) specific properties into standardized local property names");
        Set<String> foundKeys = properties.keySet().stream().filter(s -> ADREG_PROPERTY_KEYS.containsKey(s)).collect(Collectors.toSet());

        foundKeys.stream().forEach(s -> {
            String value = properties.remove(s);
            properties.put(ADREG_PROPERTY_KEYS.get(s), value);
            LOG.debug("Replaced property names from {} to {} on incoming ad", value, ADREG_PROPERTY_KEYS.get(s));
        });
    }

    @GetMapping(value = "/{uuid}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<BasicPayload> getAnnonse(@PathVariable("uuid") String uuid) {
        try {

            Stilling stilling = stillingRepository.findByUuid(uuid);
            if (stilling == null) {
                return ResponseEntity.status(NOT_FOUND).build();
            }
            return ResponseEntity
                    .ok(new EtaggedPayload<>(StillingPayload.fromStilling(stilling)));

        } catch (Exception e) {
            LOG.error("Failed to get Stilling with UUID {}", uuid, e);
            return ResponseEntity
                    .status(INTERNAL_SERVER_ERROR)
                    .body(new ErrorPayload(ErrorPayload.DefinedErrors.SERVER_INTERNAL));
        }
    }

    @PatchMapping(value = "/{uuid}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity updateSingle(@PathVariable("uuid") String uuid, @RequestBody Map<String, String> keyValueMap) {
        LOG.info("PATCH: {}", uuid);
        try {

            Stilling stilling = stillingRepository.findByUuid(uuid);
            if (stilling == null) {
                return ResponseEntity.status(NOT_FOUND).build();
            }
            stilling.oppdaterMed(new OppdaterSaksbehandlingCommand(keyValueMap));

            stillingRepository.save(stilling);
            return ResponseEntity.noContent().build();

        } catch (IllegalSaksbehandlingCommandException e) {
            LOG.warn("Refused to patch Stilling with UUID={}", uuid, e);
            return ResponseEntity
                    .status(BAD_REQUEST)
                    .body(new ErrorPayload(ErrorPayload.DefinedErrors.CLIENT_ILLEGAL_UPDATE));
        } catch (Exception e) {
            LOG.error("Failed to patch Stilling with UUID={}", uuid, e);
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
            Stilling stilling = stillingRepository.findByKildeAndMediumAndExternalId(kilde, medium, externalid);
            if (stilling == null) {
                return ResponseEntity.status(NOT_FOUND).build();
            }
            return ResponseEntity
                    .ok(new EtaggedPayload<>(StillingPayload.fromStilling(stilling)));
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
            }
            Stilling gammelStilling = stillingRepository.findByUuid(adDto.getUuid());
            if (gammelStilling != null)
                nyStilling.merge(gammelStilling);

            Stilling adEntity = stillingRepository.save(nyStilling);
            Link linkToCreatedResouce = linkTo(methodOn(StillingApi.class).getAnnonse(adEntity.getUuid())).withSelfRel();

            LOG.info("Created ad as resource {}", linkToCreatedResouce.getHref());
            return ResponseEntity
                    .created(URI.create(linkToCreatedResouce.getHref()))
                    .build(); // HTTP 201.

        } catch (Exception e) {
            LOG.error("Failed to create ad with incoming UUID {}", null, e);
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).build(); // HTTP 500.
        }
    }
}