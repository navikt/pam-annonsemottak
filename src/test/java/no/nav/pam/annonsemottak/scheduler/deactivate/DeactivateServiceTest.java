package no.nav.pam.annonsemottak.scheduler.deactivate;


import no.nav.pam.annonsemottak.outbox.StillingOutboxService;
import no.nav.pam.annonsemottak.stilling.AnnonseStatus;
import no.nav.pam.annonsemottak.stilling.Stilling;
import no.nav.pam.annonsemottak.stilling.StillingRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static no.nav.pam.annonsemottak.stilling.StillingTestdataBuilder.enkelStilling;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DeactivateServiceTest {

    @Test
    public void shouldDeactivateExpired(){
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern( "yyyy.M.dd");

        // Mocked repository data
        List<Stilling> ads = new ArrayList<>();
        ads.add(enkelStilling().utløpsdato(LocalDateTime.now().plusMonths(1).format(fmt)).build());
        ads.add(enkelStilling().utløpsdato(LocalDateTime.now().minusDays(2).format(fmt)).build());
        ads.add(enkelStilling().utløpsdato(null).build());
        //TODO: add this after 10 day replacement for expiry date is removed
//        Stilling nullExpiry = enkelStilling().utløpsdato(null).build();
//        nullExpiry.setCreated(DateTime.now().minusDays(30));
//        ads.add(nullExpiry);

        StillingRepository mockedStillingRepository = mock(StillingRepository.class);
        StillingOutboxService mockedStillingOutboxService = mock(StillingOutboxService.class);
        when(mockedStillingRepository.findByAnnonseStatus(AnnonseStatus.AKTIV)).thenReturn(ads);

        DeactivateService service = new DeactivateService(mockedStillingRepository, mockedStillingOutboxService);

        service.deactivateExpired();

        List<Stilling> expired = ads.stream().filter(s -> s.getAnnonseStatus().equals(AnnonseStatus.INAKTIV)).collect(Collectors.toList());
        Assertions.assertEquals(1, expired.size());
    }
}
