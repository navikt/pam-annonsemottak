package no.nav.pam.annonsemottak.stilling;

import no.nav.pam.annonsemottak.receivers.GenericDateParser;
import no.nav.pam.annonsemottak.receivers.Kilde;
import org.junit.Test;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static no.nav.pam.annonsemottak.stilling.OppdaterSaksbehandlingCommand.*;
import static no.nav.pam.annonsemottak.stilling.StillingTestdataBuilder.enkelStilling;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;

public class StillingTest {

    @Test
    public void saksbehandler_skal_ikke_settes_for_nye_stillinger() {
        Stilling nyStilling = enkelStilling().build();
        assertThat(nyStilling.getSaksbehandler(), is(Optional.empty()));
    }

    @Test
    public void en_unik_hash_skal_genereres_for_nye_stillinger() {
        Stilling nyStilling = enkelStilling().build();
        assertThat(nyStilling.getHash(), is(notNullValue()));
    }

    @Test
    public void en_unik_uuid_skal_genereres_for_nye_stillinger() {
        Stilling nyStilling = enkelStilling().build();
        assertThat(nyStilling.getUuid(), is(notNullValue()));
    }

    @Test
    public void nye_stillinger_skal_ha_status_mottatt() {
        Stilling nyStilling = enkelStilling().build();
        assertThat(nyStilling.getStatus(), is(equalTo(Status.MOTTATT)));
    }

    @Test
    public void skal_kunne_oppdatere_stilling_med_status_saksbehandler_og_kommentarer()
            throws Exception {
        Stilling nyStilling = enkelStilling().build();
        Map<String, String> map = new HashMap<>();
        map.put(KOMMENTARER, "En liten kommentar");
        map.put(SAKSBEHANDLER, "Truls");
        map.put(STATUS, Status.UNDER_ARBEID.getKodeAsString());
        nyStilling.oppdaterMed(new OppdaterSaksbehandlingCommand(map));

        assertThat(nyStilling.getKommentarer(), is(equalTo(Kommentarer.ofNullable("En liten kommentar"))));
        assertThat(nyStilling.getSaksbehandler(), is(equalTo(Saksbehandler.ofNullable("Truls"))));
        assertThat(nyStilling.getStatus(), is(equalTo(Status.UNDER_ARBEID)));
        assertThat(nyStilling.getMerknader(), is(equalTo(Merknader.empty())));
    }

    @Test
    public void saksbehandler_må_være_angitt_for_å_sette_status_UNDER_ARBEID()
            throws Exception {
        Stilling nyStilling = enkelStilling().build();
        Map<String, String> map = new HashMap<>();
        map.put(STATUS, Status.UNDER_ARBEID.getKodeAsString());
        map.put(SAKSBEHANDLER, "Truls");
        nyStilling.oppdaterMed(new OppdaterSaksbehandlingCommand(map));

        assertThat(nyStilling.getStatus(), is(equalTo(Status.UNDER_ARBEID)));
        assertThat(nyStilling.getSaksbehandler(), is(equalTo(Saksbehandler.ofNullable("Truls"))));
    }

    @Test
    public void status_kan_settes_tilbake_til_MOTTATT_samtidig_som_saksbehandler_fjernes()
            throws Exception {
        Stilling stillingUnderArbeid = enkelStilling().status(Status.UNDER_ARBEID).saksbehandler("Truls").build();
        Map<String, String> map = new HashMap<>();
        map.put(STATUS, Status.MOTTATT.getKodeAsString());
        map.put(SAKSBEHANDLER, null);
        stillingUnderArbeid.oppdaterMed(new OppdaterSaksbehandlingCommand(map));

        assertThat(stillingUnderArbeid.getStatus(), is(equalTo(Status.MOTTATT)));
        assertThat(stillingUnderArbeid.getSaksbehandler(), is(equalTo(Optional.empty())));
    }

    @Test(expected = IllegalSaksbehandlingCommandException.class)
    public void det_skal_ikke_være_mulig__å_sette_status_UNDER_ARBEID_uten_at_saksbehandler_er_angitt()
            throws Exception {
        Stilling nyStilling = enkelStilling().build();
        Map<String, String> map = new HashMap<>();
        map.put(STATUS, Status.UNDER_ARBEID.getKodeAsString());
        nyStilling.oppdaterMed(new OppdaterSaksbehandlingCommand(map));
    }

    @Test(expected = IllegalSaksbehandlingCommandException.class)
    public void det_skal_ikke_være_mulig_å_godkjenne_en_annonse_med_merknad()
            throws Exception {
        Stilling nyStilling = enkelStilling().build();
        Map<String, String> map = new HashMap<>();
        map.put(STATUS, Status.GODKJENT.getKodeAsString());
        map.put(MERKNADER, "1");
        nyStilling.oppdaterMed(new OppdaterSaksbehandlingCommand(map));
    }

    @Test
    public void ingen_merknad_kan_være_angitt_for_å_sette_status_GODKJENT()
            throws Exception {
        Stilling nyStilling = enkelStilling().build();
        Map<String, String> map = new HashMap<>();
        map.put(STATUS, Status.GODKJENT.getKodeAsString());
        nyStilling.oppdaterMed(new OppdaterSaksbehandlingCommand(map));

        assertThat(nyStilling.getStatus(), is(equalTo(Status.GODKJENT)));
        assertThat(nyStilling.getMerknader(), is(equalTo(Optional.empty())));
    }

    @Test
    public void blank_merknad_kan_være_angitt_for_å_sette_status_GODKJENT()
            throws Exception {
        Stilling nyStilling = enkelStilling().build();
        Map<String, String> map = new HashMap<>();
        map.put(STATUS, Status.GODKJENT.getKodeAsString());
        map.put(MERKNADER, "");
        nyStilling.oppdaterMed(new OppdaterSaksbehandlingCommand(map));

        assertThat(nyStilling.getStatus(), is(equalTo(Status.GODKJENT)));
        assertThat(nyStilling.getMerknader(), is(equalTo(Optional.empty())));
    }

    @Test
    public void minst_en_merknad_må_være_angitt_for_å_avvise_en_annonse()
            throws Exception {
        Stilling nyStilling = enkelStilling().build();
        Map<String, String> map = new HashMap<>();
        map.put(MERKNADER, "1");
        map.put(STATUS, Status.AVVIST.getKodeAsString());
        nyStilling.oppdaterMed(new OppdaterSaksbehandlingCommand(map));

        assertThat(nyStilling.getStatus(), is(equalTo(Status.AVVIST)));
        assertThat(nyStilling.getMerknader(), is(equalTo(Merknader.ofNullable("1"))));
    }

    @Test(expected = IllegalSaksbehandlingCommandException.class)
    public void det_skal_ikke_være_mulig_å_avvise_en_annonse_uten_merknad()
            throws Exception {
        Stilling nyStilling = enkelStilling().build();
        Map<String, String> map = new HashMap<>();
        map.put(STATUS, Status.AVVIST.getKodeAsString());
        nyStilling.oppdaterMed(new OppdaterSaksbehandlingCommand(map));
    }

    @Test
    public void canChangeSaksbehandlerIfSaksbehandlerIsNotAlreadySet()
            throws Exception {
        Stilling stilling = enkelStilling().build();
        Map<String, String> changes = new HashMap<>(1);
        changes.put(SAKSBEHANDLER, "New Saksbehandler");
        stilling.oppdaterMed(new OppdaterSaksbehandlingCommand(changes));
        assertThat(stilling.getSaksbehandler().map(Saksbehandler::asString).orElse(""), is(equalTo("New Saksbehandler")));
    }

    @Test
    public void canRemoveSaksbehandlerIfSaksbehandlerIsAlreadySet()
            throws Exception {
        Stilling stilling = enkelStilling()
                .saksbehandler("Old Saksbehandler")
                .build();
        Map<String, String> changes = new HashMap<>(1);
        changes.put(SAKSBEHANDLER, null);
        stilling.oppdaterMed(new OppdaterSaksbehandlingCommand(changes));
        assertThat(stilling.getSaksbehandler().isPresent(), is(false));
    }

    @Test(expected = IllegalSaksbehandlingCommandException.class)
    public void cannotChangeSaksbehandlerIfSaksbehandlerIsAlreadySetToAnotherSaksbehandler()
            throws Exception {
        Stilling stilling = enkelStilling()
                .saksbehandler("Old Saksbehandler")
                .build();
        Map<String, String> changes = new HashMap<>(1);
        changes.put(SAKSBEHANDLER, "New Saksbehandler");
        stilling.oppdaterMed(new OppdaterSaksbehandlingCommand(changes));
    }

    @Test
    public void canRemoveSaksbehandlerIfSaksbehandlerIsNotAlreadySet()
            throws Exception {
        Stilling stilling = enkelStilling().build();
        Map<String, String> changes = new HashMap<>(1);
        changes.put(SAKSBEHANDLER, null);
        stilling.oppdaterMed(new OppdaterSaksbehandlingCommand(changes));
        assertThat(stilling.getSaksbehandler().isPresent(), is(false));
    }

    @Test
    public void canChangeSaksbehandlerIfSaksbehandlerIsAlreadySetToTheSameSaksbehandler()
            throws Exception {
        Stilling stilling = enkelStilling()
                .saksbehandler("Same Saksbehandler")
                .build();
        Map<String, String> changes = new HashMap<>(1);
        changes.put(SAKSBEHANDLER, "Same Saksbehandler");
        stilling.oppdaterMed(new OppdaterSaksbehandlingCommand(changes));
        assertThat(stilling.getSaksbehandler().map(Saksbehandler::asString).orElse(""), is(equalTo("Same Saksbehandler")));
    }

    @Test
    public void expiryDateIsAdjustedCorrectly() {
        String format = "dd.MM.yyyy";

        Stilling stilling = enkelStilling().utløpsdato("12.12.2518").build();
        assertThat(stilling.getExpires().format(DateTimeFormatter.ofPattern(format)),
                is(equalTo(LocalDateTime.now().plusDays(10).format(DateTimeFormatter.ofPattern(format)))));
    }

    @Test
    public void at_stopping_kan_skje_automatisk() {

        Stilling oppdatering = enkelStilling().utløpsdato("11.12.2018").kilde(Kilde.XML_STILLING.value()).build();
        Stilling eksisterende = enkelStilling().utløpsdato("12.12.2018").kilde(Kilde.XML_STILLING.value()).build();

        oppdatering.stopIfExpired(eksisterende);
        assertThat(oppdatering.getAnnonseStatus(), is(equalTo(AnnonseStatus.STOPPET)));
    }

    @Test
    public void at_inaktive_annonser_ikke_stoppes_automatisk() {

        Stilling oppdatering = enkelStilling().utløpsdato("11.12.2018").kilde(Kilde.XML_STILLING.value()).build();
        Stilling eksisterende = enkelStilling().utløpsdato("12.12.2018").kilde(Kilde.XML_STILLING.value()).build()
                .deactivate();

        oppdatering.stopIfExpired(eksisterende);
        assertThat(oppdatering.getAnnonseStatus(), is(not(equalTo(AnnonseStatus.STOPPET))));
    }

    @Test
    public void at_med_fremtidig_stopdato_ikke_stoppes_automatisk() {

        Stilling oppdatering = enkelStilling().utløpsdato("13.12.2018").kilde(Kilde.XML_STILLING.value()).build();
        Stilling eksisterende = enkelStilling().utløpsdato("12.12.2018").kilde(Kilde.XML_STILLING.value()).build()
                .deactivate();

        oppdatering.stopIfExpired(eksisterende);
        assertThat(oppdatering.getAnnonseStatus(), is(not(equalTo(AnnonseStatus.STOPPET))));
    }

    @Test
    public void at_ikke_xml_stilling_annonser_ikke_stoppes_automatisk() {

        Stilling oppdatering = enkelStilling().utløpsdato("11.12.2018").kilde(Kilde.AMEDIA.value()).build();
        Stilling eksisterende = enkelStilling().utløpsdato("12.12.2018").kilde(Kilde.AMEDIA.value()).build();

        oppdatering.stopIfExpired(eksisterende);
        assertThat(oppdatering.getAnnonseStatus(), is(not(equalTo(AnnonseStatus.STOPPET))));
    }
}
