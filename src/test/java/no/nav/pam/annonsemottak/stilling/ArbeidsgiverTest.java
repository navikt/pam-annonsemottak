package no.nav.pam.annonsemottak.stilling;

import org.junit.Test;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class ArbeidsgiverTest {

    @Test
    public void skal_trimme_arbeidsgiver_ved_opprettelse() {
        Optional<Arbeidsgiver> arbeidsgiver = Arbeidsgiver.ofNullable("\n" +
                "                                    Skanska Teknikk\n" +
                "                                ");

        assertThat(arbeidsgiver.get().asString(), is(equalTo("Skanska Teknikk")));
    }
}
