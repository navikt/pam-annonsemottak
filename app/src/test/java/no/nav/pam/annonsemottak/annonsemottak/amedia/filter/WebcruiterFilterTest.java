package no.nav.pam.annonsemottak.annonsemottak.amedia.filter;

import no.nav.pam.annonsemottak.stilling.Stilling;
import no.nav.pam.annonsemottak.stilling.StillingTestdataBuilder;
import org.assertj.core.api.Condition;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class WebcruiterFilterTest {


    @Test
    public void filtrerbortStillingerOmAnnonsetekstMangler() {
        List<Stilling> stillinger = Arrays.asList(
            new StillingTestdataBuilder().url("aaawww.webcruiter.noaaa")
                .tittel("tittelMedWebcruiterIUrl").build(),
            new StillingTestdataBuilder().stillingstekst("aaavia Webcruiteraaaa")
                .tittel("tittelMedWebcruiterITekst").build(),
            new StillingTestdataBuilder().url("aaa").stillingstekst("aaa")
                .tittel("tittelUtenWebcruiter").build()
        );
        List<Stilling> nyeStillinger = new WebcruiterFilter().doFilter(stillinger);

        assertThat(nyeStillinger)
            .hasSize(1)
            .haveAtLeastOne(new Condition<>(
                st -> "tittelUtenWebcruiter".equals(st.getTitle()),
                "Ikke filtrert"));
    }
}