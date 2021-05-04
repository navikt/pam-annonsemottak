package no.nav.pam.annonsemottak.receivers.amedia.filter;

import no.nav.pam.annonsemottak.stilling.Stilling;
import no.nav.pam.annonsemottak.stilling.StillingTestdataBuilder;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ReferertLenkeManglerFilterTest {

    @Test
    public void filtrerbortStillingerOmLenkeikkeFinnes() {
        List<Stilling> stillinger = Arrays.asList(
                new StillingTestdataBuilder()
                        .tittel("uten lenke")
                        .stillingstekst("--&quot;Søk på stilling&quot;--")
                        .build(),
                new StillingTestdataBuilder()
                        .url("<a href=\"\">har lenke</a>")
                        .tittel("med lenke")
                        .stillingstekst("--&quot;Søk på stilling&quot;--")
                        .build(),
                new StillingTestdataBuilder()
                        .tittel("uten lenke uten søketekst")
                        .stillingstekst("----")
                        .build()
        );
        List<Stilling> nyeStillinger = new ReferertLenkeManglerFilter().doFilter(stillinger);

        assertThat(nyeStillinger)
                .hasSize(2)
                .haveAtLeastOne(new Condition<>(
                        st -> "med lenke".equals(st.getTitle()),
                        "Ikke filtrert"))
                .haveAtLeastOne(new Condition<>(
                        st -> "med lenke".equals(st.getTitle()),
                        "uten lenke uten søketekst"));
    }
}
