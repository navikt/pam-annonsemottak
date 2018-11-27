package no.nav.pam.annonsemottak.annonsemottak.amedia;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.pam.annonsemottak.annonsemottak.Kilde;
import no.nav.pam.annonsemottak.annonsemottak.Medium;
import no.nav.pam.annonsemottak.annonsemottak.common.PropertyNames;
import no.nav.pam.annonsemottak.stilling.Arbeidsgiver;
import no.nav.pam.annonsemottak.stilling.Stilling;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Condition;
import org.assertj.core.api.SoftAssertions;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AmediaResponseMapperTest {

    private final static InputStream enkeltResultat;
    private final static InputStream mangeResultat;
    private final static InputStream idResultat;
    private final static InputStream enkeltResultatLiteData;
    private final static InputStream bareWorkLocation;
    private final static InputStream enkeltResultatUtenUrl;
    private final static InputStream enkeltResultatButikkategori;
    private final static InputStream enkeltResultatGenerellKategori;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        enkeltResultat = AmediaResponseMapperTest.class.getClassLoader()
            .getResourceAsStream("amedia.io.samples/enkeltResultat.json");

        mangeResultat = AmediaResponseMapperTest.class.getClassLoader()
            .getResourceAsStream("amedia.io.samples/mangeResultat.json");

        idResultat = AmediaResponseMapperTest.class.getClassLoader()
            .getResourceAsStream("amedia.io.samples/idResultat.json");

        enkeltResultatLiteData = AmediaResponseMapperTest.class.getClassLoader()
            .getResourceAsStream("amedia.io.samples/enkeltResultatLiteData.json");

        bareWorkLocation = AmediaResponseMapperTest.class.getClassLoader()
            .getResourceAsStream("amedia.io.samples/bareWorkLocation.json");

        enkeltResultatUtenUrl = AmediaResponseMapperTest.class.getClassLoader()
            .getResourceAsStream("amedia.io.samples/enkeltResultatUtenUrl.json");

        enkeltResultatButikkategori = AmediaResponseMapperTest.class.getClassLoader()
            .getResourceAsStream("amedia.io.samples/enkeltResultatButikkategori.json");

        enkeltResultatGenerellKategori = AmediaResponseMapperTest.class.getClassLoader()
            .getResourceAsStream("amedia.io.samples/enkeltResultatGenerellKategori.json");
    }

    @Test
    public void kanMappeAmediaResponseTilStilling() throws Exception {
        List<Stilling> stillinger = mapStilling(enkeltResultat);

        SoftAssertions s = new SoftAssertions();
        s.assertThat(stillinger).hasSize(1);
        Stilling stilling = stillinger.get(0);

        s.assertThat(stilling.getTitle()).isEqualTo("Tankbilsjåfør");
        s.assertThat(stilling.getPlace()).isEqualTo("Alstahaug");
        s.assertThat(stilling.getArbeidsgiver()).containsInstanceOf(Arbeidsgiver.class);

        s.assertThat(stilling.getArbeidsgiver()).hasValueSatisfying(
            new Condition<>(a -> "Boreal Sjø AS".equals(a.asString()), "Boreal Sjø AS"));
        s.assertThat(stilling.getEmployerDescription()).startsWith("<p>Boreal  er et");
        s.assertThat(stilling.getJobDescription()).startsWith("<p>Minol består av 42");
        s.assertThat(stilling.getDueDate()).isEqualTo("30.10.2017");
        s.assertThat(stilling.getKilde()).isEqualTo(Kilde.AMEDIA.toString());
        s.assertThat(stilling.getMedium()).isEqualTo(Medium.AMEDIA.toString());
        s.assertThat(stilling.getUrl())
            .isEqualTo("https://karriere.no/jobb/tankbilsjafor-572920.html");
        s.assertThat(stilling.getExternalId()).isEqualTo("3410158");
        s.assertThat(stilling.getExpires()).isEqualTo(AmediaDateConverter.convertDate("2017-10-30T00:00:00Z"));  //soknadsfrist
        s.assertThat(stilling.getPublished()).isNull();

        Map<String, String> properties = stilling.getProperties();
        s.assertThat(properties.get(PropertyNames.EXTERNAL_PUBLISH_DATE)).isEqualTo("2017-10-17T00:00:00Z");
        s.assertThat(properties.get(PropertyNames.ANTALL_STILLINGER)).isEqualTo("1");
        s.assertThat(properties.get(PropertyNames.HELTIDDELTID)).isEqualTo("Heltid");
        s.assertThat(properties.get(PropertyNames.ANNONSOR)).isEqualTo("Karriere.no AS");
        s.assertThat(properties.get(PropertyNames.ADRESSE))
            .isEqualTo("test_primary test_secondary 8800 Sandnessjøen");
        s.assertThat(properties.get(PropertyNames.SEKTOR)).isEqualTo("Privat");
        s.assertThat(properties.get(PropertyNames.STILLINGSTITTEL)).isEqualTo("Tankbilsjåfør");
        s.assertThat(properties.get("system_created")).isEqualTo("2017-10-17T08:35:43Z");
        s.assertThat(properties.get(PropertyNames.CREATED_DATE)).isEqualTo("2017-10-17T08:35:43Z");
        s.assertThat(properties.get("system_modified")).isEqualTo("2017-10-17T08:38:07Z");
        s.assertThat(properties.get(PropertyNames.UPDATED_DATE)).isEqualTo("2017-10-17T08:35:43Z");
        s.assertThat(properties.get(PropertyNames.LOCATION_ADDRESS)).isEqualTo("test_primary");
        s.assertThat(properties.get("secondary_address")).isEqualTo("test_secondary");
        s.assertThat(properties.get(PropertyNames.LOCATION_POSTCODE)).isEqualTo("8800");
        s.assertThat(properties.get(PropertyNames.LOCATION_CITY)).isEqualTo("Sandnessjøen");
        s.assertThat(properties.get("geography")).isEqualTo("Norge/Nordland/Alstahaug");
        s.assertThat(properties.get(PropertyNames.LOGO_URL_MAIN))
            .isEqualTo("https://g.api.no/obscura/API/image/r1/zett/355x223rp-hi/1508983200000");
        s.assertThat(properties.get(PropertyNames.LOGO_URL_LISTING))
            .isEqualTo("5d/b2/5db27b154fa7b0d5fbe567eb1fa01d0c");
        s.assertThat(properties.get(PropertyNames.GEO_LATITUDE)).isEqualTo("60.3700001951161");
        s.assertThat(properties.get(PropertyNames.GEO_LONGITUDE)).isEqualTo("5.34445174400657");
        s.assertThat(properties.get(PropertyNames.KONTAKTINFO)).isEqualTo("[]");
        s.assertThat(properties.get("publications")).isEqualTo("\"www.fremover.no\",\"www.an.no\",\"www.ta.no\",\"www.tb.no\",\"www.ba.no\",\"www.ranablad.no\",\"www.hardanger-folkeblad.no\",\"www.lofot-tidende.no\",\"www.oa.no\",\"www.tk.no\",\"www.nordhordland.no\",\"www.ifinnmark.no\",\"www.nordlys.no\",\"www.lofotposten.no\",\"www.rb.no\",\"www.helg.no\",\"www.dt.no\"");

        s.assertAll();
    }

    @Test
    public void kanMappeMangeAmediaResponseTilStilling() throws Exception {
        List<Stilling> stillinger = mapStilling(mangeResultat);

        SoftAssertions s = new SoftAssertions();
        s.assertThat(stillinger).hasSize(27);
        Stilling stilling = stillinger.get(0);

        s.assertThat(stilling.getTitle()).isEqualTo("Pedagogisk rådgiver");
        s.assertThat(stilling.getPlace()).isEqualTo("Kongsvinger");
        s.assertThat(stilling.getArbeidsgiver()).containsInstanceOf(Arbeidsgiver.class);

        s.assertThat(stilling.getArbeidsgiver()).hasValueSatisfying(
            new Condition<>(
                a -> "Service- og forvaltningsenheten".equals(a.asString()),
                "Service- og forvaltningsenheten"));
        s.assertThat(stilling.getEmployerDescription()).startsWith("");
        s.assertThat(stilling.getJobDescription()).startsWith("<p>Kongsvinger kommune har");
        s.assertThat(stilling.getDueDate()).isEqualTo("27.11.2017");
        s.assertThat(stilling.getKilde()).isEqualTo(Kilde.AMEDIA.toString());
        s.assertThat(stilling.getMedium()).isEqualTo(Medium.AMEDIA.toString());
        s.assertThat(stilling.getUrl())
            .isEqualTo("https://e-skjema.no/kongsvinger/");
        s.assertThat(stilling.getExternalId()).isEqualTo("3411737");

        Map<String, String> properties = stilling.getProperties();
        s.assertThat(properties.get(PropertyNames.ANTALL_STILLINGER)).isEqualTo("1");
        s.assertThat(properties.get(PropertyNames.HELTIDDELTID)).isEqualTo(null);
        s.assertThat(properties.get(PropertyNames.ANNONSOR)).isEqualTo("Kongsvinger kommune");
        s.assertThat(properties.get(PropertyNames.ADRESSE)).isEqualTo("2226 Kongsvinger");
        s.assertThat(properties.get(PropertyNames.SEKTOR)).isEqualTo(null);
        s.assertThat(properties.get(PropertyNames.STILLINGSTITTEL))
            .isEqualTo("Pedagogisk rådgiver");
        s.assertThat(properties.get(PropertyNames.KONTAKTINFO)).isEqualTo(
            "[{\"mobile\":\"908 86 423\",\"name\":\"Siri Elisabeth Nygaard Hansen\",\"description\":null,\"title\":\"Enhetsleder\",\"email\":\"\"}]");
        s.assertAll();
    }

    @Test
    public void kanHenteStillingsIder() throws IOException {
        List<String> ider = mapIDer(idResultat);
        SoftAssertions s = new SoftAssertions();
        s.assertThat(ider).hasSize(11);
        s.assertThat(ider).allMatch(id -> !StringUtils.isBlank(id));
        s.assertThat(ider).startsWith("3357133", "3360213", "3361940", "3361941", "3361942");
        s.assertAll();
    }

    @Test
    public void defaultVerdiOmIkkeArbeidsgiverFinnes() throws IOException {
        List<Stilling> stillinger = mapStilling(enkeltResultatLiteData);

        SoftAssertions s = new SoftAssertions();
        s.assertThat(stillinger).hasSize(1);
        Stilling stilling = stillinger.get(0);

        s.assertThat(stilling.getArbeidsgiver()).hasValueSatisfying(
            new Condition<>(
                a -> "Ikke oppgitt".equals(a.asString()),
                "Ikke oppgitt"));

        s.assertAll();
    }

    @Test
    public void nullOmUrlIkkeFinnes() throws IOException {
        List<Stilling> stillinger = mapStilling(enkeltResultatUtenUrl);

        SoftAssertions s = new SoftAssertions();
        s.assertThat(stillinger).hasSize(1);
        Stilling stilling = stillinger.get(0);

        s.assertThat(stilling.getUrl()).isNull();

        s.assertAll();
    }

    @Test
    public void kategoriBrukes() throws IOException {
        List<Stilling> stillinger = mapStilling(enkeltResultatButikkategori);

        SoftAssertions s = new SoftAssertions();
        s.assertThat(stillinger).hasSize(1);
        Stilling stilling = stillinger.get(0);

        s.assertThat(stilling.getProperties().get(PropertyNames.OCCUPATIONS)).isEqualTo("Butikk");

        s.assertAll();
    }

    @Test
    public void tomOmViHarForGenerellKategori() throws IOException {
        List<Stilling> stillinger = mapStilling(enkeltResultatGenerellKategori);

        SoftAssertions s = new SoftAssertions();
        s.assertThat(stillinger).hasSize(1);
        Stilling stilling = stillinger.get(0);

        s.assertThat(stilling.getProperties().get(PropertyNames.OCCUPATIONS)).isNull();

        s.assertAll();
    }


    @Test
    public void ingenSted() throws IOException {
        List<Stilling> stillinger = mapStilling(bareWorkLocation);

        SoftAssertions s = new SoftAssertions();
        s.assertThat(stillinger).hasSize(1);
        Stilling stilling = stillinger.get(0);

        s.assertThat(stilling.getPlace()).isEqualTo("Tysvær kommune");

        s.assertAll();
    }

    private List<Stilling> mapStilling(InputStream resultat) throws java.io.IOException {
        JsonNode amediaResponse = OBJECT_MAPPER.readValue(resultat, JsonNode.class);
        return AmediaResponseMapper.mapResponse(amediaResponse);
    }

    private List<String> mapIDer(InputStream resultat) throws java.io.IOException {
        JsonNode amediaResponse = OBJECT_MAPPER.readValue(resultat, JsonNode.class);
        return AmediaResponseMapper.mapEksternIder(amediaResponse);
    }

}