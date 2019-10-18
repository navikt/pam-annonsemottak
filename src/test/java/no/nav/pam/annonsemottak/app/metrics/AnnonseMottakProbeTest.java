package no.nav.pam.annonsemottak.app.metrics;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class AnnonseMottakProbeTest {

    private SensuClient sensuClient = mock(SensuClient.class);

    private AnnonseMottakProbe annonseMottakProbe = new AnnonseMottakProbe(sensuClient);

    private String sensuEvent =
            "{" +
                    "\"name\":\"annonsemottak-events\"," +
                    "\"type\":\"metric\"," +
                    "\"handlers\":[\"events_nano\"]," +
                    "\"output\":\"annonsemottak.ads.collected.v2.changed,application=pam-annonsemottak,cluster=dev-fss,namespace=default,origin=Molde\\ kommune,source=DEXI counter=5i 1571349494135000000\"," +
                    "\"status\":0" +
                    "}";
    @Test
    @Ignore
    public void verifySensuCall() {
        annonseMottakProbe.changedAdPoint(5L, "DEXI", "Molde kommune");

        ArgumentCaptor<SensuClient.SensuEvent> captor = ArgumentCaptor.forClass(SensuClient.SensuEvent.class);
        verify(sensuClient).write(captor.capture());
        Assert.assertEquals(sensuEvent, captor.getValue().getJson());
    }


}
