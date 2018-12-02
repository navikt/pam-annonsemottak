package no.nav.pam.annonsemottak.rest.payloads;

import org.junit.Test;

import java.util.Map;

import static no.nav.pam.annonsemottak.stilling.StillingTestdataBuilder.enkelStilling;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsMapContaining.hasEntry;

public class StillingPayloadTest {

    @Test
    public void skal_mappe_manglene_saksbehandler_til_null() {
        Map<String, String> stilling = StillingPayload.fromStilling(enkelStilling().build());
        assertThat(stilling, hasEntry("saksbehandler", null));
    }

    @Test
    public void skal_mappe_saksbehandler() {
        Map<String, String> stilling = StillingPayload.fromStilling(enkelStilling().saksbehandler("Navn på saksbehandler").build());
        assertThat(stilling, hasEntry("saksbehandler", "Navn på saksbehandler"));
    }

    @Test
    public void skal_mappe_medium() {
        Map<String, String> stilling = StillingPayload.fromStilling(enkelStilling().medium("Bergen kommune").build());
        assertThat(stilling, hasEntry("medium", "Bergen kommune"));
    }

}
