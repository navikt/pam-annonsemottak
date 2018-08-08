package no.nav.pam.annonsemottak.stilling;

import org.junit.Assert;
import org.junit.Test;

import static no.nav.pam.annonsemottak.stilling.StillingTestdataBuilder.enkelStilling;

public class AnnonseStatusTest {

    @Test
    public void stop() {
        Stilling nyStilling = enkelStilling().build();
        Assert.assertEquals(nyStilling.getAnnonseStatus(), AnnonseStatus.AKTIV);
        nyStilling.stop();
        Assert.assertEquals(nyStilling.getAnnonseStatus(), AnnonseStatus.STOPPET);
    }

    @Test
    public void deactivate() {
        Stilling nyStilling = enkelStilling().build();
        nyStilling.deactivate();
        Assert.assertEquals(nyStilling.getAnnonseStatus(), AnnonseStatus.INAKTIV);
    }
}
