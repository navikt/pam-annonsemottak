package no.nav.pam.annonsemottak.receivers.amedia.filter;

import no.nav.pam.annonsemottak.stilling.Stilling;
import no.nav.pam.annonsemottak.stilling.StillingTestdataBuilder;
import org.assertj.core.api.Condition;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


public class SokeknappFilterTest {

    @Test
    public void filtrererBortTekstPaaSokeknapp() {
        String tekst = "<b>Søknad sendes</b><br /><b>Alle søknader skal sendes elektronisk via vårt søkesystem ved å klikke på &quot;Søk på Stilling&quot;-knappen til høyre på denne siden.</b><br />Attester og vitnemål skal ikke innsendes, men medbringes ved evt. intervju.";
        String forventet = "<b>Søknad sendes</b><br /><b>Alle søknader skal sendes elektronisk via vårt søkesystem ved å klikke på annonselenken på denne siden.</b><br />Attester og vitnemål skal ikke innsendes, men medbringes ved evt. intervju.";
        List<Stilling> stillinger = Arrays
            .asList(new StillingTestdataBuilder().stillingstekst(tekst).build(),
                new StillingTestdataBuilder().stillingstekst("annen tekst").build());
        List<Stilling> nyeStillinger = new SokeknappFilter().doFilter(stillinger);

        assertThat(nyeStillinger)
            .hasSize(2)
            .haveAtLeastOne(new Condition<>(
                st -> "annen tekst".equals(st.getJobDescription()),
                "har riktig tekst uten endring"))
            .haveAtLeastOne(new Condition<>(
                st -> forventet.equalsIgnoreCase(st.getJobDescription()),
                "har riktig tekst med endring"));

    }

    @Test
    public void filterTest() {

        SoftAssertions s = new SoftAssertions();
        s.assertThat(
            SokeknappFilter.byttUtSokeknapptekst("--&quot;Søk på Stilling&quot;-knappen--"))
            .isEqualToIgnoringCase("--annonselenken--");
        s.assertThat(SokeknappFilter
            .byttUtSokeknapptekst("--&quot;Søk på Stilling&quot;-knappen til høyre--"))
            .isEqualToIgnoringCase("--annonselenken--");
        s.assertThat(SokeknappFilter
            .byttUtSokeknapptekst("--&quot;Søk på Stilling&quot;-knappen til høgre--"))
            .isEqualToIgnoringCase("--annonselenken--");
        s.assertThat(SokeknappFilter
            .byttUtSokeknapptekst("--&quot;Søk på Stilling&quot;-knappen på høyre side--"))
            .isEqualToIgnoringCase("--annonselenken--");
        s.assertThat(
            SokeknappFilter.byttUtSokeknapptekst("--&quot;Søk på Stilling&quot;-knappen"))
            .isEqualToIgnoringCase("--annonselenken");
        s.assertThat(
            SokeknappFilter.byttUtSokeknapptekst("--&quot;søk på stilling&quot;-knappen--"))
            .isEqualToIgnoringCase("--annonselenken--");
        s.assertThat(SokeknappFilter
            .byttUtSokeknapptekst("--&quot;søk på stillingen&quot; oppe til høyre--"))
            .isEqualToIgnoringCase("--annonselenken--");
        s.assertThat(SokeknappFilter
            .byttUtSokeknapptekst("--&quot;søk på stillings&quot; knappen på høyre side--"))
            .isEqualToIgnoringCase("--annonselenken--");
        s.assertThat(SokeknappFilter
            .byttUtSokeknapptekst("--&quot;Søk stillingen&quot;--"))
            .isEqualToIgnoringCase("--annonselenken--");
        s.assertThat(SokeknappFilter
            .byttUtSokeknapptekst("--&quot;Søk på Stilling&quot; i høyre menyen--"))
            .isEqualToIgnoringCase("--annonselenken--");
        s.assertThat(SokeknappFilter
            .byttUtSokeknapptekst("--<em>&quot;Søk på Stilling&quot;</em> knappen på høyre side--"))
            .isEqualToIgnoringCase("--annonselenken--");

        s.assertAll();
    }
}
