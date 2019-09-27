package no.nav.pam.annonsemottak.receivers.amedia;

import org.junit.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class AmediaUrlTest {

    private static final String URL_BASE = "http://test/api/prophecy/external/nav/jobs";
    private final AmediaUrl url = new AmediaUrl(URL_BASE);

    @Test
    public void kanOppretteEnParamterStrengMedAlleVerdier() {
        LocalDateTime sistModifisert = LocalDateTime.of(2017, 10, 20, 11, 0, 0);

        assertThat(url.modifiedAfter(sistModifisert))
                .isEqualTo(URL_BASE  + "?modified=2017-10-20T11:00:00Z");
    }

    @Test
    public void modifisertDatoErNull() {

        assertThat(url.modifiedAfter(null)).isEqualTo(
            URL_BASE + "?modified=2000-01-01T00:00:00Z");
    }


    @Test
    public void all() {
        assertThat(url.all()).isEqualTo(URL_BASE + "/all");
    }

    @Test
    public void ping() {
        assertThat(url.ping()).isEqualTo(URL_BASE);
    }
}