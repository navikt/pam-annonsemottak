package no.nav.pam.annonsemottak.receivers.xmlstilling;

import no.nav.pam.annonsemottak.stilling.Stilling;
import no.nav.pam.annonsemottak.stilling.StillingTestdataBuilder;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Arrays;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

public class StillingerTest {


    @Test
    public void find_latest_date() {
        LocalDateTime latestDate = LocalDateTime.of(2019, 1, 11, 14, 51, 11);

        Stillinger stillinger = new Stillinger(Arrays.asList(
                stillingMedSystemModifiedDate(latestDate.minusSeconds(1)),
                stillingMedSystemModifiedDate(latestDate),
                stillingMedSystemModifiedDate(latestDate.minusSeconds(2))));

        assertThat(stillinger.latestDate().get())
                .isEqualTo(LocalDateTime.of(2019, 1, 11, 14, 51, 11));
    }

    @Test
    public void find_latest_date_with_no_results() {
        Stillinger nullResult = new Stillinger(null);
        assertThat(nullResult.latestDate()).isEmpty();

        Stillinger emptyResult = new Stillinger(emptyList());
        assertThat(emptyResult.latestDate()).isEmpty();
    }

    private Stilling stillingMedSystemModifiedDate(LocalDateTime systemModifiedDate) {
        return StillingTestdataBuilder.enkelStilling().systemModifiedDate(systemModifiedDate).build();
    }

}
