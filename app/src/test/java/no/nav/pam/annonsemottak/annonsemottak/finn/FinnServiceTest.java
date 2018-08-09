package no.nav.pam.annonsemottak.annonsemottak.finn;

import no.nav.pam.annonsemottak.annonsemottak.Kilde;
import no.nav.pam.annonsemottak.annonsemottak.Medium;
import no.nav.pam.annonsemottak.annonsemottak.common.rest.payloads.ResultsOnSave;
import no.nav.pam.annonsemottak.annonsemottak.externalRuns.ExternalRun;
import no.nav.pam.annonsemottak.annonsemottak.externalRuns.ExternalRunsService;
import no.nav.pam.annonsemottak.annonsemottak.fangst.AnnonseFangstService;
import no.nav.pam.annonsemottak.annonsemottak.fangst.AnnonseResult;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class FinnServiceTest {

    private FinnService finnService;

    private FinnConnector mockedConnector;
    private ExternalRunsService mockedExternalRunService;
    private AnnonseFangstService mockedAnnonseFangstService;


    @Before
    public void init() throws FinnConnectorException, ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        mockedConnector = mock(FinnConnector.class);
        mockedExternalRunService = mock(ExternalRunsService.class);
        mockedAnnonseFangstService = mock(AnnonseFangstService.class);

        finnService = new FinnService(mockedAnnonseFangstService, mockedConnector, mockedExternalRunService);
    }

    @Test
    public void shouldFilterNewAndUpdatedForSave() throws FinnConnectorException, ParserConfigurationException, SAXException, IOException, XPathExpressionException {

        //Last run yesterday
        ExternalRun externalRun = new ExternalRun(Kilde.FINN.toString(), Medium.FINN.toString(), LocalDateTime.now().minusDays(1));
        when(mockedExternalRunService.findByNameAndMedium(Kilde.FINN.toString(), Medium.FINN.toString())).thenReturn(externalRun);

        Set<FinnAdHead> searchResult = new HashSet<>();

//        searchResult.add(generateAdHeadWithDates("1", DateTime.now().minusDays(2), DateTime.now(), DateTime.now().plusMonths(1))); // Existing but changed
//        searchResult.add(generateAdHeadWithDates("2", DateTime.now().minusDays(2), DateTime.now().minusDays(2), DateTime.now().plusMonths(1))); // Existing
//        searchResult.add(generateAdHeadWithDates("3", DateTime.now().minusDays(2), DateTime.now().minusDays(2), DateTime.now().plusMonths(1))); // Existing
//        searchResult.add(generateAdHeadWithDates("4", DateTime.now(), DateTime.now(), DateTime.now().plusMonths(1))); // New ad

        // TODO: Temporary dates with 4 day delay. Switch back to commented lines, once the 4 day delay is disabled.
        searchResult.add(generateAdHeadWithDates("1", LocalDateTime.now().minusDays(3), LocalDateTime.now(), LocalDateTime.now().plusMonths(1))); // Existing but changed
        searchResult.add(generateAdHeadWithDates("2", LocalDateTime.now().minusDays(1), LocalDateTime.now().minusDays(2), LocalDateTime.now().plusMonths(1))); // Existing
        searchResult.add(generateAdHeadWithDates("3", LocalDateTime.now().minusDays(1), LocalDateTime.now().minusDays(3), LocalDateTime.now().plusMonths(1))); // Existing
        searchResult.add(generateAdHeadWithDates("4", LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now().plusMonths(1))); // New ad

        when(mockedConnector.fetchSearchResult("TEST")).thenReturn(searchResult);

        when(mockedAnnonseFangstService.retrieveAnnonseLists(anyList(), anySet(), eq(Kilde.FINN.toString()), eq(Medium.FINN.toString()))).thenReturn(new AnnonseResult());

        ResultsOnSave result =  finnService.saveAndUpdateFromCollection("TEST");

        ArgumentCaptor<Set> argumentCaptor = ArgumentCaptor.forClass(Set.class);
        verify(mockedConnector).fetchFullAds(argumentCaptor.capture());
        Set<FinnAdHead> capturedArgument = argumentCaptor.<Set<FinnAdHead>> getValue();
        assertEquals(2, capturedArgument.size());
    }

    @Test
    public void shouldSaveAll() throws FinnConnectorException, ParserConfigurationException, SAXException, IOException, XPathExpressionException {

        //Last run yesterday
        when(mockedExternalRunService.findByNameAndMedium(Kilde.FINN.toString(), Medium.FINN.toString())).thenReturn(null);

        Set<FinnAdHead> searchResult = new HashSet<>();
        searchResult.add(generateAdHeadWithDates("1", LocalDateTime.now().minusDays(2), LocalDateTime.now(), LocalDateTime.now().plusMonths(1))); // Existing but changed
        searchResult.add(generateAdHeadWithDates("2", LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(2), LocalDateTime.now().plusMonths(1))); // Existing
        searchResult.add(generateAdHeadWithDates("3", LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(2), LocalDateTime.now().plusMonths(1))); // Existing
        searchResult.add(generateAdHeadWithDates("4", LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now().plusMonths(1))); // New ad
        when(mockedConnector.fetchSearchResult("TEST")).thenReturn(searchResult);

        when(mockedAnnonseFangstService.retrieveAnnonseLists(anyList(), anySet(), eq(Kilde.FINN.toString()), eq(Medium.FINN.toString()))).thenReturn(new AnnonseResult());

        ResultsOnSave result =  finnService.saveAndUpdateFromCollection("TEST");

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
