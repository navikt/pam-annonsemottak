package no.nav.pam.annonsemottak.rest;

import no.nav.pam.annonsemottak.rest.payloads.AnnonsehodePayload;
import no.nav.pam.annonsemottak.stilling.Merknader;
import no.nav.pam.annonsemottak.stilling.Stilling;
import org.junit.jupiter.api.Test;

import static no.nav.pam.annonsemottak.stilling.StillingTestdataBuilder.enkelStilling;
import static org.junit.Assert.assertEquals;

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
        assertEquals(stilling.getCreated().toString(), payload.getMottattDato());
        assertEquals(stilling.getStatus().getKodeAsString(), payload.getStatus());
        assertEquals(stilling.getTitle(), payload.getTittel());
    }

}
