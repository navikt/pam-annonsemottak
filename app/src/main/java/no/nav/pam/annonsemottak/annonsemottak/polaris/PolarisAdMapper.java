package no.nav.pam.annonsemottak.annonsemottak.polaris;

import no.nav.pam.annonsemottak.annonsemottak.Kilde;
import no.nav.pam.annonsemottak.annonsemottak.Medium;
import no.nav.pam.annonsemottak.annonsemottak.polaris.model.PolarisAd;
import no.nav.pam.annonsemottak.stilling.Stilling;

public class PolarisAdMapper {

    public static Stilling mapToStilling(PolarisAd polarisAd) {

        Stilling stilling = new Stilling(
                polarisAd.title,
                polarisAd.location.city,
                polarisAd.companyName,
                polarisAd.companyInformation,
                polarisAd.text,
                (polarisAd.applicationDeadlineDate != null) ? polarisAd.applicationDeadlineDate.toString() : polarisAd.applicationDeadlineText,
                Kilde.POLARIS.value(),
                (polarisAd.bookings.publication != null) ? polarisAd.bookings.publication : Medium.POLARIS.value(),
                polarisAd.url,
                polarisAd.positionId,
                null
        );


        return stilling;
    }
}
