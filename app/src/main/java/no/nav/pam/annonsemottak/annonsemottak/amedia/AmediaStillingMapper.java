package no.nav.pam.annonsemottak.annonsemottak.amedia;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import no.nav.pam.annonsemottak.annonsemottak.GenericDateParser;
import no.nav.pam.annonsemottak.annonsemottak.Kilde;
import no.nav.pam.annonsemottak.annonsemottak.Medium;
import no.nav.pam.annonsemottak.annonsemottak.common.PropertyNames;
import no.nav.pam.annonsemottak.markdown.HtmlToMarkdownConverter;
import no.nav.pam.annonsemottak.stilling.Stilling;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static no.nav.pam.annonsemottak.annonsemottak.amedia.AmediaResponseMapper.text;

class AmediaStillingMapper {

    private final AmediaFieldTransformer amediaFieldTransformer;

    private final JsonNode stilling;
    private final JsonNode source;
    private final JsonNode attributes;
    private final JsonNode text;
    private final JsonNode company;
    private final JsonNode address;
    private final JsonNode bookings;
    private final JsonNode primaryLogo;


    public AmediaStillingMapper(JsonNode stilling) {
        amediaFieldTransformer = new AmediaFieldTransformer();
        this.stilling = stilling;
        this.source = stilling.path("_source");
        this.attributes = source.path("attributes");
        this.text = source.path("text");
        this.company = source.path("company");
        this.address = source.path("address");
        this.primaryLogo = source.path("primary_logo");
        this.bookings = source.path("bookings");


    }

    public Stilling getStilling() {
        String stillingstittel = text(source.get("title"));
        String arbeidssted = amediaFieldTransformer.finnSted(
                amediaFieldTransformer.reservefelt(
                        text(attributes.get("worklocation")),
                        text(address.get("geography")),
                        text(address.get("geographyzones"))
                )
        );
        String arbeidsgiver = finnArbeidsgiver();
        String arbeidsgiveromtale = text(attributes.get("employerdescription"));

        String annonsetekst =
                Lists.newArrayList(text.iterator()).stream()
                        .map(t -> text(t.get("value")))
                        .collect(Collectors.joining(" "));

        String soknadsfrist = text(attributes.get("applicationdue"));
        if (soknadsfrist.length() > 20) {
            soknadsfrist = soknadsfrist.substring(0, 20);
        }
        String url = (!StringUtils.isBlank(text(attributes.path("applicationurl")))) ? text(attributes.path("applicationurl")) : null;
        String externalId = text(stilling.get("_id"));
        LocalDateTime expires = AmediaDateConverter
                .convertDate(amediaFieldTransformer.hentListeSomJsonnoder(bookings).stream()
                        .map(b -> text(b.get("date_to")))
                        .max(String::compareTo).orElse(""));

        LocalDateTime systemModifiedTime = AmediaDateConverter
                .convertDate(text(source.get("system_modified_time")));

        Stilling stilling = new Stilling(
                HtmlToMarkdownConverter.parse(stillingstittel).trim(),
                arbeidssted,
                arbeidsgiver,
                arbeidsgiveromtale,
                annonsetekst,
                soknadsfrist,
                Kilde.AMEDIA.toString(),
                Medium.AMEDIA.toString(),
                url,
                externalId,
                getKeyValueMap()
        );

        stilling.setExpires(GenericDateParser.parse(soknadsfrist).orElse(expires));
        stilling.setSystemModifiedDate(systemModifiedTime);

        return stilling;
    }

    private String getStedMedSoner() {
        return amediaFieldTransformer.reservefelt(
                text(address.get("geography")),
                text(address.get("geographyzones"))
        );
    }

    private String finnArbeidsgiver() {
        return amediaFieldTransformer.reservefelt(
                text(attributes.get("employer")),
                text(company.get("title"))
        );

    }

    private Map<String, String> getKeyValueMap() {

        String adresse = Stream.of(
                text(address.get("primary_address")),
                text(address.get("secondary_address")),
                text(address.get("post_code")),
                text(address.get("post_location")))
                .filter(t -> !"null".equals(t) && !"".equals(t))
                .collect(Collectors.joining(" "));

        String publications = amediaFieldTransformer.hentListeSomJsonnoder(bookings).stream()
                .flatMap(b -> amediaFieldTransformer.hentListeSomStrenger(b.get("publications")).stream())
                .distinct()
                .collect(Collectors.joining(","));

        LocalDateTime published = AmediaDateConverter
                .convertDate(amediaFieldTransformer.hentListeSomJsonnoder(bookings).stream()
                        .map(b -> text(b.get("date_from")))
                        .max(String::compareTo).orElse(""));

        return
                addifValue(ImmutableMap.builder(),
                        PropertyNames.ANTALL_STILLINGER, text(attributes.get("positioncount")),
                        PropertyNames.HELTIDDELTID, text(attributes.get("workhours")),
                        PropertyNames.ANNONSOR, text(company.get("title")),
                        PropertyNames.ADRESSE, adresse,
                        PropertyNames.SEKTOR, text(attributes.get("worksector")),
                        PropertyNames.STILLINGSTITTEL, text(attributes.get("positiontitle")),

                        "system_created", text(source.get("system_created_time")),
                        PropertyNames.CREATED_DATE, text(source.get("created_time")),
                        "system_modified", text(source.get("system_modified_time")),
                        PropertyNames.UPDATED_DATE, text(source.get("modified_time")),
                        PropertyNames.EXTERNAL_PUBLISH_DATE, published.atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT),
                        PropertyNames.LOCATION_ADDRESS, text(address.get("primary_address")),
                        "secondary_address", text(address.get("secondary_address")),
                        PropertyNames.LOCATION_POSTCODE, text(address.get("post_code")),
                        PropertyNames.LOCATION_CITY, text(address.get("post_location")),
                        "geography", getStedMedSoner(),
                        PropertyNames.LOGO_URL_MAIN, primaryLogo != null
                                ? "https://g.api.no/obscura/API/image/r1/zett/355x223rp-hi/1508983200000"
                                : null,
                        PropertyNames.LOGO_URL_LISTING, primaryLogo != null ? text(primaryLogo.get("reference")) : null,
                        PropertyNames.GEO_LATITUDE, text(attributes.get("mapcoordinatelat")),
                        PropertyNames.GEO_LONGITUDE, text(attributes.get("mapcoordinatelon")),
                        PropertyNames.KONTAKTINFO, amediaFieldTransformer.hentListeSomStreng(source.get("contacts")),
                        PropertyNames.OCCUPATIONS, Optional.of(
                                text(source.path("category")))
                                .filter(cs -> StringUtils.isNotBlank(cs) && !StringUtils.equals("Stilling/Annet", cs))
                                .map(s -> StringUtils.removeStart(s, "Stilling/"))
                                .orElse(null),
                        "publications", publications
                ).build();
    }


    /**
     * Hjelpemetode for guava ImmutableMap. Conditonal if, siden guava ikke støtter det, og
     * alternativet er å sjekke for hver verdi, eller bruke en modifiable map og fjerne uønskede
     * entries etterpå.
     *
     * @param builder   ImmutableMap builder
     * @param keyvalues key or value
     * @return the builder with values for non null values
     */
    private ImmutableMap.Builder<String, String> addifValue(
            ImmutableMap.Builder<String, String> builder,
            String... keyvalues) {
        for (int i = 0; i < keyvalues.length; i += 2) {
            String key = keyvalues[i];
            String value = keyvalues[i + 1];
            if (!StringUtils.isEmpty(value)) {
                builder.put(key, value);
            }
        }
        return builder;
    }

}
