package no.nav.pam.annonsemottak.annonsemottak.dexi;

import no.nav.pam.annonsemottak.annonsemottak.common.rest.payloads.ResultsOnSave;
import no.nav.pam.annonsemottak.annonsemottak.fangst.AnnonseResult;
import no.nav.pam.annonsemottak.annonsemottak.fangst.DexiAnnonseFangstService;
import no.nav.pam.annonsemottak.stilling.StillingRepository;
import org.junit.Before;
import org.junit.Test;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DexiServiceTest {

    private DexiService dexiService;
    private DexiConnector mockedDexiConnector;
    private StillingRepository mockedStillingRepository;
    private DexiAnnonseFangstService mockedAnnonseFangstService;

    @Before
    public void init() throws IOException {
        //Instantiate mocks
        mockedStillingRepository = mock(StillingRepository.class);
        mockedDexiConnector = mock(DexiConnector.class);
        mockedAnnonseFangstService = mock(DexiAnnonseFangstService.class);

        //TODO: Improve this mock so the results are better
        AnnonseResult annonseResult = new AnnonseResult();

        List<DexiConfiguration> configs = new ArrayList<>();
        configs.add(new DexiConfiguration("robot-1", "robot 1", "job-1", "job 1"));
        configs.add(new DexiConfiguration("robot-2", "robot 2", "job-2", "job 2"));
        configs.add(new DexiConfiguration("robot-3", "robot 3", "job-3", "job 3"));
        when(mockedDexiConnector.getConfigurations(DexiConfiguration.PRODUCTION)).thenReturn(configs);
        when(mockedAnnonseFangstService.retrieveAnnonseLists(anyList(), anyString(), anyString())).thenReturn(annonseResult);
        dexiService = new DexiService(mockedDexiConnector, mockedAnnonseFangstService);
    }

    @Test
    public void saveLatestResultsFromAllJobsWithoutFail() throws DexiException, IOException {
        when(mockedDexiConnector.getLatestResultForJobID("job-1")).thenReturn(getMockedLatestResultForJobID("72441a26-4d72-4cae-b348-7b5efe5714d0"));
        when(mockedDexiConnector.getLatestResultForJobID("job-2")).thenReturn(getMockedLatestResultForJobID("92aca435-0df2-4b05-8150-d9f4b84ff087"));
        when(mockedDexiConnector.getLatestResultForJobID("job-3")).thenReturn(getMockedLatestResultForJobID("72441a26-4d72-4cae-b348-7b5efe5714d0"));

        ResultsOnSave result = dexiService.saveLatestResultsFromAllJobs();

        assertNotNull(result);
        assertEquals(6, result.getReceived());
        assertEquals(0, result.getSaved()); // since mock AnnonseResult is 0
    }

    @Test
    public void saveLatestResultsFromAllJobsWithOneFail() throws DexiException, IOException {
        when(mockedDexiConnector.getLatestResultForJobID("job-1")).thenThrow(new IOException());
        when(mockedDexiConnector.getLatestResultForJobID("job-2")).thenReturn(getMockedLatestResultForJobID("72441a26-4d72-4cae-b348-7b5efe5714d0"));

        ResultsOnSave result = dexiService.saveLatestResultsFromAllJobs();

        assertNotNull(result);
        assertEquals(3, result.getReceived());
    }

    private List<Map<String, String>> getMockedLatestResultForJobID(String id) throws IOException {
        try (Reader reader = new FileReader("src/test/resources/dexi.io/samples/" + id + ".json")) {
            DexiConnector connector = new DexiConnector(null,null,null,null);

            Map results = connector.deserialize(reader);
            return connector.convertToProperJson(results);
        }
    }
}
