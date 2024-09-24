package no.nav.pam.annonsemottak.rest.payloads;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static no.nav.pam.annonsemottak.stilling.StillingTestdataBuilder.enkelStilling;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsMapContaining.hasEntry;

public class StillingPayloadTest {

    @Test
    public void skal_mappe_medium() {
        Map<String, String> stilling = StillingPayload.fromStilling(enkelStilling().medium("Bergen kommune").build());
        assertThat(stilling, hasEntry("medium", "Bergen kommune"));
    }

}
