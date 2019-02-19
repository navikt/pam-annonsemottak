package no.nav.pam.annonsemottak.receivers.fangst;

import io.micrometer.core.instrument.MeterRegistry;
import no.nav.pam.annonsemottak.Application;
import no.nav.pam.annonsemottak.receivers.Kilde;
import no.nav.pam.annonsemottak.receivers.Medium;
import no.nav.pam.annonsemottak.stilling.AnnonseStatus;
import no.nav.pam.annonsemottak.stilling.Stilling;
import no.nav.pam.annonsemottak.stilling.StillingRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertTrue;
import static no.nav.pam.annonsemottak.stilling.StillingTestdataBuilder.enkelStilling;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

@RunWith(SpringRunner.class)
@DataJpaTest
@Rollback
@Transactional
@ContextConfiguration(classes = Application.class)
public class AnnonseFangstServiceTest {

    @Inject
    private StillingRepository stillingRepository;

    @Mock
    private MeterRegistry meterRegistry;

    @Test
    public void should_add_new_and_changed_ads(){
        DuplicateHandler duplicateHandler = mock(DuplicateHandler.class);
        AnnonseFangstService fangstService = new AnnonseFangstService(stillingRepository, duplicateHandler, meterRegistry);
        String kilde = Kilde.FINN.toString();
        String medium = Medium.FINN.toString();

        Stilling s1 = enkelStilling().kilde(kilde).medium(medium).externalId("1").arbeidsgiver("nav.no").tittel("utvikler").build();
        Stilling s2 = enkelStilling().kilde(kilde).medium(medium).externalId("2").arbeidsgiver("nav.no").tittel("frontend").saksbehandler("Tuan").build();
        Stilling s3 = enkelStilling().kilde(kilde).medium(medium).externalId("3").arbeidsgiver("nav.no").tittel("systemutvikler").build();
        Stilling s4 = enkelStilling().kilde(kilde).medium(medium).externalId("4").arbeidsgiver("nav.no").tittel("tester")
                .utlopsdato(LocalDateTime.now().minusDays(2).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).build();

        stillingRepository.save(s1);
        stillingRepository.save(s2);
        stillingRepository.save(s3);
        stillingRepository.save(s4);

        // we receive a new list
        Stilling s5 = enkelStilling().kilde(kilde).medium(medium).externalId("2").arbeidsgiver("nav.no").tittel("frontendutvikler").build();
        Stilling s6 = enkelStilling().kilde(kilde).medium(medium).externalId("5").arbeidsgiver("nav.no").tittel("nystilling").build();
        Stilling s7 = enkelStilling().kilde(kilde).medium(medium).externalId("6").arbeidsgiver("nav.no").tittel("nystilling2").build();
        List<Stilling> receivedList = new ArrayList<>();
        receivedList.add(s5);
        receivedList.add(s6);
        receivedList.add(s7);

        //List of all FinnAdHeads. Should include external IDs for all active ads in Finn.
        // 1 - unchanged, 2 - modified, 3 - deleted in source, 4 - expired, 5,6 -new
        String[] externalIds = {"1", "2", "5", "6"};
        Set<String> allExternalIdSet = new HashSet<>(Arrays.asList(externalIds));
        AnnonseResult annonseResult = fangstService.retrieveAnnonseLists(receivedList, allExternalIdSet, kilde, medium);

        assertThat("new list should be correct", annonseResult.getNewList().size(), equalTo(2));
        List<String> newExternalIds = annonseResult.getNewList().stream().map(Stilling::getExternalId).collect(Collectors.toList());
        assertTrue(newExternalIds.contains("5"));
        assertTrue(newExternalIds.contains("6"));

        assertThat("modify list should be correct", annonseResult.getModifyList().size(), equalTo(1));
        assertThat("modified ad externalId is correct", annonseResult.getModifyList().get(0).getExternalId(), equalTo("2"));

        assertThat("stop list should be correct", annonseResult.getStopList().size(), equalTo(1));
        assertThat("stopped ad should be flagged", annonseResult.getStopList().get(0).getAnnonseStatus(), equalTo(AnnonseStatus.STOPPET));
        assertThat("stopped ad externalId is correct", annonseResult.getStopList().get(0).getExternalId(), equalTo("3"));

        assertThat("expired list should be correct", annonseResult.getExpiredList().size(), equalTo(1));
        assertThat("expired ad should be flagged", annonseResult.getExpiredList().get(0).getAnnonseStatus(), equalTo(AnnonseStatus.INAKTIV));
        assertThat("expired ad externalId is correct", annonseResult.getExpiredList().get(0).getExternalId(), equalTo("4"));
    }
}
