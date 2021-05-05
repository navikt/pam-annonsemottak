package no.nav.pam.annonsemottak.receivers.amedia.filter;

import no.nav.pam.annonsemottak.stilling.Stilling;
import no.nav.pam.annonsemottak.stilling.StillingTestdataBuilder;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AnnonsetekstManglerFilterTest {

    @Test
    public void filtrerbortStillingerOmAnnonsetekstMangler() {
        List<Stilling> stillinger = Arrays.asList(
            new StillingTestdataBuilder().stillingstekst("har tekst under 30 tegn")
                .tittel("tittelUtenStillingtekst").build(),
            new StillingTestdataBuilder()
                .stillingstekst("har tekst over 30 tegn ---------------------")
                .tittel("tittelMedStillingtekst").build()
        );
        List<Stilling> nyeStillinger = new AnnonsetekstManglerFilter().doFilter(stillinger);

        assertThat(nyeStillinger)
            .hasSize(1)
            .haveAtLeastOne(new Condition<>(
                st -> "tittelMedStillingtekst".equals(st.getTitle()),
                "Ikke filtrert"));
    }

}
