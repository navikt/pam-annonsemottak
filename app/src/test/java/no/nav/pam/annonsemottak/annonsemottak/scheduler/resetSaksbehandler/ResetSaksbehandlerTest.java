package no.nav.pam.annonsemottak.annonsemottak.scheduler.resetSaksbehandler;

import no.nav.pam.annonsemottak.stilling.AnnonseStatus;
import no.nav.pam.annonsemottak.stilling.Status;
import no.nav.pam.annonsemottak.stilling.Stilling;
import no.nav.pam.annonsemottak.stilling.StillingRepository;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static no.nav.pam.annonsemottak.stilling.StillingTestdataBuilder.enkelStilling;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ResetSaksbehandlerTest {

    private ResetSaksbehandlerService service;

    @Test
    public void should_reset_old_ads(){
        List<Stilling> ads = new ArrayList<>();
        ads.add(0, enkelStilling().saksbehandler("Saksbehandler 1").status(Status.UNDER_ARBEID).build());
        ads.add(1, enkelStilling().saksbehandler("Saksbehandler 2").status(Status.UNDER_ARBEID).build());
        ads.add(2, enkelStilling().status(Status.MOTTATT).build());
        ads.get(0).setUpdated(LocalDateTime.now().minusDays(1));
        ads.get(1).setUpdated(LocalDateTime.now().minusDays(8));

        StillingRepository mockedStillingRepository = mock(StillingRepository.class);
        when(mockedStillingRepository.findBySaksbehandlingStatusAndAnnonseStatus(Status.UNDER_ARBEID, AnnonseStatus.AKTIV)).thenReturn(ads);
        service = new ResetSaksbehandlerService(mockedStillingRepository);

        List<Stilling> beforeReset = filterStillingWithNoSaksbehandlerAndStatus(ads);
        Assert.assertEquals(1, beforeReset.size());

        service.resetSaksbehandler();

        List<Stilling> afterReset = filterStillingWithNoSaksbehandlerAndStatus(ads);
        Assert.assertEquals(2, afterReset.size());

        afterReset.stream().filter(s -> s.getSaksbehandler().isPresent());
    }

    private List<Stilling> filterStillingWithNoSaksbehandlerAndStatus(List<Stilling> ads){
        return ads.stream()
                .filter(s -> !s.getSaksbehandler().isPresent() && s.getStatus() == Status.MOTTATT)
                .collect(Collectors.toList());
    }
}
