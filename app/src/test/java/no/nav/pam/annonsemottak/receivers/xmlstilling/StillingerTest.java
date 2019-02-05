package no.nav.pam.annonsemottak.receivers.xmlstilling;

import no.nav.pam.annonsemottak.receivers.xmlstilling.Stillinger.Gruppe;
import no.nav.pam.annonsemottak.stilling.Stilling;
import no.nav.pam.annonsemottak.stilling.StillingTestdataBuilder;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Random;
import java.util.function.Function;

import static java.util.Collections.emptyList;
import static no.nav.pam.annonsemottak.receivers.xmlstilling.Stillinger.Gruppe.*;
import static org.assertj.core.api.Assertions.assertThat;

public class StillingerTest {

    private Function<Stilling, Gruppe> randomGroup = st -> Gruppe.values()[new Random().nextInt(Gruppe.values().length)];
    private Function<Stilling, Gruppe> size_1_2_4_groups = new Function<Stilling, Gruppe>() {
        int i = 0;
        @Override
        public Gruppe apply(Stilling stilling) {
            return new Gruppe[]{ CHANGED, NEW, NEW, UNCHANGED, UNCHANGED, UNCHANGED, UNCHANGED }[i++];
        }
    };

    @Test
    public void find_latest_date() {
        LocalDateTime latestDate = LocalDateTime.of(2019, 1, 11, 14, 51, 11);

        Stillinger stillinger = new Stillinger(Arrays.asList(
                stillingMedSystemModifiedDate(latestDate.minusSeconds(1)),
                stillingMedSystemModifiedDate(latestDate),
                stillingMedSystemModifiedDate(latestDate.minusSeconds(2))),
                 randomGroup);

        assertThat(stillinger.latestDate().get())
                .isEqualTo(LocalDateTime.of(2019, 1, 11, 14, 51, 11));
    }

    @Test
    public void find_latest_date_with_no_results() {
        Stillinger nullResult = new Stillinger(null, randomGroup);
        assertThat(nullResult.latestDate()).isEmpty();

        Stillinger emptyResult = new Stillinger(emptyList(), randomGroup);
        assertThat(emptyResult.latestDate()).isEmpty();
    }

    @Test
    public void grouping() {
        Stillinger stillinger = new Stillinger(Arrays.asList(
                StillingTestdataBuilder.enkelStilling().build(),
                StillingTestdataBuilder.enkelStilling().build(),
                StillingTestdataBuilder.enkelStilling().build(),
                StillingTestdataBuilder.enkelStilling().build(),
                StillingTestdataBuilder.enkelStilling().build(),
                StillingTestdataBuilder.enkelStilling().build(),
                StillingTestdataBuilder.enkelStilling().build()),
        size_1_2_4_groups);

        assertThat(stillinger.size(CHANGED)).isEqualTo(1);
        assertThat(stillinger.size(NEW)).isEqualTo(2);
        assertThat(stillinger.size(UNCHANGED)).isEqualTo(4);

    }

    @Test
    public void size_calculation() {
        Stillinger stillinger = new Stillinger(Arrays.asList(
                StillingTestdataBuilder.enkelStilling().build(),
                StillingTestdataBuilder.enkelStilling().build(),
                StillingTestdataBuilder.enkelStilling().build(),
                StillingTestdataBuilder.enkelStilling().build(),
                StillingTestdataBuilder.enkelStilling().build(),
                StillingTestdataBuilder.enkelStilling().build(),
                StillingTestdataBuilder.enkelStilling().build()),
        size_1_2_4_groups);

        assertThat(stillinger.get(CHANGED).orElse(null)).hasSize(1);
        assertThat(stillinger.get(NEW).orElse(null)).hasSize(2);
        assertThat(stillinger.get(UNCHANGED).orElse(null)).hasSize(4);

        assertThat(stillinger.merge(CHANGED, NEW)).hasSize(3);
        assertThat(stillinger.merge(NEW, UNCHANGED)).hasSize(6);
        assertThat(stillinger.merge(CHANGED, UNCHANGED)).hasSize(5);
        assertThat(stillinger.merge(CHANGED, NEW, UNCHANGED)).hasSize(7);

        assertThat(stillinger.asList()).hasSize(7);
    }


    private Stilling stillingMedSystemModifiedDate(LocalDateTime systemModifiedDate) {
        return StillingTestdataBuilder.enkelStilling().systemModifiedDate(systemModifiedDate).build();
    }

}
