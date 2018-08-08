package no.nav.pam.annonsemottak.annonsemottak.scheduler.deactivate;


import no.nav.pam.annonsemottak.stilling.AnnonseStatus;
import no.nav.pam.annonsemottak.stilling.Stilling;
import no.nav.pam.annonsemottak.stilling.StillingRepository;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static no.nav.pam.annonsemottak.stilling.StillingTestdataBuilder.enkelStilling;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DeactivateServiceTest {

    private DeactivateService service;

    @Test
    public void shouldDeactivateExpired(){
        DateTimeFormatter fmt = DateTimeFormat.forPattern( "yyyy.M.dd");

        // Mocked repository data
        List<Stilling> ads = new ArrayList<>();
        ads.add(enkelStilling().utløpsdato(DateTime.now().plusMonths(1).toString(fmt)).build());
        ads.add(enkelStilling().utløpsdato(DateTime.now().minusDays(2).toString(fmt)).build());
        ads.add(enkelStilling().utløpsdato(null).build());
        //TODO: add this after 10 day replacement for expiry date is removed
//        Stilling nullExpiry = enkelStilling().utløpsdato(null).build();
//        nullExpiry.setCreated(DateTime.now().minusDays(30));
//        ads.add(nullExpiry);

        StillingRepository mockedStillingRepository = mock(StillingRepository.class);
        when(mockedStillingRepository.findByAnnonseStatus(AnnonseStatus.AKTIV)).thenReturn(ads);

        service = new DeactivateService(mockedStillingRepository);

        service.deactivateExpired();

        List<Stilling> expired = ads.stream().filter(s -> s.getAnnonseStatus().equals(AnnonseStatus.INAKTIV)).collect(Collectors.toList());
        Assert.assertEquals(1, expired.size());
    }
}
