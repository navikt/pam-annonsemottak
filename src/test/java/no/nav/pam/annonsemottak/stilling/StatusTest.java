package no.nav.pam.annonsemottak.stilling;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class StatusTest {

    @Test
    public void statuskode_0_skal_mappes_til_MOTTATT() {
        assertThat(Status.valueOfStatuskode("0"), is(equalTo(Status.MOTTATT)));
    }
    @Test
    public void statuskode_1_skal_mappes_til_UNDER_ARBEID() {
        assertThat(Status.valueOfStatuskode("1"), is(equalTo(Status.UNDER_ARBEID)));
    }

    @Test
    public void statuskode_2_skal_mappes_til_GODKJENT() {
        assertThat(Status.valueOfStatuskode("2"), is(equalTo(Status.GODKJENT)));
    }

    @Test
    public void statuskode_3_skal_mappes_til_AVVIST() {
        assertThat(Status.valueOfStatuskode("3"), is(equalTo(Status.AVVIST)));
    }

    @Test
    public void statuskode_4_skal_mappes_til_FJERNET() {
        assertThat(Status.valueOfStatuskode("4"), is(equalTo(Status.FJERNET)));
    }

    @Test
    public void ukjent_statuskode_skal_gi_exception() {
        String ukjentStatus = Integer.toString(Status.values().length + 1);
        Assertions.assertThrows(IllegalArgumentException.class, () -> Status.valueOfStatuskode(ukjentStatus));
    }

}
