package no.nav.pam.annonsemottak.stilling;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class SorteringTest {

    @Test
    public void sortering_skal_støtte_mottattdato() {
        Sortering sorteringEtterMottattdatoAscending = Sortering.valueOf(Sortering.OrderBy.nullSafeValueOf("Mottattdato"), null);
        assertThat(sorteringEtterMottattdatoAscending.asSort(), is(equalTo(Sort.by(Sort.Direction.ASC, "created"))));
    }

    @Test
    public void sortering_skal_støtte_arbeidsgiver() {
        Sortering sorteringEtterMottattdatoAscending = Sortering.valueOf(Sortering.OrderBy.nullSafeValueOf("Arbeidsgiver"), null);
        assertThat(sorteringEtterMottattdatoAscending.asSort(), is(equalTo(Sort.by(Sort.Direction.ASC, "employer"))));
    }

    @Test
    public void sortering_skal_støtte_arbeidssted() {
        Sortering sorteringEtterMottattdatoAscending = Sortering.valueOf(Sortering.OrderBy.nullSafeValueOf("Sted"), null);
        assertThat(sorteringEtterMottattdatoAscending.asSort(), is(equalTo(Sort.by(Sort.Direction.ASC, "place"))));
    }

    @Test
    public void sortering_skal_støtte_stillingstittel() {
        Sortering sorteringEtterMottattdatoAscending = Sortering.valueOf(Sortering.OrderBy.nullSafeValueOf("Tittel"), null);
        assertThat(sorteringEtterMottattdatoAscending.asSort(), is(equalTo(Sort.by(Sort.Direction.ASC, "title"))));
    }

    @Test
    public void asSort_skal_lage_samme_objekt_som_bruk_av_sort_sin_constructor_direkte() {
        Sort sort = Sortering.valueOf(Sortering.OrderBy.MOTTATTDATO, null).asSort();
        Sort sort2 = Sort.by(Sort.Direction.ASC, "created");

        assertThat(sort, is(equalTo(sort2)));
    }
}
