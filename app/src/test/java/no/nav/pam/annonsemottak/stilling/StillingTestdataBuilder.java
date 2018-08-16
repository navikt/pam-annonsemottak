package no.nav.pam.annonsemottak.stilling;

import no.nav.pam.annonsemottak.annonsemottak.GenericDateParser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static no.nav.pam.annonsemottak.stilling.OppdaterSaksbehandlingCommand.*;

public class StillingTestdataBuilder implements TestdataBuilder<Stilling> {

    private String arbeidsgiver;
    private String arbeidssted;
    private String tittel;
    private String stillingstekst;
    private String arbeidsgiverbeskrivelse;
    private String utløpsdato;
    private String saksbehandler;
    private Status status;
    private Merknader merknader;
    private Map<String, String> props;
    private String kilde = "KILDE";
    private String medium = StillingTestdataBuilder.class.getSimpleName();
    private String url;
    private String externalId;
    private LocalDateTime systemModifiedDate = null;

    public static StillingTestdataBuilder stilling() {
        return new StillingTestdataBuilder();
    }

    /**
     * Inspirert av Object Mother-patternet, og gir deg en ferdigutfylt StillingTestdataBuilder.<br>
     * Det gir deg muligheter for å lage en Stilling, og samtidig kunne overstyre de feltene som er relevante for testen.
     */
    public static StillingTestdataBuilder enkelStilling() {
        StillingTestdataBuilder stilling = new StillingTestdataBuilder();
        stilling.tittel = "Stillingstittel";
        stilling.arbeidssted = "Sted";
        stilling.arbeidsgiver = "Navn på arbeidsgiver";
        stilling.utløpsdato = LocalDateTime.now().plusMonths(1).format(DateTimeFormatter.ofPattern("dd/M/yyyy"));
        stilling.arbeidsgiverbeskrivelse = "Beskrivelse av arbeidsgiver";
        stilling.stillingstekst = "Arbeidsbeskrivelse";
        stilling.url = "https://jobb.tu.no/jobs/teknisk-sektor/51342-gode-kunnskaper-om-vei-og-jernbaneplanlegging-i-by";
        stilling.externalId = "unik-externt-id";
        return stilling;
    }

    @Override
    public Stilling build() {
        Stilling stilling = new Stilling(tittel, arbeidssted, arbeidsgiver, arbeidsgiverbeskrivelse,
                stillingstekst, utløpsdato, kilde, medium, url, externalId, GenericDateParser.parse(utløpsdato).orElse(null), props != null ? props : new HashMap(), systemModifiedDate);
        Map map = new HashMap();
        if (status != null) {
            map.put(STATUS, status.getKodeAsString());
        }
        if (saksbehandler != null) {
            map.put(SAKSBEHANDLER, saksbehandler);
        }
        if (merknader != null) {
            map.put(MERKNADER, merknader.asString());
        }

        if (!map.isEmpty()) {
            try {
                stilling.oppdaterMed(new OppdaterSaksbehandlingCommand(map));
            } catch (IllegalSaksbehandlingCommandException e) {
                throw new RuntimeException(e); // Parent doesn't declare exception.
            }
        }

        return stilling;
    }

    public StillingTestdataBuilder arbeidsgiver(String arbeidsgiver) {
        this.arbeidsgiver = arbeidsgiver;
        return this;
    }

    public StillingTestdataBuilder arbeidssted(String arbeidssted) {
        this.arbeidssted = arbeidssted;
        return this;
    }

    public StillingTestdataBuilder tittel(String tittel) {
        this.tittel = tittel;
        return this;
    }

    public StillingTestdataBuilder saksbehandler(String saksbehandler) {
        this.saksbehandler = saksbehandler;
        return this;
    }

    public StillingTestdataBuilder status(Status status) {
        this.status = status;
        return this;
    }

    public StillingTestdataBuilder merknader(Merknader merknader) {
        this.merknader = merknader;
        return this;
    }

    public StillingTestdataBuilder utløpsdato(String utløpsdato) {
        this.utløpsdato = utløpsdato;
        return this;
    }

    public StillingTestdataBuilder properties(Map<String, String> props) {
        this.props = props;
        return this;
    }

    public StillingTestdataBuilder arbeidsgiverbeskrivelse(String arbeidsgiverbeskrivelse) {
        this.arbeidsgiverbeskrivelse = arbeidsgiverbeskrivelse;
        return this;
    }

    public StillingTestdataBuilder stillingstekst(String stillingstekst) {
        this.stillingstekst = stillingstekst;
        return this;
    }

    public StillingTestdataBuilder kilde(String kilde) {
        this.kilde = kilde;
        return this;
    }

    public StillingTestdataBuilder medium(String medium) {
        this.medium = medium;
        return this;
    }

    public StillingTestdataBuilder url(String url) {
        this.url = url;
        return this;
    }

    public StillingTestdataBuilder externalId(String externalId) {
        this.externalId = externalId;
        return this;
    }

    public StillingTestdataBuilder utlopsdato(String dato) {
        this.utløpsdato = dato;
        return this;
    }

    public StillingTestdataBuilder systemModifiedDate(LocalDateTime date) {
        this.systemModifiedDate = date;
        return this;
    }
}
