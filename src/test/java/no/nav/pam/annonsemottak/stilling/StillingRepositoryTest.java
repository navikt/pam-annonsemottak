package no.nav.pam.annonsemottak.stilling;

import jakarta.inject.Inject;
import no.nav.pam.annonsemottak.Application;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.StreamSupport.stream;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Rollback
@Transactional
@ContextConfiguration(classes = Application.class)
public class StillingRepositoryTest {

    @Inject
    private StillingRepository stillingRepository;

    @Test
    public void should_create_database_stilling() {
        stillingRepository.save(StillingTestdataBuilder.enkelStilling().tittel("Første stilling").externalId("ID1").build());
        stillingRepository.save(StillingTestdataBuilder.enkelStilling().tittel("Andre stilling").externalId("ID2").build());
        Iterable<Stilling> all = stillingRepository.findAll(PageRequest.of(0, 1));
        assertThat(all, contains(Matchers.hasProperty("id")));
    }

    @Test
    public void should_identify_duplicate() {
        Stilling one = stillingRepository.save(StillingTestdataBuilder.enkelStilling().tittel("Første stilling").externalId("ID1").build());
        Stilling two = stillingRepository.save(StillingTestdataBuilder.enkelStilling().tittel("Andre stilling").externalId("ID2").build());
        Boolean oneExists = stillingRepository.hashExists(one.getHash());
        Boolean twoExists = stillingRepository.hashExists(two.getHash());
        assertTrue(oneExists && twoExists);
        Stilling three = StillingTestdataBuilder.enkelStilling().tittel("Tredje stilling").build();
        Boolean threeExists = stillingRepository.hashExists(three.getHash());
        assertFalse(threeExists);
    }

    @Test
    public void should_identify_duplicates_in_list() {
        List<Stilling> stillinger = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            stillinger.add(StillingTestdataBuilder.enkelStilling().externalId("ID" + i).build());
        }

        List<Stilling> filtrerteStillinger = filter(stillinger);
        stillingRepository.saveAll(filtrerteStillinger);
        assertThat(filtrerteStillinger.size(), is(equalTo(stillinger.size())));

        List<Stilling> filter = filter(stillinger);
        assertThat(filter, is(empty()));
    }

    private List<Stilling> filter(List<Stilling> stillinger) {
        return stillinger
                .stream()
                .filter(s -> !stillingRepository.hashExists(s.getHash()))
                .collect(Collectors.toList());
    }

    @Test
    public void findAllByOrderByCreatedAsc_skal_sortere_på_mottattdato_som_default_og_vise_eldste_først() {
        stillingRepository.save(StillingTestdataBuilder.enkelStilling().tittel("Første stilling").externalId("ID1").build());
        stillingRepository.save(StillingTestdataBuilder.enkelStilling().tittel("Adnre stilling").externalId("ID2").build());

        Iterable<Stilling> alleStillinger = stillingRepository.findAll(Sort.by(Sort.Direction.ASC, "created"));

        assertThat(stream(alleStillinger.spliterator(), false).count(), is(equalTo(2L)));
        assertThat(stream(alleStillinger.spliterator(), false).findFirst().get().getTitle(), is(equalTo("Første stilling")));
    }


    @Test
    public void kilde_og_medium_skal_lagres() {
        stillingRepository.save(StillingTestdataBuilder.enkelStilling().kilde("KILDE").medium("MEDIUM").build());
        Iterable<Stilling> alleStillinger = stillingRepository.findAll();
        Stilling stilling = stream(alleStillinger.spliterator(), false).findFirst().get();
        assertThat(stilling.getKilde(), is(equalTo("KILDE")));
        assertThat(stilling.getMedium(), is(equalTo("MEDIUM")));
    }

    @Test
    public void annonsestatus_should_be_saved_and_fetch() {
        String kilde = "KILDE";
        String medium = "MEDIUM";
        List<Stilling> stillinger = stillingRepository.findByKildeAndMediumAndAnnonseStatus(kilde, medium, AnnonseStatus.AKTIV);
        assertThat(stillinger.size(), is(equalTo(0)));
        Stilling s = StillingTestdataBuilder.enkelStilling().kilde(kilde).medium(medium).externalId("ID1").build();
        Stilling s2 = StillingTestdataBuilder.enkelStilling().kilde(kilde).medium(medium).externalId("ID2").build();
        s2.stop();
        stillingRepository.save(s);
        stillingRepository.save(s2);
        stillinger = stillingRepository.findByKildeAndMediumAndAnnonseStatus(kilde, medium, AnnonseStatus.AKTIV);
        assertThat(stillinger.size(), is(equalTo(1)));
    }

}
