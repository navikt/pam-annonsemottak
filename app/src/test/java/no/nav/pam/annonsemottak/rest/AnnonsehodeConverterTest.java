package no.nav.pam.annonsemottak.rest;

import no.nav.pam.annonsemottak.stilling.Merknader;
import no.nav.pam.annonsemottak.stilling.OppdaterSaksbehandlingCommand;
import no.nav.pam.annonsemottak.stilling.Stilling;
import no.nav.pam.annonsemottak.rest.payloads.AnnonsehodePayload;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static no.nav.pam.annonsemottak.stilling.StillingTestdataBuilder.enkelStilling;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class AnnonsehodeConverterTest {

    @Test
    public void convert() {
        Stilling stilling = enkelStilling()
                .merknader(new Merknader("TEST"))
                .saksbehandler("Saksbehandler")
                .build();
        AnnonsehodePayload payload = new AnnonsehodeConverter().apply(stilling);
        assertEquals(stilling.getArbeidsgiver().get().asString(), payload.getArbeidsgiver());
        assertEquals(stilling.getPlace(), payload.getArbeidssted());
        assertEquals(stilling.getMerknader().get().asString(), payload.getMerknader());
        assertEquals(stilling.getCreated().toString(), payload.getMottattDato());
        assertEquals(stilling.getSaksbehandler().get().asString(), payload.getSaksbehandler());
        assertEquals(stilling.getStatus().getKodeAsString(), payload.getStatus());
        assertEquals(stilling.getTitle(), payload.getTittel());
    }

    @Test
    public void convertNullSafe()
            throws Exception {
        Stilling stilling = enkelStilling().build();
        Map<String, String> map = new HashMap<>();
        map.put("saksbehandler", null);
        stilling.oppdaterMed(new OppdaterSaksbehandlingCommand(map));
        stilling.setCreated(null);
        try {
            new AnnonsehodeConverter().apply(stilling);
        } catch (NullPointerException e) {
            fail();
        }
    }

}
