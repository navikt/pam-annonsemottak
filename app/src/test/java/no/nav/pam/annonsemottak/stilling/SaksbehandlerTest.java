package no.nav.pam.annonsemottak.stilling;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class SaksbehandlerTest {

    @Test
    public void skal_opprette_en_gyldig_saksbehandler() {
        Saksbehandler saksbehandlerTruls = new Saksbehandler("Truls");
        assertThat(saksbehandlerTruls, is(equalTo(new Saksbehandler("Truls"))));
    }
}
