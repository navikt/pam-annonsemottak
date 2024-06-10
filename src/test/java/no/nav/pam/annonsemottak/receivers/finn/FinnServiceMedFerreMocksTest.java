package no.nav.pam.annonsemottak.receivers.finn;

import jakarta.inject.Inject;
import no.nav.pam.annonsemottak.Application;
import no.nav.pam.annonsemottak.app.metrics.AnnonseMottakProbe;
import no.nav.pam.annonsemottak.outbox.StillingOutbox;
import no.nav.pam.annonsemottak.outbox.StillingOutboxRepository;
import no.nav.pam.annonsemottak.outbox.StillingOutboxService;
import no.nav.pam.annonsemottak.receivers.Kilde;
import no.nav.pam.annonsemottak.receivers.Medium;
import no.nav.pam.annonsemottak.receivers.externalRun.ExternalRunService;
import no.nav.pam.annonsemottak.receivers.fangst.AnnonseFangstService;
import no.nav.pam.annonsemottak.stilling.Stilling;
import no.nav.pam.annonsemottak.stilling.StillingRepository;
import no.nav.pam.annonsemottak.stilling.StillingTestdataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;

import java.io.Reader;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@Rollback
@Transactional
@ContextConfiguration(classes = Application.class)
public class FinnServiceMedFerreMocksTest {

    @Inject
    private StillingRepository stillingRepository;
    @Inject
    private StillingOutboxService stillingOutboxService;
    @Inject
    private StillingOutboxRepository stillingOutboxRepository;
    private FinnService finnService;

    private FinnConnector mockFinnConnector;
    private ExternalRunService mockedExternalRunService;
    private AnnonseFangstService annonseFangstService;
    private AnnonseMottakProbe probe = mock(AnnonseMottakProbe.class);

    private AnnonseFangstService annonseFangstSpy;


    @BeforeEach
    public void init() {
        mockFinnConnector = mock(FinnConnector.class);
        mockedExternalRunService = mock(ExternalRunService.class);
        annonseFangstService = new AnnonseFangstService(stillingRepository, stillingOutboxService);

        annonseFangstSpy = spy(annonseFangstService);
        finnService = new FinnService(annonseFangstService, mockFinnConnector, mockedExternalRunService, probe);
    }

    @Test
    public void lagrerUtenFeil() throws Exception {
        Set<FinnAdHead> searchResult = new HashSet<>();
        Set<FinnAd> finnAds = new HashSet<>();

        FinnAd finnAd = generateFinnAd();
        finnAds.add(finnAd);
        String externalId = finnAd.getId().split(":")[2];

        Stilling s = StillingTestdataBuilder.enkelStilling().externalId(externalId).properties(Map.of("arbeidsdag", "[\"Ukedager\"]", "orgnummer", "1234567")).medium("FINN").kilde("FINN").build();
        stillingRepository.save(s);
        searchResult.add(generateAdHeadWithDates(externalId, LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now().plusMonths(1)));

        when(mockedExternalRunService.findByNameAndMedium(Kilde.FINN.toString(), Medium.FINN.toString())).thenReturn(null);
        when(mockFinnConnector.fetchSearchResult()).thenReturn(searchResult);
        when(mockFinnConnector.fetchFullAds(searchResult)).thenReturn(finnAds);
        finnService.saveAndUpdateFromCollection();

        Stilling lagretStilling = stillingRepository.findByUuid(s.getUuid()).get();
        StillingOutbox lagretStillingOutbox = stillingOutboxRepository.hentUprosesserteMeldinger(500, 0).get(0);

        assertNotNull(lagretStilling);
        assertNotNull(lagretStillingOutbox);
    }


    private FinnAdHead generateAdHeadWithDates(String externalId, LocalDateTime published, LocalDateTime updated, LocalDateTime expires) {
        FinnAdHead adHead = new FinnAdHead();
        adHead.setId(externalId);
        adHead.setPublished(published);
        adHead.setUpdated(updated);
        adHead.setExpires(expires);

        return adHead;
    }

    private FinnAd generateFinnAd() throws Exception {
        FinnConnector finnConnector = new FinnConnector(null, null, null,null, null, 0);
        try (Reader reader = FinnConnectorTest.getReader("src/test/resources/finn/samples/ad1.xml")) {
            Document document = finnConnector.parseReaderToDocument(reader);
            return new FinnAd(document);
        }
    }
}
