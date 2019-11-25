package no.nav.pam.annonsemottak.stilling;

import com.google.common.hash.Hashing;
import no.nav.pam.annonsemottak.receivers.Kilde;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.time.LocalDateTime.now;

@Entity
@Table(name = "STILLING")
@SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 1)
public class Stilling extends ModelEntity {

    private static final int MAX_EXPIRY_LIMIT = 6;

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

    @NotNull
    private String employer;

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

    private String place;
    private String title;
    private String dueDate;
    private String url;
    private String externalId;
    private LocalDateTime published = now();
    private LocalDateTime expires = now().plusDays(10);

    @Enumerated(EnumType.STRING)
    private AnnonseStatus annonseStatus = AnnonseStatus.AKTIV;

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
                jobDescription, applicationDeadline, kilde, medium, null, uuid);

        this.properties.putAll(properties);
        this.setExpires(expires);
        this.uuid = uuid;
    }

    public Stilling(String title, String place, @NotNull String employer, String employerDescription,
                    String jobDescription, String dueDate, @NotNull String kilde, @NotNull String medium,
                    String url, String externalId) {
        this.title = title;
        this.place = place;
        this.employer = employer;
        this.employerDescription = employerDescription;
        this.jobDescription = jobDescription;
        this.dueDate = dueDate;
        this.kilde = kilde;
        this.medium = medium;
        this.url = url;
        this.externalId = externalId;

        this.uuid = UUID.randomUUID().toString();
        this.hash = hash();
    }

    private String hash() {
        Map<String, String> nonIdentifyingProperties = new HashMap<>(properties);
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

    public String getHash() {
        return hash;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getEmployerDescription() {
        return employerDescription;
    }

    public void setEmployerDescription(String employerDescription) {
        this.employerDescription = employerDescription;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public String getPlace() {
        return place;
    }

    public String getTitle() {
        return title;
    }

    public Optional<Arbeidsgiver> getArbeidsgiver() {
        return Arbeidsgiver.ofNullable(employer);
    }


    /**
     * Returnerer søknadsfristen til en gitt stillingsannonse.
     * </p>
     * Merk! Søknadsfrist er en String, da feltet kan innehole både dato og "SNAREST".
     */
    public String getDueDate() {
        return dueDate;
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

    public void setExpires(LocalDateTime expires) {
        if (expires != null && expires.isBefore(now().plusMonths(MAX_EXPIRY_LIMIT))) {
            this.expires = expires;
        }
    }

    public LocalDateTime getSystemModifiedDate() {
        return systemModifiedDate;
    }

    public void setSystemModifiedDate(LocalDateTime systemModifiedDate) {
        this.systemModifiedDate = systemModifiedDate;
    }

    public LocalDateTime getPublished() {
        return published;
    }

    public void setPublished(LocalDateTime published) {
        if (published != null)
            this.published = published;
    }

    public AnnonseStatus getAnnonseStatus() {
        return annonseStatus;
    }

    public String getExternalId() {
        return externalId;
    }


    public void oppdaterMed(OppdaterSaksbehandlingCommand command)
            throws IllegalSaksbehandlingCommandException {
        this.saksbehandling.oppdaterMed(command, this);
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
    }

    public Stilling merge(Stilling stilling) {
        Status oldStatus = stilling.getSaksbehandling().getStatus();

        this.saksbehandling = stilling.getSaksbehandling();
        if ((oldStatus == Status.GODKJENT || oldStatus == Status.FJERNET) && this.annonseStatus != AnnonseStatus.STOPPET) {
            this.getSaksbehandling().oppdatert();
        }
        this.setId(stilling.getId());
        this.uuid = stilling.getUuid();
        this.setCreated(stilling.getCreated());

        return this;
    }

    public Stilling stopIfExpired(Stilling stilling) {

        if(!kilde.equals(Kilde.XML_STILLING.toString())) {
            return this;
        }

        if(stilling.getExpires().equals(expires)) {
            return this;
        }

        if(stilling.getAnnonseStatus() != AnnonseStatus.AKTIV) {
            return this;
        }

        if(expires.isBefore(now())) {
            annonseStatus = AnnonseStatus.STOPPET;
        }

        return this;

    }

    @Override
    public String toString() {
        return "[UUID: " + uuid + "]" +
                "[Arbeidsgiver: " + employer + "]" +
                "[Annonsetittel: " + title + "]";
    }
}
