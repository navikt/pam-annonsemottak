package no.nav.pam.annonsemottak.receivers.amedia.filter;

import no.nav.pam.annonsemottak.stilling.Stilling;
import no.nav.pam.annonsemottak.stilling.StillingTestdataBuilder;
import org.assertj.core.api.Condition;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ArbeidsgiverFilterTest {

    @Test
    public void filtrerbortStillingerOmDetErFilterPaaArbeidsgiver() {
        List<Stilling> stillinger = Arrays.asList(
                new StillingTestdataBuilder().arbeidsgiver("testemployerblacklisted")
                        .tittel("tittelBlacklisted").build(),
                new StillingTestdataBuilder().arbeidsgiver("testikkeblacklisted")
                        .tittel("tittelIkkeBlacklisted").build()
        );
        List<Stilling> nyeStillinger = new ArbeidsgiverFilter().doFilter(stillinger);

        assertThat(nyeStillinger)
                .hasSize(1)
                .haveAtLeastOne(new Condition<>(
                        st -> "tittelIkkeBlacklisted".equals(st.getTitle()),
                        "Ikke filtrert"));
    }

}