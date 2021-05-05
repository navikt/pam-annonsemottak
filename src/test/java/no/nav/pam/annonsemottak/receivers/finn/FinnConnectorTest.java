package no.nav.pam.annonsemottak.receivers.finn;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FinnConnectorTest {

    private FinnConnector connector = new FinnConnector(null, null, null, 0);

    static Reader getReader(String filename)
            throws UnsupportedEncodingException, FileNotFoundException {
        return new InputStreamReader(new FileInputStream(filename), "UTF-8");
    }

    @Test
    public void handleServiceDocument()
            throws Exception {
        try (Reader reader = getReader("src/test/resources/finn/samples/service_document.xml")) {
            FinnServiceDocument document = new FinnServiceDocument(connector.parseReaderToDocument(reader));

            SoftAssertions softAssert = new SoftAssertions();
            softAssert.assertThat(document.getHrefFromCollectionInWorkspace("does-not-exist", "neither-do-i")).isNull();
            softAssert.assertThat(document.getHrefFromCollectionInWorkspace("models", "does-not-exist")).isNull();
            softAssert.assertThat(document.getHrefFromCollectionInWorkspace("searches", "job-full-time").toString()).isEqualTo("https://cache.api.finn.no/iad/search/job-full-time");
            softAssert.assertThat(document.getHrefFromCollectionInWorkspace("searches", "job-part-time").toString()).isEqualTo("https://cache.api.finn.no/iad/search/job-part-time");
            softAssert.assertThat(document.getHrefFromCollectionInWorkspace("searches", "job-management").toString()).isEqualTo("https://cache.api.finn.no/iad/search/job-management");
            softAssert.assertAll();
        }
    }

    @Test
    public void handleSearchResults()
            throws Exception {
        try (Reader reader = getReader("src/test/resources/finn/samples/job-full-time.xml")) {
            FinnSearchResultsHandler handler = new FinnSearchResultsHandler();
            connector.parseReaderWithHandler(reader, handler);
            assertEquals("https://cache.api.finn.no/iad/search/job-full-time/?age=1&sort=0&page=2", handler.getNextPageUrl().orElse(null).toString());
            assertEquals(30, handler.getFinnAdHeads().size());

            Optional<FinnAdHead> adHead1 = handler.getFinnAdHeads().stream().filter(x -> x.getId().equals("91696757")).findAny();
            assertTrue(adHead1.isPresent());
            assertEquals("Logistikkoperatør", adHead1.get().getTitle());

            assertEquals("2017-03-01T13:13:00.000Z", FinnDateConverter.toString(adHead1.get().getUpdated()));
            assertEquals("2017-03-01T13:14:00.000Z", FinnDateConverter.toString(adHead1.get().getPublished()));
            assertEquals("2017-03-15T22:59:00.000Z", FinnDateConverter.toString(adHead1.get().getExpires()));
        }
    }

    @Test
    public void handleAd1()
            throws Exception {
        try (Reader reader = getReader("src/test/resources/finn/samples/ad1.xml")) {
            FinnAd ad = new FinnAd(connector.parseReaderToDocument(reader));

            SoftAssertions softAssert = new SoftAssertions();
            softAssert.assertThat(ad.getId()).isEqualTo("urn:id:115526620");
            softAssert.assertThat(ad.getTitle()).isEqualTo("GPS montør / Servicetekniker søkes");
            softAssert.assertThat(ad.getUpdated()).isEqualTo("2019-03-07T11:17:00.000Z");
            softAssert.assertThat(ad.getPublished()).isEqualTo("2018-03-08T09:42:00.000Z");
            softAssert.assertThat(ad.getDateSubmitted()).isEqualTo("2018-03-08T09:42:00.000Z");
            softAssert.assertThat(ad.getExpires()).isEqualTo("2019-04-18T10:17:00.000Z");
            softAssert.assertThat(ad.getEdited()).isEqualTo("2019-03-07T11:17:00.000Z");
            softAssert.assertThat(ad.getIdentifier()).isEqualTo("115526620");
            softAssert.assertThat(ad.isPrivate()).isTrue();
            softAssert.assertThat(ad.getType()).isEqualTo("job-full-time");
            softAssert.assertThat(ad.isActive()).isTrue();
            softAssert.assertThat(ad.isDisposed()).isFalse();
            softAssert.assertThat(ad.getLinkToApply().get(0)).isEqualTo("https://www.finn.no/recruitment/hired/frontend/applynow/input.action?adId=115526620");
            softAssert.assertThat(ad.getLocation().getAddress()).isEqualTo("Rosenholmveien 25");
            softAssert.assertThat(ad.getLocation().getPostalCode()).isEqualTo("1414");
            softAssert.assertThat(ad.getLocation().getCity()).isEqualTo("Trollåsen");
            softAssert.assertThat(ad.getLocation().getCountry()).isEqualTo("Norge");
            softAssert.assertThat(ad.getApplicationDeadline()).isEqualTo("22.03.2019");
            softAssert.assertThat(ad.getCompany().getName()).isEqualTo("Zeekit AS");
            softAssert.assertThat(ad.getCompany().getIngress().startsWith("Zeekit AS hatt en solid vekst og teller i dag totalt")).isTrue();
            softAssert.assertThat(ad.getCompany().getUrl()).isEqualTo("http://www.zeekit.no");
            softAssert.assertThat(ad.getDuration()).isEqualTo("Fast");
            softAssert.assertThat(ad.getIndustry().size()).isEqualTo(3);
            softAssert.assertThat(ad.getIndustry().contains("Bil, kjøretøy og verksted")).isTrue();
            softAssert.assertThat(ad.getIndustry().contains("IT")).isTrue();
            softAssert.assertThat(ad.getJobTitle()).isEqualTo("GPS montør, Servicetekniker, bilelektriker, lastebilmekaniker");
            softAssert.assertThat(ad.getKeywords().size()).isEqualTo(5);
            softAssert.assertThat(ad.getKeywords().contains("Bilelektro")).isTrue();
            softAssert.assertThat(ad.getKeywords().contains("GPS")).isTrue();
            softAssert.assertThat(ad.getKeywords().contains("Datafangst")).isTrue();
            softAssert.assertThat(ad.getKeywords().contains("Montering")).isTrue();
            softAssert.assertThat(ad.getKeywords().contains("Feilsøking")).isTrue();
            softAssert.assertThat(ad.getSector()).isEqualTo("Privat");
            softAssert.assertThat(ad.getStartDate()).isEqualTo("01.06.2019");

            //Workplaces
            softAssert.assertThat(ad.getWorkplaces().size()).isEqualTo(2);
            softAssert.assertThat(ad.getWorkplaces().contains("Rosenholmveien 25")).isTrue();
            softAssert.assertThat(ad.getWorkplaces().contains("1414 Trollåsen")).isTrue();

            //Contacts
            softAssert.assertThat(ad.getContacts().get(0).getName()).isEqualTo("Thomas Tvetbråten");
            softAssert.assertThat(ad.getContacts().get(0).getEmail()).isEqualTo("thomas.tvetbraaten@zeekit.no");
            softAssert.assertThat(ad.getContacts().get(0).getTitle()).isEqualTo("Teknisk Sjef");
            softAssert.assertThat(ad.getContacts().get(0).getPhone_mobile()).isEqualTo("+47 916 75 001");

            //Geo locations
            softAssert.assertThat(ad.getGeoLocation().getAccuracy()).isEqualTo("9");
            softAssert.assertThat(ad.getGeoLocation().getLatitude()).isEqualTo("59.821956634521484");
            softAssert.assertThat(ad.getGeoLocation().getLongitude()).isEqualTo("10.787223815917969");

            //Occupations
            softAssert.assertThat(ad.getOccupations().size()).isEqualTo(1);
            softAssert.assertThat(ad.getOccupations().contains("Teknisk personell")).isTrue();

            // Test Logo urls are correct
            softAssert.assertThat(ad.getLogoUrlList().size()).isEqualTo(1);
            softAssert.assertThat(ad.getLogoUrlList().get(0)).isEqualTo("https://images.finncdn.no/dynamic/default/2018/3/vertical-1/08/0/115/526/_1977044420.png");
            softAssert.assertAll();
        }
    }

    @Test
    public void handleAd2WithMultipleGeneralTextEntries()
            throws Exception {
        try (Reader reader = getReader("src/test/resources/finn/samples/ad2.xml")) {
            FinnAd ad = new FinnAd(connector.parseReaderToDocument(reader));
            FinnAd.GeneralText text;
            assertEquals(2, ad.getGeneralText().size());
            text = ad.getGeneralText().get(0);
            assertEquals(null, text.getTitle());
            assertTrue(text.getValue().contains("Avdelingslederen skal ved siden av å være"));
            text = ad.getGeneralText().get(1);
            assertEquals("Arbeidsoppgaver", text.getTitle());
            assertEquals("Test", text.getValue());}
    }

    @Test
    public void handleAd3WithMultipleLogoUrls()
            throws Exception {
        try (Reader reader = getReader("src/test/resources/finn/samples/ad3.xml")) {
            FinnAd ad = new FinnAd(connector.parseReaderToDocument(reader));
            assertEquals(2, ad.getLogoUrlList().size());

            assertTrue(ad.getLogoUrlList().contains("https://images.finncdn.no/dynamic/default/2017/10/2/3/105/360/823_1965618946.png"));
            assertTrue(ad.getLogoUrlList().contains("http://logo.proffice.com/images/Fett_%c3%98konomi_AS.png"));
        }
    }
}
