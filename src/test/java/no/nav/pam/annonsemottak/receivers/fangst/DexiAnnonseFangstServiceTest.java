package no.nav.pam.annonsemottak.receivers.fangst;


import no.nav.pam.annonsemottak.Application;
import no.nav.pam.annonsemottak.stilling.AnnonseStatus;
import no.nav.pam.annonsemottak.stilling.Stilling;
import no.nav.pam.annonsemottak.stilling.StillingRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static no.nav.pam.annonsemottak.stilling.StillingTestdataBuilder.enkelStilling;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Rollback
@Transactional
@ContextConfiguration(classes = Application.class)
public class DexiAnnonseFangstServiceTest {

    @Inject
    private StillingRepository stillingRepository;


    @Test
    public void stop_modify_new_annonseListss_should_be_correct() {
        DexiAnnonseFangstService fangstService = new DexiAnnonseFangstService(stillingRepository);
        String kilde = "KILDE";
        String medium = "MEDIUM";
        // we have 3 annonse in database
        Stilling s1 = enkelStilling().kilde(kilde).medium(medium).externalId("1").arbeidsgiver("nav.no").tittel("utvikler").build();
        Stilling s2 = enkelStilling().kilde(kilde).medium(medium).externalId("2").arbeidsgiver("nav.no").tittel("frontend").saksbehandler("Tuan").build();
        Stilling s3 = enkelStilling().kilde(kilde).medium(medium).externalId("3").arbeidsgiver("nav.no").tittel("systemutvikler").build();
        stillingRepository.save(s1);
        stillingRepository.save(s2);
        stillingRepository.save(s3);

        // we receive a new list
        Stilling s4 = enkelStilling().kilde(kilde).medium(medium).externalId("1").arbeidsgiver("nav.no").tittel("utvikler").build();
        Stilling s5 = enkelStilling().kilde(kilde).medium(medium).externalId("2").arbeidsgiver("nav.no").tittel("frontendutvikler").build();
        Stilling s6 = enkelStilling().kilde(kilde).medium(medium).externalId("4").arbeidsgiver("nav.no").tittel("nystilling").build();
        Stilling s7 = enkelStilling().kilde(kilde).medium(medium).externalId("5").arbeidsgiver("nav.no").tittel("nystilling2").build();
        List<Stilling> receivedList = new ArrayList<>();
        receivedList.add(s4);
        receivedList.add(s5);
        receivedList.add(s6);
        receivedList.add(s7);
        AnnonseResult annonseResult = fangstService.retrieveAnnonseLists(receivedList, kilde, medium);
        assertThat("new list should be correct", annonseResult.getNewList().size(), equalTo(2));
        assertThat("modify list should be correct", annonseResult.getModifyList().size(), equalTo(1));
        assertThat("stop list should be correct", annonseResult.getStopList().size(), equalTo(1));
        assertThat("stop ad should be flagged", annonseResult.getStopList().get(0).getAnnonseStatus(), equalTo(AnnonseStatus.STOPPET));
        // check that we can save back the lists
        stillingRepository.saveAll(annonseResult.getNewList());
        stillingRepository.saveAll(annonseResult.getStopList());
        stillingRepository.saveAll(annonseResult.getModifyList());
        assertThat("Modify add should be correct", annonseResult.getModifyList().get(0).getUuid(), equalTo(s2.getUuid()));
        assertThat("Modififed tittle is correct", annonseResult.getModifyList().get(0).getTitle(),
                equalTo(s2.getTitle()));
        assertThat("Saksbehandler for modified ad should not changed", annonseResult.getModifyList().get(0).getSaksbehandler().get().asString(), equalTo("Tuan"));
        // simulate ad getting reactivated
        receivedList.add(s3);
        annonseResult = fangstService.retrieveAnnonseLists(receivedList, kilde, medium);
        assertThat("one ad should be reactivated", annonseResult.getModifyList().size(), equalTo(1));
        Stilling noExternal =  enkelStilling().kilde(kilde).externalId(null).medium(medium).arbeidsgiver("nav.no").tittel("No External").build();
        receivedList.add(noExternal);
        annonseResult = fangstService.retrieveAnnonseLists(receivedList, kilde, medium);
        assertThat("Falling back to old method, should get one new ad", annonseResult.getNewList().size(), equalTo(1));

    }
}
