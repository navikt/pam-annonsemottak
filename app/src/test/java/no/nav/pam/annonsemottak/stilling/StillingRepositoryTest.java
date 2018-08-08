package no.nav.pam.annonsemottak.stilling;

import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.StreamSupport.stream;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
@Rollback
@Transactional
public class StillingRepositoryTest {

    @Inject
    private StillingRepository stillingRepository;

    @Test
    public void should_create_database_stilling() throws Exception {
        stillingRepository.save(StillingTestdataBuilder.enkelStilling().tittel("Første stilling").externalId("ID1").build());
        stillingRepository.save(StillingTestdataBuilder.enkelStilling().tittel("Andre stilling").externalId("ID2").build());
        Iterable<Stilling> all = stillingRepository.findAll(PageRequest.of(0, 1));
        assertThat(all, contains(Matchers.hasProperty("id")));
    }

    @Test
    public void should_identify_duplicate() throws Exception {
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
    public void should_identify_duplicates_in_list() throws Exception {
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

        Iterable<Stilling> alleStillinger = stillingRepository.findAll(new Sort(Sort.Direction.ASC, "created"));

        assertThat(stream(alleStillinger.spliterator(), false).count(), is(equalTo(2L)));
        assertThat(stream(alleStillinger.spliterator(), false).findFirst().get().getStillingstittel(), is(equalTo("Første stilling")));
    }

    @Test
    public void stilling_skal_støtte_lagring_og_henting_med_saksbehandler_som_verditype() {
        stillingRepository.save(StillingTestdataBuilder.enkelStilling().tittel("Første stilling").saksbehandler("Truls").externalId("ID1").build());
        stillingRepository.save(StillingTestdataBuilder.enkelStilling().tittel("Andre stilling").externalId("ID2").build());

        Iterable<Stilling> alleStillinger = stillingRepository.findAll(new Sort(Sort.Direction.ASC, "created"));

        assertThat(stream(alleStillinger.spliterator(), false).count(), is(equalTo(2L)));
        Stilling hentetStilling = stream(alleStillinger.spliterator(), false).findFirst().get();
        assertThat(hentetStilling.getStillingstittel(), is(equalTo("Første stilling")));
        assertThat(hentetStilling.getSaksbehandling().getSaksbehandler(), is(equalTo(Saksbehandler.ofNullable("Truls"))));
    }

    @Test
    public void stilling_publisert_skal_bare_kunne_settes_en_gang() {
        final String externalID = java.util.UUID.randomUUID().toString();
        Stilling stilling = StillingTestdataBuilder.enkelStilling().externalId(externalID).build();
        stilling.setPublished(new DateTime());

        stillingRepository.save(stilling);
        Stilling lastetStilling = stillingRepository.findByKildeAndMediumAndExternalId(
                stilling.getKilde(), stilling.getMedium(), externalID);

        assertNotNull(lastetStilling);

        try {
            lastetStilling.setPublished(new DateTime());
            fail("Det skal ikke være lov å sette published for andre gang");
        } catch (IllegalArgumentException e) {
            // Dette er ok
        }
    }

    @Test
    public void publiseringsdato_skal_bevares_ved_oppdateringer() throws IllegalSaksbehandlingCommandException {
        final String externalID = java.util.UUID.randomUUID().toString();
        Stilling brandNew = StillingTestdataBuilder.enkelStilling().externalId(externalID).build();
        OppdaterSaksbehandlingCommand saksbehandlingCommand = new OppdaterSaksbehandlingCommand(Collections.singletonMap("status", "2"));
        brandNew.oppdaterMed(saksbehandlingCommand); // Sets published date to "now" and status to GODKJENT

        final DateTime published = brandNew.getPublished();
        assertNotNull(brandNew.getPublished());

        final String uuid = brandNew.getUuid();

        stillingRepository.save(brandNew);

        Stilling externallyUpdated = StillingTestdataBuilder.enkelStilling().externalId(externalID).tittel("Oppdatert tittel").build();

        Stilling stored = stillingRepository.findByUuid(uuid);
        assertNotNull(stored);
        assertNotNull(stored.getPublished());

        externallyUpdated.merge(stored);

        stored = stillingRepository.save(externallyUpdated);

        assertEquals(published, stored.getPublished());
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

//    @Test
//    public void annonse_dato_skal_være_riktige() {
//        Stilling s = enkelStilling().build();
//        s = stillingRepository.save(s);
//        Stilling load = stillingRepository.findByUuid(s.getUuid());
//        assertNotNull(load.getCreated());
//        assertNotNull(load.getExpires());
//        assertTrue(load.getExpires().isAfter(load.getCreated()));
//    }

}
