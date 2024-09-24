package no.nav.pam.annonsemottak.stilling;

import no.nav.pam.annonsemottak.receivers.Kilde;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static no.nav.pam.annonsemottak.stilling.StillingTestdataBuilder.enkelStilling;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;

public class StillingTest {

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
    public void expiryDateIsAdjustedCorrectly() {
        String format = "dd.MM.yyyy";

        Stilling stilling = enkelStilling().utløpsdato("12.12.2518").build();
        assertThat(stilling.getExpires().format(DateTimeFormatter.ofPattern(format)),
                is(equalTo(LocalDateTime.now().plusMonths(6).format(DateTimeFormatter.ofPattern(format)))));
    }

    @Test
    public void at_uendrede_stillinger_er_like() {
        Stilling oppdatering = enkelStilling().utløpsdato("12.12.2018").kilde(Kilde.XML_STILLING.value()).build();
        Stilling eksisterende = enkelStilling().utløpsdato("12.12.2018").kilde(Kilde.XML_STILLING.value()).build();

        assertThat(oppdatering.getHash(), is(equalTo(eksisterende.getHash())));
    }

    @Test
    public void at_stillinger_med_endret_expires_er_ulike_eksisterende() {
        LocalDateTime expires = LocalDateTime.now();

        Stilling oppdatering = new StillingBuilder().title("tittel").kilde(Kilde.XML_STILLING.value())
                .expires(expires.minusDays(1)).build();

        Stilling eksisterende = new StillingBuilder().title("tittel").kilde(Kilde.XML_STILLING.value())
                .expires(expires).build();

        assertThat(oppdatering.getHash(), is(not(equalTo(eksisterende.getHash()))));
    }
}
