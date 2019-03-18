package no.nav.pam.annonsemottak.receivers.finn;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.pam.annonsemottak.receivers.common.PropertyNames;
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
            assertEquals("https://www.finn.no/115526620", stilling.getUrl());
        }
        try (Reader reader = FinnConnectorTest.getReader(AD2)) {
            Stilling stilling = FinnAdMapper.toStilling(new FinnAd(connector.parseReaderToDocument(reader)));
            assertNotNull(stilling);
            assertEquals("https://www.finn.no/142783569", stilling.getUrl());
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
            softAssert.assertThat(stilling.getJobDescription()).startsWith("Avdelingslederen skal ved siden ");
            softAssert.assertThat(stilling.getJobDescription()).contains("Arbeidsoppgaver");
            softAssert.assertThat(stilling.getJobDescription()).contains("---------------");
            softAssert.assertAll();


        }
    }

    @Test
    public void contacts_should_be_mapped_properly() throws Exception {
        try (Reader reader = FinnConnectorTest.getReader(AD1)) {
            Stilling stilling = FinnAdMapper.toStilling(new FinnAd(connector.parseReaderToDocument(reader)));
            String contactInfoJson = stilling.getProperties().get(PropertyNames.KONTAKTINFO);
            JsonNode jsonNode = objectMapper.readTree(contactInfoJson);

            assertEquals("Thomas Tvetbråten", jsonNode.path(0).path("name").asText());
            assertEquals("+47 916 75 001", jsonNode.path(0).path("phone_mobile").asText());
            assertEquals("thomas.tvetbraaten@zeekit.no", jsonNode.path(0).path("email").asText());
            assertEquals("Teknisk Sjef", jsonNode.path(0).path("title").asText());
        }
    }

    @Test
    public void media_should_be_mapped() throws Exception {
        try (Reader reader = FinnConnectorTest.getReader(AD1)) {
            Stilling stilling = FinnAdMapper.toStilling(new FinnAd(connector.parseReaderToDocument(reader)));
            assertNotNull(stilling);
            assertEquals("https://images.finncdn.no/dynamic/default/2018/3/vertical-1/08/0/115/526/_1977044420.png", stilling.getProperties().get("media.logo.url"));
        }
    }

    @Test
    public void externalPublishDateShouldBeMapped() throws Exception {
        try (Reader reader = FinnConnectorTest.getReader(AD1)) {
            Stilling stilling = FinnAdMapper.toStilling(new FinnAd(connector.parseReaderToDocument(reader)));
            assertEquals("2018-03-08T09:42:00.000Z", stilling.getProperties().get(PropertyNames.EXTERNAL_PUBLISH_DATE));
        }
    }

    @Test
    public void geocoordinates_should_be_mapped() throws Exception {
        try (Reader reader = FinnConnectorTest.getReader(AD1)) {
            Stilling stilling = FinnAdMapper.toStilling(new FinnAd(connector.parseReaderToDocument(reader)));
            assertNotNull(stilling);
            assertEquals("10.787223815917969", stilling.getProperties().get(PropertyNames.GEO_LONGITUDE));
            assertEquals("59.821956634521484", stilling.getProperties().get(PropertyNames.GEO_LATITUDE));
        }
    }

    @Test
    public void expired_date_should_be_mapped_correctly() throws Exception {
        try (Reader reader = FinnConnectorTest.getReader(AD1)) {
            Stilling stilling = FinnAdMapper.toStilling(new FinnAd(connector.parseReaderToDocument(reader)));
            assertNotNull(stilling);
            assertEquals("2019-03-22", stilling.getExpires().format(DateTimeFormatter.ISO_DATE));
        }

        // Non parsable text = snarest. Use expired field
        try (Reader reader = FinnConnectorTest.getReader(AD3)) {
            Stilling stilling = FinnAdMapper.toStilling(new FinnAd(connector.parseReaderToDocument(reader)));
            assertNotNull(stilling);
            assertEquals("2017-11-10", stilling.getExpires().format(DateTimeFormatter.ISO_DATE));
        }
    }
}
