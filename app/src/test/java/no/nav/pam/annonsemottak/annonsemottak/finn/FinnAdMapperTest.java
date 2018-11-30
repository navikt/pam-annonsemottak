package no.nav.pam.annonsemottak.annonsemottak.finn;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.pam.annonsemottak.annonsemottak.common.PropertyNames;
import no.nav.pam.annonsemottak.stilling.Stilling;
import org.assertj.core.api.SoftAssertions;
import org.junit.Test;
import org.mockito.Mock;

import java.io.Reader;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FinnAdMapperTest {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String AD1 = "src/test/resources/finn/samples/ad1.xml";
    private static final String AD2 = "src/test/resources/finn/samples/ad2.xml";
    private static final String AD3 = "src/test/resources/finn/samples/ad3.xml";

    private final FinnConnector connector = new FinnConnector(null, null, null, 0);

    @Mock
    private FinnAd ad = mock(FinnAd.class);

    @Mock
    private FinnAd.GeneralText text = mock(FinnAd.GeneralText.class);

    @Test
    public void mapToNullIfEmptyAnnonsetittel() {

        when(ad.getGeneralText()).thenReturn(Collections.singletonList(text));
        when(text.getTitle()).thenReturn("A non-empty text");
        when(text.getValue()).thenReturn("A non-empty text");

        when(ad.getTitle()).thenReturn(null);
        assertNull(FinnAdMapper.toStilling(ad));

        when(ad.getTitle()).thenReturn("");
        assertNull(FinnAdMapper.toStilling(ad));

    }

    @Test
    public void mapToNullIfEmptyAnnonsetekst() {

        when(ad.getTitle()).thenReturn("A non-empty text");

        when(ad.getGeneralText()).thenReturn(null);
        assertNull(FinnAdMapper.toStilling(ad));

        when(ad.getGeneralText()).thenReturn(Collections.singletonList(text));
        when(text.getTitle()).thenReturn(null);
        when(text.getValue()).thenReturn(null);
        assertNull(FinnAdMapper.toStilling(ad));

        when(text.getTitle()).thenReturn("");
        when(text.getValue()).thenReturn("");
        assertNull(FinnAdMapper.toStilling(ad));

    }

    @Test
    public void adUrlShouldBeMappedProperly()
            throws Exception {

        try (Reader reader = FinnConnectorTest.getReader(AD1)) {
            Stilling stilling = FinnAdMapper.toStilling(new FinnAd(connector.parseReaderToDocument(reader)));
            assertNotNull(stilling);
            assertEquals("https://www.finn.no/91758798", stilling.getUrl());
        }
        try (Reader reader = FinnConnectorTest.getReader(AD2)) {
            Stilling stilling = FinnAdMapper.toStilling(new FinnAd(connector.parseReaderToDocument(reader)));
            assertNotNull(stilling);
            assertEquals("https://www.finn.no/92031856", stilling.getUrl());
        }

    }

    @Test
    public void noPropertiesShouldHaveNullValues()
            throws Exception {

        try (Reader reader = FinnConnectorTest.getReader(AD1)) {
            Stilling stilling = FinnAdMapper.toStilling(new FinnAd(connector.parseReaderToDocument(reader)));
            assertNotNull(stilling);
            stilling.getProperties().forEach((key, value) -> assertNotNull(value));
        }

    }

    @Test
    public void annonsetekstBasedOnMultipleGeneralTextEntriesShouldBeConcatenatedWithTitles()
            throws Exception {
        try (Reader reader = FinnConnectorTest.getReader(AD2)) {
            Stilling stilling = FinnAdMapper.toStilling(new FinnAd(connector.parseReaderToDocument(reader)));

            SoftAssertions softAssert = new SoftAssertions();
            softAssert.assertThat(stilling).isNotNull();
            softAssert.assertThat(stilling.getJobDescription()).startsWith("Vi søker dyktig prosjektleder");
            softAssert.assertThat(stilling.getJobDescription()).contains("Arbeidsoppgaver");
            softAssert.assertThat(stilling.getJobDescription()).contains("---------------");
            softAssert.assertThat(stilling.getJobDescription()).contains("Kvalifikasjoner");
            softAssert.assertThat(stilling.getJobDescription()).contains("---------------");
            softAssert.assertThat(stilling.getJobDescription()).contains("Utdanning");
            softAssert.assertThat(stilling.getJobDescription()).contains("---------");
            softAssert.assertThat(stilling.getJobDescription()).contains("Språk");
            softAssert.assertThat(stilling.getJobDescription()).contains("-----");
            softAssert.assertThat(stilling.getJobDescription()).contains("Egenskaper");
            softAssert.assertThat(stilling.getJobDescription()).contains("----------");
            softAssert.assertThat(stilling.getJobDescription()).contains("Vi tilbyr");
            softAssert.assertThat(stilling.getJobDescription()).contains("---------");
            softAssert.assertAll();


        }
    }

    @Test
    public void contacts_should_be_mapped_properly() throws Exception {
        try (Reader reader = FinnConnectorTest.getReader(AD1)) {
            Stilling stilling = FinnAdMapper.toStilling(new FinnAd(connector.parseReaderToDocument(reader)));
            String contactInfoJson = stilling.getProperties().get(PropertyNames.KONTAKTINFO);
            JsonNode jsonNode = objectMapper.readTree(contactInfoJson);

            assertEquals("Charlotte Silkebekken Bergerud", jsonNode.path(0).path("name").asText());
            assertEquals("+47 958 32 353", jsonNode.path(0).path("phone_work").asText());
            assertEquals("csb@toptemp.no", jsonNode.path(0).path("email").asText());
            assertEquals("Senior rekrutteringsrådgiver", jsonNode.path(0).path("title").asText());
        }
    }

    @Test
    public void media_should_be_mapped() throws Exception {
        try (Reader reader = FinnConnectorTest.getReader(AD1)) {
            Stilling stilling = FinnAdMapper.toStilling(new FinnAd(connector.parseReaderToDocument(reader)));
            assertNotNull(stilling);
            assertEquals("https://images.finncdn.no/mmo/2017/3/2/8/917/587/98_2118645667.png", stilling.getProperties().get("media.logo.url"));
            assertEquals("https://images.finncdn.no/mmo/logo/result/2083520418/iad_5153168901682665885ikon-finn-amesto-top-temp.png", stilling.getProperties().get("media.logo.url.list"));
            assertEquals("https://images.finncdn.no/mmo/logo/object/2083520418/iad_9204971676583467114amestotoptemp_logo_fra_januar_2015.png", stilling.getProperties().get("media.logo.url.main"));
        }
    }

    @Test
    public void externalPublishDateShouldBeMapped() throws Exception {
        try (Reader reader = FinnConnectorTest.getReader(AD1)) {
            Stilling stilling = FinnAdMapper.toStilling(new FinnAd(connector.parseReaderToDocument(reader)));
            assertEquals("2017-03-02T15:01:00.000Z", stilling.getProperties().get(PropertyNames.EXTERNAL_PUBLISH_DATE));
        }
    }

    @Test
    public void geocoordinates_should_be_mapped() throws Exception {
        try (Reader reader = FinnConnectorTest.getReader(AD1)) {
            Stilling stilling = FinnAdMapper.toStilling(new FinnAd(connector.parseReaderToDocument(reader)));
            assertNotNull(stilling);
            assertEquals("11.03524", stilling.getProperties().get(PropertyNames.GEO_LONGITUDE));
            assertEquals("59.95626", stilling.getProperties().get(PropertyNames.GEO_LATITUDE));
        }
    }

    @Test
    public void expired_date_should_be_mapped_correctly() throws Exception {
        try (Reader reader = FinnConnectorTest.getReader(AD1)) {
            Stilling stilling = FinnAdMapper.toStilling(new FinnAd(connector.parseReaderToDocument(reader)));
            assertNotNull(stilling);
            assertEquals("2017-03-19", stilling.getExpires().format(DateTimeFormatter.ISO_DATE));
        }

        // Non parsable text = snarest. Use expired field
        try (Reader reader = FinnConnectorTest.getReader(AD3)) {
            Stilling stilling = FinnAdMapper.toStilling(new FinnAd(connector.parseReaderToDocument(reader)));
            assertNotNull(stilling);
            assertEquals("2017-11-10", stilling.getExpires().format(DateTimeFormatter.ISO_DATE));
        }
    }
}
