package no.nav.pam.annonsemottak.stilling;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static no.nav.pam.annonsemottak.stilling.StillingTestdataBuilder.enkelStilling;

public class AnnonseStatusTest {

    @Test
    public void stop() {
        Stilling nyStilling = enkelStilling().build();
        Assertions.assertEquals(nyStilling.getAnnonseStatus(), AnnonseStatus.AKTIV);
        nyStilling.stop();
        Assertions.assertEquals(nyStilling.getAnnonseStatus(), AnnonseStatus.STOPPET);
    }

    @Test
    public void deactivate() {
        Stilling nyStilling = enkelStilling().build();
        nyStilling.deactivate();
        Assertions.assertEquals(nyStilling.getAnnonseStatus(), AnnonseStatus.INAKTIV);
    }
}
