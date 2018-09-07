package no.nav.pam.annonsemottak.stilling;

import com.google.common.collect.ImmutableMap;
import com.google.common.hash.Hashing;
import no.nav.pam.annonsemottak.ModelEntity;
import no.nav.pam.annonsemottak.app.sensu.SensuClient;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Entity
@Table(name = "STILLING")
@SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 1)
public class Stilling extends ModelEntity {

    private static final int DEFAULT_EXPIRY_DAYS = 10;
    private static final int MAX_EXPIRY_LIMIT  = 6;

    private static final Set<String> NONIDENTIFYING_KEYS;

    static {
        Set<String> s = new HashSet<>();
        s.add("hash");
        NONIDENTIFYING_KEYS = Collections.unmodifiableSet(s);
    }

    @Embedded
    private Saksbehandling saksbehandling = new Saksbehandling();

    @NotNull
    private String uuid;

    private String place;

    private String title;

    @NotNull
    private String employer;

    private String dueDate;

    @NotNull
    private String kilde;

    @NotNull
    private String medium;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String employerDescription;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String jobDescription;

    @NotNull
    private String hash;

    private String url;

    private String externalId;


    private LocalDateTime published = null;

    @Enumerated(EnumType.STRING)
    private AnnonseStatus annonseStatus = AnnonseStatus.AKTIV;

    private LocalDateTime expires = LocalDateTime.now().plusDays(10);

    @Transient
    private LocalDateTime systemModifiedDate;

    @ElementCollection(fetch = FetchType.LAZY)
    @MapKeyColumn(name = "PROPERTIES_KEY")
    @Column(name = "PROPERTIES_VALUE", nullable = false)
    @BatchSize(size = 500)
    @CollectionTable(
            name = "STILLING_PROPERTIES",
            joinColumns = @JoinColumn(name = "STILLING_ID"),
            uniqueConstraints = {
                    @UniqueConstraint(columnNames = {"STILLING_ID", "PROPERTIES_KEY"})
            })
    private Map<String, String> properties = new HashMap<>();

    /**
     * Default constructor som er påkrevd for Hibernate.
     */
    protected Stilling() {
    }

    /**
     * Constructor used when mapping manually registered ads to a {@link Stilling}. These ads already have a UUID, and
     * are always from the source/medium "Stillingsregistrering"/"Stillingsregistrering".
     *
     * @param uuid                Pre-existing UUID.
     * @param employerName        Name.
     * @param employerDescription Description.
     * @param jobTitle            Title
     * @param jobLocation         Geographic location, i.e. town.
     * @param jobDescription      Assumed to be HTML.
     * @param applicationDeadline Random format.
     * @param kilde               String "Stillingsregistrering".
     * @param medium              String "Stillingsregistrering".
     * @param expires             sistePubliseringsDato.
     * @param properties          Other properties.
     */
    public Stilling(
            String uuid,
            String employerName,
            String employerDescription,
            String jobTitle,
            String jobLocation,
            String jobDescription,
            String applicationDeadline,
            String kilde,
            String medium,
            LocalDateTime expires,
            Map<String, String> properties
    ) {
        this(jobTitle, jobLocation, employerName, employerDescription,
                jobDescription, applicationDeadline, kilde, medium, null, uuid,
                expires, properties, null);
        this.uuid = uuid;
    }


    // very big constructor, fix later.
    public Stilling(String stillingstittel, String arbeidssted, String arbeidsgiver, String arbeidsgiveromtale,
                    String annonsetekst, String søknadsfrist, String kilde, String medium, String url, String externalId,
                    LocalDateTime expires, Map<String, String> metaData, LocalDateTime systemModifiedDate) {
        this.uuid = UUID.randomUUID().toString();
        this.title = stillingstittel;
        this.place = arbeidssted;
        this.employer = arbeidsgiver;
        this.employerDescription = arbeidsgiveromtale;
        this.jobDescription = annonsetekst;
        this.dueDate = søknadsfrist;
        this.kilde = kilde;
        this.medium = medium;
        this.url = url;
        this.externalId = externalId;
        this.properties = metaData;
        if (expires != null && expires.isBefore(LocalDateTime.now().plusMonths(MAX_EXPIRY_LIMIT))) {
            this.expires = expires;
        } else {
            // temporally only for back compatibility, will remove this as soon as expires is no longer null in database.
            this.expires = this.getCreated().plusDays(DEFAULT_EXPIRY_DAYS);
        }
        this.hash = hash();
        this.systemModifiedDate = systemModifiedDate;

    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setEmployerDescription(String employerDescription) {
        this.employerDescription = employerDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    private String hash() {
        Map<String, String> nonIdentifyingProperties = new HashMap<>();
        nonIdentifyingProperties.putAll(properties);
        nonIdentifyingProperties.keySet().removeAll(NONIDENTIFYING_KEYS);

        String input = new StringBuilder()
                .append(title)
                .append(place)
                .append(dueDate)
                .append(employer)
                .append(employerDescription)
                .append(jobDescription)
                .append(nonIdentifyingProperties
                        .values()
                        .stream()
                        .collect(Collectors.joining(",")))
                .toString();
        return Hashing.sha256().hashString(input, StandardCharsets.UTF_8).toString();
    }

    public String getUuid() {
        return uuid;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public String getArbeidssted() {
        return place;
    }

    public Optional<Arbeidsgiver> getArbeidsgiver() {
        return Arbeidsgiver.ofNullable(employer);
    }

    public String getAnnonsetekst() {
        return jobDescription;
    }

    public String getStillingstittel() {
        return title;
    }

    /**
     * Returnerer søknadsfristen til en gitt stillingsannonse.
     * </p>
     * Merk! Søknadsfrist er en String, da feltet kan innehole både dato og "SNAREST".
     */
    public String getSoeknadsfrist() {
        return dueDate;
    }

    public String getArbeidsgiveromtale() {
        return employerDescription;
    }

    public String getHash() {
        return hash;
    }

    public void oppdaterMed(OppdaterSaksbehandlingCommand command)
            throws IllegalSaksbehandlingCommandException {
        this.saksbehandling.oppdaterMed(command, this);
    }

    public String getKilde() {
        return kilde;
    }

    public String getMedium() {
        return medium;
    }

    Saksbehandling getSaksbehandling() {
        return saksbehandling;
    }

    public Status getStatus() {
        return saksbehandling.getStatus();
    }

    public Optional<Saksbehandler> getSaksbehandler() {
        return saksbehandling.getSaksbehandler();
    }

    public Optional<Merknader> getMerknader() {
        return saksbehandling.getMerknader();
    }

    public Optional<Kommentarer> getKommentarer() {
        return saksbehandling.getKommentarer();
    }

    public String getUrl() {
        return url;
    }

    public LocalDateTime getExpires() {
        return expires;
    }

    public LocalDateTime getSystemModifiedDate() {
        return systemModifiedDate;
    }

    @Override
    public String toString() {
        return "[UUID: " + uuid + "]" +
                "[Arbeidsgiver: " + employer + "]" +
                "[Annonsetittel: " + title + "]";
    }

    public String getExternalId() {
        return externalId;
    }

    public Stilling stop() {
        this.annonseStatus = AnnonseStatus.STOPPET;
        return this;
    }

    public Stilling deactivate() {
        this.annonseStatus = AnnonseStatus.INAKTIV;
        return this;
    }

    public Stilling reset() {
        this.saksbehandling.resetSaksbehandler();
        return this;
    }

    public void rejectAsDuplicate(Integer id) {
        this.saksbehandling.rejectAsDuplicate(id);
        SensuClient.sendEvent(
                "stillingAvvistDuplikat.event",
                Collections.emptyMap(),
                ImmutableMap.of("kilde", this.kilde));
    }

    public void rejectBecauseOfCapasity() {
        this.deactivate();
        this.saksbehandling.rejectBecauseOfCapasity();
    }

    public LocalDateTime getPublished() {
        return published;
    }

    public void setPublished(LocalDateTime published) {
        if (this.published != null)
            throw new IllegalArgumentException("Published er allerede satt. Kan ikke overskrives");
        this.published = published;
    }

    public AnnonseStatus getAnnonseStatus() {
        return annonseStatus;
    }

    public Stilling merge(Stilling stilling) {
        Status oldStatus = stilling.getSaksbehandling().getStatus();

        if ((oldStatus == Status.GODKJENT || oldStatus == Status.FJERNET) && this.annonseStatus != AnnonseStatus.STOPPET) {
            this.getSaksbehandling().oppdatert();
        } else {
            this.saksbehandling = stilling.getSaksbehandling();
        }
        this.setId(stilling.getId());
        this.uuid = stilling.getUuid();
        this.setCreated(stilling.getCreated());
        if (this.getPublished() == null) { // Paranoid, avoid IAE in case something has already set published.. (What could possibly go wrong.)
            this.setPublished(stilling.getPublished());
        }
        return this;
    }
}
