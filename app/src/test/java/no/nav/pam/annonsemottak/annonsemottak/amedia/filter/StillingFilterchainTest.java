package no.nav.pam.annonsemottak.annonsemottak.amedia.filter;

import no.nav.pam.annonsemottak.stilling.Stilling;
import no.nav.pam.annonsemottak.stilling.StillingTestdataBuilder;
import org.assertj.core.api.SoftAssertions;
import org.junit.Test;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

public class StillingFilterchainTest {

    @Test
    public void filterchaintest() {
        List<Stilling> stillinger = Arrays.asList(
            new StillingTestdataBuilder()
                .tittel("filtreres ikke pga at den har link i tillegg til knappetekst")
                .arbeidsgiver("arb1")
                .stillingstekst(
                    "ved å klikke på &quot;Søk på Stilling&quot;-knappen til høyre på denne siden.")
                .url("aaawww.blabla.noaaa")
                .systemModifiedDate(Instant.ofEpochMilli(1).atZone(ZoneId.systemDefault()).toLocalDateTime())
                .build(),
            new StillingTestdataBuilder()
                .tittel("filtreres ikke pga at den har link i tillegg til knappetekst")
                .arbeidsgiver("arb1")
                .stillingstekst(
                    "ved å klikke på &quot;Søk på Stilling&quot;-knappen til høyre på denne siden.")
                .url("aaawww.webcruiter.noaaa")
                .systemModifiedDate(Instant.ofEpochMilli(2).atZone(ZoneId.systemDefault()).toLocalDateTime())
                .build(),

            new StillingTestdataBuilder()
                .tittel("filtreres pga at den ikke har link i tillegg til knappetekst")
                .arbeidsgiver("arb2")
                .stillingstekst(
                    "ved å klikke på &quot;Søk på Stilling&quot;-knappen til høyre på denne siden.")
                .systemModifiedDate(Instant.ofEpochMilli(3).atZone(ZoneId.systemDefault()).toLocalDateTime())
                .build(),

            new StillingTestdataBuilder()
                .tittel("filtreres pga lite tekst")
                .arbeidsgiver("arb3")
                .stillingstekst("for lite tekst")
                .url("")
                .systemModifiedDate(Instant.ofEpochMilli(4).atZone(ZoneId.systemDefault()).toLocalDateTime())
                .build(),
            new StillingTestdataBuilder()
                .tittel("ikke filtrering pga tekst")
                .arbeidsgiver("arb4")
                .stillingstekst(
                    "Mer enn 30 tegn --------------------------------------------------------")
                .url("lenke")
                .systemModifiedDate(Instant.ofEpochMilli(4).atZone(ZoneId.systemDefault()).toLocalDateTime())
                .build()

        );

        List<Stilling> nyeStillinger = new StillingFilterchain().doFilter(stillinger);

        SoftAssertions s = new SoftAssertions();
        s.assertThat(nyeStillinger).hasSize(2);

        s.assertThat(nyeStillinger.get(0).getStillingstittel())
            .isEqualTo("filtreres ikke pga at den har link i tillegg til knappetekst");
        s.assertThat(nyeStillinger.get(0).getAnnonsetekst())
            .isEqualTo("ved å klikke på annonselenken på denne siden.");
        s.assertThat(nyeStillinger.get(1).getStillingstittel())
            .isEqualTo("ikke filtrering pga tekst");
        s.assertAll();

    }
}