package no.nav.pam.annonsemottak.temp.feedclient;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import no.nav.pam.annonsemottak.stilling.AnnonseStatus;
import no.nav.pam.annonsemottak.stilling.Status;
import no.nav.pam.annonsemottak.stilling.Stilling;
import no.nav.pam.annonsemottak.stilling.StillingRepository;
import no.nav.pam.feed.client.FeedConnector;
import no.nav.pam.feed.client.FeedTransport;
import no.nav.pam.feed.taskscheduler.FeedTaskService;
import org.assertj.core.api.SoftAssertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Optional;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Rollback
public class FeedClientServiceTest {

    private static final String FEED_FILE = "src/test/resources/temp/feedclient.samples/feed1.json";
    private static final String FEED_FILE2 = "src/test/resources/temp/feedclient.samples/feed2.json";
    private static final String FEED_FILE_WITH_ONE_ITEM = "src/test/resources/temp/feedclient.samples/feed3_only_one_item.json";
    private static final String FEED_FILE_WITH_DUPLICATES = "src/test/resources/temp/feedclient.samples/feed1_with_duplicate_item.json";
    private static final String URL = "https://pamtest/";

    @Mock
    private FeedConnector connector;

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Autowired
    private FeedTaskService feedTaskService;

    @Autowired
    private StillingRepository stillingRepository;

    private FeedClientService service;

    @Qualifier("jacksonMapper")
    @Autowired
    private ObjectMapper mapper;


    @Before
    public void init() {

        service = new FeedClientService(feedTaskService, connector, new SimpleMeterRegistry(), URL, stillingRepository);
    }

    @Test
    public void should_fetch_and_save_for_the_first_time() throws IOException {
        FeedTransport<StillingFeedItem> feedTransport = mapper.readValue(new File(FEED_FILE), new TypeReference<FeedTransport<StillingFeedItem>>() {
        });
        when(connector.fetchContentList(URL, 0, StillingFeedItem.class)).thenReturn(feedTransport.content);

        assertFalse(feedTaskService.fetchLastRunDateForJob(FeedClientService.TASK_NAME).isPresent());

        service.fetchAndSaveLatestAds();

        verify(connector).fetchContentList(URL, 0, StillingFeedItem.class);
        assertEquals(10, stillingRepository.findUuids().size());

        //Test lastUpdatedDate is set correctly
        Optional<LocalDateTime> lastUpdatedDateOptional = feedTaskService.fetchLastRunDateForJob(FeedClientService.TASK_NAME);
        assertTrue(lastUpdatedDateOptional.isPresent());
        feedTransport.content.sort(Comparator.comparing(o -> o.updated));
        LocalDateTime exptectedLastUpdatedDate = feedTransport.content.get(feedTransport.content.size() - 1).updated;
        assertEquals(exptectedLastUpdatedDate, lastUpdatedDateOptional.get());
    }

    @Test
    public void should_fetch_and_save_after_first_run() throws IOException {
        LocalDateTime dateTime = LocalDateTime.now().minusDays(2);
        FeedTransport<StillingFeedItem> feedTransport = mapper.readValue(new File(FEED_FILE), new TypeReference<FeedTransport<StillingFeedItem>>() {
        });
        when(connector.fetchContentList(URL, dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(), StillingFeedItem.class)).thenReturn(feedTransport.content);

        //Set last run date to 2 days ago
        feedTaskService.save(FeedClientService.TASK_NAME, dateTime);

        service.fetchAndSaveLatestAds();
        verify(connector).fetchContentList(URL, dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(), StillingFeedItem.class);

        //Test mapping for one of he ads
        Stilling ad1 = stillingRepository.findByUuid("de1d8755-98ef-4e63-8626-2fd96a4f2706");
        assertNotNull(ad1);

        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(ad1.getMedium()).isEqualTo("FINN");
        softAssertions.assertThat(ad1.getExternalId()).isEqualTo("108499379");
        softAssertions.assertThat(ad1.getUrl()).isEqualTo("https://www.finn.no/108499379");
        softAssertions.assertThat(ad1.getStatus()).isEqualTo(Status.MOTTATT);
        softAssertions.assertThat(ad1.getAnnonseStatus()).isEqualTo(AnnonseStatus.AKTIV);
        softAssertions.assertAll();
    }

    @Test
    public void should_be_distinct_before_save() throws IOException {
        FeedTransport<StillingFeedItem> feedTransport = mapper.readValue(new File(FEED_FILE_WITH_DUPLICATES), new TypeReference<FeedTransport<StillingFeedItem>>() {
        });
        when(connector.fetchContentList(URL, 0, StillingFeedItem.class)).thenReturn(feedTransport.content);

        assertFalse(feedTaskService.fetchLastRunDateForJob(FeedClientService.TASK_NAME).isPresent());

        service.fetchAndSaveLatestAds();

        verify(connector).fetchContentList(URL, 0, StillingFeedItem.class);
        assertEquals(10, stillingRepository.findUuids().size());
    }


    @Test
    public void should_fetch_and_save_feed_with_uuid() throws IOException {
        String uuid = "897e5cda-6529-4bb4-bdf0-90474f0e74e6";
        String path = URL + "/" + uuid;
        FeedTransport<StillingFeedItem> feedTransport = mapper.readValue(new File(FEED_FILE_WITH_ONE_ITEM),
                new TypeReference<FeedTransport<StillingFeedItem>>() {
                });
        when(connector.fetchContentList(path, 0, StillingFeedItem.class)).thenReturn(feedTransport.content);

        service.fetchAndSaveOneAd(uuid);
        verify(connector).fetchContentList(path, 0, StillingFeedItem.class);

        //Test mapping for one of he ads
        Stilling ad1 = stillingRepository.findByUuid(uuid);
        assertNotNull(ad1);

        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(ad1.getCreated()).isEqualTo(LocalDateTime.parse("2017-12-14T08:38:39.744"));
        softAssertions.assertThat(ad1.getCreatedBy()).isEqualTo("test1234");
        softAssertions.assertThat(ad1.getCreatedByDisplayName()).isEqualTo("Testuser Displayname");
        softAssertions.assertThat(ad1.getUpdated()).isEqualTo(LocalDateTime.parse("2017-12-14T08:38:39.744"));
        softAssertions.assertThat(ad1.getUpdatedBy()).isEqualTo("test1234");
        softAssertions.assertThat(ad1.getUpdatedByDisplayName()).isEqualTo("Testuser Displayname");
        softAssertions.assertThat(ad1.getUuid()).isEqualTo("897e5cda-6529-4bb4-bdf0-90474f0e74e6");
        softAssertions.assertThat(ad1.getKilde()).isEqualTo("DEXI");
        softAssertions.assertThat(ad1.getMedium()).isEqualTo("www.norgesgruppen.no");
        softAssertions.assertThat(ad1.getUrl()).isEqualTo("http://www.norgesgruppen.no/muligheter/ledige-stillinger/?advertId=1616");
        softAssertions.assertThat(ad1.getPublished()).isNull();
        softAssertions.assertThat(ad1.getAnnonseStatus()).isEqualTo(AnnonseStatus.AKTIV);
        softAssertions.assertThat(ad1.getExpires()).isEqualTo(LocalDateTime.parse("2018-01-11T00:00"));
        softAssertions.assertThat(ad1.getSystemModifiedDate()).isNull();
        softAssertions.assertThat(ad1.getStatus()).isEqualTo(Status.AVVIST);
        softAssertions.assertThat(ad1.getArbeidsgiveromtale()).isEqualTo("KIWI minipris er en av landets mest ekspansive lavpriskjeder og omfatter over 650 butikker. I 2017 forventer vi en samlet omsetning på 35,4 milliarder kroner. Kjedekonseptet er faste, lave priser på alle varer og en kostnadseffektiv drift. KIWI minipris er med i NorgesGruppen som er Norges største dagligvaregruppering.\n\n");
        softAssertions.assertThat(ad1.getArbeidsgiver().get().asString()).isEqualTo("KIWI Tigerplassen");
        softAssertions.assertThat(ad1.getStillingstittel()).isEqualTo("KIWI Tigerplassen søker etter butikkmedarbeider deltid 20 %");
        softAssertions.assertThat(ad1.getSaksbehandler().get().asString()).isEqualTo("Eziz");
        softAssertions.assertThat(ad1.getKommentarer().get().asString()).isEqualTo("Duplikat av id: 9608306");
        softAssertions.assertThat(ad1.getMerknader().get().asString()).isEqualTo("3");
        softAssertions.assertThat(ad1.getArbeidssted()).isEqualTo("Moss");
        softAssertions.assertThat(ad1.getSoeknadsfrist()).isEqualTo("11.01.2018");
        softAssertions.assertThat(ad1.getAnnonsetekst()).isEqualTo("Arbeidsoppgaver:\n* Hovedsakelig kassererarbeid\n* Kundebehandling\n* Varehåndtering\n* Forefallende butikkarbeid\n\nPersonlige egenskaper:\n* Må være fylt 18 år\n* Punktlig\n* Utadvendt og ærlig\n* Serviceinnstilt\n* Ryddig\n* Godt humør\n\nKvalifikasjoner:\n* Erfaring fra dagligvare er ønskelig, men ingen betingelse\n\nVi kan tilby:\n* Opplæring\n* Karrieremuligheter\n* Sosialt og trivelig arbeidsplass blant hyggelige kolleger\n\nNB! Vi gjør oppmerksom på at alle søknader må sendes inn via søknadskjema tilknyttet annonsen, samt at det meste av kommunikasjonen vedrørende stillingen vil skje per e-post.\n\n");

        softAssertions.assertThat(ad1.getProperties().get("Annonsor")).isEqualTo("Norgesgruppen");
        softAssertions.assertThat(ad1.getProperties().keySet().size()).isEqualTo(8);

        softAssertions.assertAll();
    }

}
