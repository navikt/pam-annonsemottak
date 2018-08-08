package no.nav.pam.annonsemottak.annonsemottak.amedia;

import org.junit.Test;

import static no.nav.pam.annonsemottak.annonsemottak.amedia.AmediaFieldTransformer.IKKE_OPPGITT;
import static org.assertj.core.api.Assertions.assertThat;

public class AmediaFieldTransformerTest {

    AmediaFieldTransformer transformer = new AmediaFieldTransformer();

    @Test
    public void brukerPrimaryField() {
        assertThat(transformer.reservefelt("pri", "sec")).isEqualTo("pri");
    }

    @Test
    public void brukerSecondaryField() {
        assertThat(transformer.reservefelt(null, "sec")).isEqualTo("sec");
    }

    @Test
    public void brukerIkkeFunnetField() {
        assertThat(transformer.reservefelt(null, null)).isEqualTo(IKKE_OPPGITT);
    }

    @Test
    public void ingenSteder() {
        assertThat(transformer.finnSted(null)).isEqualTo(IKKE_OPPGITT);
    }

    @Test
    public void stedUtenSlash() {
        assertThat(transformer.finnSted("sted")).isEqualTo("sted");
    }

    @Test
    public void stedMedSlash() {
        assertThat(transformer.finnSted("sted/sted1/sted2/sted3")).isEqualTo("sted3");
    }

}