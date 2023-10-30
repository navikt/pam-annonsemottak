package no.nav.pam.annonsemottak.receivers.finn;

import no.nav.pam.annonsemottak.app.metrics.AnnonseMottakProbe;
import no.nav.pam.annonsemottak.receivers.Kilde;
import no.nav.pam.annonsemottak.receivers.Medium;
import no.nav.pam.annonsemottak.receivers.externalRun.ExternalRun;
import no.nav.pam.annonsemottak.receivers.externalRun.ExternalRunService;
import no.nav.pam.annonsemottak.receivers.fangst.AnnonseFangstService;
import no.nav.pam.annonsemottak.receivers.fangst.AnnonseResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class FinnServiceTest {

    private FinnService finnService;

    private FinnConnector mockedConnector;
    private ExternalRunService mockedExternalRunService;
    private AnnonseFangstService mockedAnnonseFangstService;
    private AnnonseMottakProbe probe = mock(AnnonseMottakProbe.class);


    @BeforeEach
    public void init() {
        mockedConnector = mock(FinnConnector.class);
        mockedExternalRunService = mock(ExternalRunService.class);
        mockedAnnonseFangstService = mock(AnnonseFangstService.class);

        finnService = new FinnService(mockedAnnonseFangstService, mockedConnector, mockedExternalRunService, probe);
    }

    @Test
    public void shouldFilterNewAndUpdatedForSave() throws FinnConnectorException {

        //Last run yesterday
        ExternalRun externalRun = new ExternalRun(Kilde.FINN.toString(), Medium.FINN.toString(), LocalDateTime.now().minusDays(1));
        when(mockedExternalRunService.findByNameAndMedium(Kilde.FINN.toString(), Medium.FINN.toString())).thenReturn(externalRun);

        Set<FinnAdHead> searchResult = new HashSet<>();

        // TODO: Temporary dates with 4 day delay. Switch back to commented lines, once the 4 day delay is disabled.
        searchResult.add(generateAdHeadWithDates("1", LocalDateTime.now().minusDays(3), LocalDateTime.now(), LocalDateTime.now().plusMonths(1))); // Existing but changed
        searchResult.add(generateAdHeadWithDates("2", LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(2), LocalDateTime.now().plusMonths(1))); // Existing
        searchResult.add(generateAdHeadWithDates("3", LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(3), LocalDateTime.now().plusMonths(1))); // Existing
        searchResult.add(generateAdHeadWithDates("4", LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now().plusMonths(1))); // New ad

        when(mockedConnector.fetchSearchResult()).thenReturn(searchResult);

        when(mockedAnnonseFangstService.retrieveAnnonseLists(anyList(), anySet(), eq(Kilde.FINN.toString()), eq(Medium.FINN.toString()))).thenReturn(new AnnonseResult());

        finnService.saveAndUpdateFromCollection();

        ArgumentCaptor<Set> argumentCaptor = ArgumentCaptor.forClass(Set.class);
        verify(mockedConnector).fetchFullAds(argumentCaptor.capture());
        Set<FinnAdHead> capturedArgument = argumentCaptor.<Set<FinnAdHead>> getValue();
        assertEquals(2, capturedArgument.size());
    }

    @Test
    public void shouldSaveAll() throws FinnConnectorException {

        //Last run yesterday
        when(mockedExternalRunService.findByNameAndMedium(Kilde.FINN.toString(), Medium.FINN.toString())).thenReturn(null);

        Set<FinnAdHead> searchResult = new HashSet<>();
        searchResult.add(generateAdHeadWithDates("1", LocalDateTime.now().minusDays(2), LocalDateTime.now(), LocalDateTime.now().plusMonths(1))); // Existing but changed
        searchResult.add(generateAdHeadWithDates("2", LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(2), LocalDateTime.now().plusMonths(1))); // Existing
        searchResult.add(generateAdHeadWithDates("3", LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(2), LocalDateTime.now().plusMonths(1))); // Existing
        searchResult.add(generateAdHeadWithDates("4", LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now().plusMonths(1))); // New ad
        when(mockedConnector.fetchSearchResult()).thenReturn(searchResult);

        when(mockedAnnonseFangstService.retrieveAnnonseLists(anyList(), anySet(), eq(Kilde.FINN.toString()), eq(Medium.FINN.toString()))).thenReturn(new AnnonseResult());

        finnService.saveAndUpdateFromCollection();

        verify(mockedConnector).fetchFullAds(searchResult);
    }


    private FinnAdHead generateAdHeadWithDates(String externalId, LocalDateTime published, LocalDateTime updated, LocalDateTime expires) {
        FinnAdHead adHead = new FinnAdHead();
        adHead.setId(externalId);
        adHead.setPublished(published);
        adHead.setUpdated(updated);
        adHead.setExpires(expires);

        return adHead;
    }
}
