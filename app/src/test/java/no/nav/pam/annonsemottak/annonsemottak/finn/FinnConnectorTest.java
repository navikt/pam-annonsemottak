package no.nav.pam.annonsemottak.annonsemottak.finn;

import org.assertj.core.api.SoftAssertions;
import org.junit.Test;

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
            softAssert.assertThat(ad.getId()).isEqualTo("urn:id:91758798");
            softAssert.assertThat(ad.getTitle()).isEqualTo("Vil du lede den digitale reisen?");
            softAssert.assertThat(ad.getUpdated()).isEqualTo("2017-03-02T15:05:00.000Z");
            softAssert.assertThat(ad.getPublished()).isEqualTo("2017-03-02T15:01:00.000Z");
            softAssert.assertThat(ad.getDateSubmitted()).isEqualTo("2017-03-02T15:01:00.000Z");
            softAssert.assertThat(ad.getExpires()).isEqualTo("2017-04-13T21:59:00.000Z");
            softAssert.assertThat(ad.getEdited()).isEqualTo("2017-03-02T15:05:00.000Z");
            softAssert.assertThat(ad.getIdentifier()).isEqualTo("91758798");
            softAssert.assertThat(ad.isPrivate()).isFalse();
            softAssert.assertThat(ad.getType()).isEqualTo("job-management");
            softAssert.assertThat(ad.isActive()).isTrue();
            softAssert.assertThat(ad.isDisposed()).isFalse();
            softAssert.assertThat(ad.getLinkToApply().get(0)).isEqualTo("http://portal.crmasp.no/TopTempOnlinePortalWeb/Account/RegisterExtended?aid=sQ7x2j4wLuZrx/PZbYky8YfId7c/iknHJweajlgylXFtHt8d%2BH53d2zUEwlWaIHLRgwGEKPLRI37pdEdaYcIPkDCf97wnw1exjbSOuOc7aw%3D");
            softAssert.assertThat(ad.getLocation().getAddress()).isEqualTo("DEPOTGATA 22");
            softAssert.assertThat(ad.getLocation().getPostalCode()).isEqualTo("2000");
            softAssert.assertThat(ad.getLocation().getCity()).isEqualTo("Lillestrøm");
            softAssert.assertThat(ad.getLocation().getCountry()).isEqualTo("Norge");
            softAssert.assertThat(ad.getAdvertiserReference()).isEqualTo("Charlotte - Top Temp");
            softAssert.assertThat(ad.getApplicationDeadline()).isEqualTo("19.03.2017");
            softAssert.assertThat(ad.getCompany().getName()).isEqualTo("Felleskjøpet");
            softAssert.assertThat(ad.getCompany().getIngress().startsWith("Felleskjøpet er den ledende")).isTrue();
            softAssert.assertThat(ad.getCompany().getUrl()).isEqualTo("http://www.felleskjopet.no");
            softAssert.assertThat(ad.getDuration()).isEqualTo("Fast");
            softAssert.assertThat(ad.getIndustry().size()).isEqualTo(2);
            softAssert.assertThat(ad.getIndustry().contains("Jordbruk og skogbruk")).isTrue();
            softAssert.assertThat(ad.getIndustry().contains("Butikk og varehandel")).isTrue();
            softAssert.assertThat(ad.getJobTitle()).isEqualTo("Leder for Digitale Kanaler");
            softAssert.assertThat(ad.getKeywords().size()).isEqualTo(5);
            softAssert.assertThat(ad.getKeywords().contains("digitaldirektør")).isTrue();
            softAssert.assertThat(ad.getKeywords().contains("digitalisering")).isTrue();
            softAssert.assertThat(ad.getKeywords().contains("digitale")).isTrue();
            softAssert.assertThat(ad.getKeywords().contains("ehandel")).isTrue();
            softAssert.assertThat(ad.getKeywords().contains("ledelse")).isTrue();
            softAssert.assertThat(ad.getManagerRole()).isEqualTo("Leder");
            softAssert.assertThat(ad.getProviderId()).isEqualTo("0");
            softAssert.assertThat(ad.getSector()).isEqualTo("Privat");
            softAssert.assertThat(ad.getSituation()).isEqualTo("Lillestrøm");
            softAssert.assertThat(ad.getStartDate()).isEqualTo("Etter avtale");

            //Workplaces
            softAssert.assertThat(ad.getWorkplaces().size()).isEqualTo(2);
            softAssert.assertThat(ad.getWorkplaces().contains("DEPOTGATA 22")).isTrue();
            softAssert.assertThat(ad.getWorkplaces().contains("2000 Lillestrøm")).isTrue();

            //Contacts
            softAssert.assertThat(ad.getContacts().get(0).getName()).isEqualTo("Charlotte Silkebekken Bergerud");
            softAssert.assertThat(ad.getContacts().get(0).getEmail()).isEqualTo("csb@toptemp.no");
            softAssert.assertThat(ad.getContacts().get(0).getTitle()).isEqualTo("Senior rekrutteringsrådgiver");
            softAssert.assertThat(ad.getContacts().get(0).getPhone_work()).isEqualTo("+47 958 32 353");

            //Geo locations
            softAssert.assertThat(ad.getGeoLocation().getAccuracy()).isEqualTo("9");
            softAssert.assertThat(ad.getGeoLocation().getLatitude()).isEqualTo("59.95626");
            softAssert.assertThat(ad.getGeoLocation().getLongitude()).isEqualTo("11.03524");

            //Occupations
            softAssert.assertThat(ad.getOccupations().size()).isEqualTo(3);
            softAssert.assertThat(ad.getOccupations().contains("Ledelse")).isTrue();
            softAssert.assertThat(ad.getOccupations().contains("Markedsfører")).isTrue();
            softAssert.assertThat(ad.getOccupations().contains("Forretningsutvikling og strategi")).isTrue();

            // Test Logo urls are correct
            softAssert.assertThat(ad.getAuthor().getUrlListLogo()).isEqualTo("https://images.finncdn.no/mmo/logo/result/2083520418/iad_5153168901682665885ikon-finn-amesto-top-temp.png");
            softAssert.assertThat(ad.getAuthor().getUrlMainLogo()).isEqualTo("https://images.finncdn.no/mmo/logo/object/2083520418/iad_9204971676583467114amestotoptemp_logo_fra_januar_2015.png");
            softAssert.assertThat(ad.getLogoUrlList().size()).isEqualTo(1);
            softAssert.assertThat(ad.getLogoUrlList().get(0)).isEqualTo("https://images.finncdn.no/mmo/2017/3/2/8/917/587/98_2118645667.png");
            softAssert.assertAll();
        }
    }

    @Test
    public void handleAd2WithMultipleGeneralTextEntries()
            throws Exception {
        try (Reader reader = getReader("src/test/resources/finn/samples/ad2.xml")) {
            FinnAd ad = new FinnAd(connector.parseReaderToDocument(reader));
            FinnAd.GeneralText text;
            assertEquals(7, ad.getGeneralText().size());
            text = ad.getGeneralText().get(0);
            assertEquals(null, text.getTitle());
            assertTrue(text.getValue().startsWith("Vi søker dyktig prosjektleder"));
            text = ad.getGeneralText().get(1);
            assertEquals("Arbeidsoppgaver", text.getTitle());
            assertTrue(text.getValue().startsWith("<ul><li>Lede prosjektet"));
            text = ad.getGeneralText().get(2);
            assertEquals("Kvalifikasjoner", text.getTitle());
            assertTrue(text.getValue().startsWith("<ul><li>Ledererfaring"));
            text = ad.getGeneralText().get(3);
            assertEquals("Utdanning", text.getTitle());
            assertTrue(text.getValue().startsWith("<ul><li>Fagskole"));
            text = ad.getGeneralText().get(4);
            assertEquals("Språk", text.getTitle());
            assertTrue(text.getValue().startsWith("<ul><li>Engelsk"));
            text = ad.getGeneralText().get(5);
            assertEquals("Egenskaper", text.getTitle());
            assertTrue(text.getValue().startsWith("<ul><li>Resultatorientert"));
            text = ad.getGeneralText().get(6);
            assertEquals("Vi tilbyr", text.getTitle());
            assertTrue(text.getValue().startsWith("<ul><li>Stillingsbrøk"));
        }
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
