package no.nav.pam.annonsemottak.receivers.amedia.filter;

import no.nav.pam.annonsemottak.stilling.Stilling;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FeltelengdeFilterTest {

    @Test
    void filtrerBortStillingerSomHarForLangeFelter() {
        Stilling expectedOkStilling = lagStilling();
        FeltelengdeFilter filter = new FeltelengdeFilter();

        List<Stilling> alleStillinger = new ArrayList<>();
        alleStillinger.add(expectedOkStilling);
        alleStillinger.add(lagStillingMedForlangeFelter());

        List<Stilling> filtrerteStillinger = filter.doFilter(alleStillinger);

        assertEquals(1, filtrerteStillinger.size());
        assertNotEquals(alleStillinger.size(), filtrerteStillinger.size());
        assertEquals(expectedOkStilling, filtrerteStillinger.getFirst());
    }

    private static Stilling lagStilling() {
        return new Stilling(
                "En tittel",
                "Et sted",
                "Arbeidsgiver",
                "Beskrivelse av arbeidsgiver",
                "Dummy job description",
                "2023-12-31",
                "Dummy kilde",
                "Dummy medium",
                "http://dummy.url",
                "DummyExternalId"
        );
    }

    private static Stilling lagStillingMedForlangeFelter() {
        String forLangtFelt = "A".repeat(300);
        String akkuratForLang = "B".repeat(255);
        String akkuratPasse = "C".repeat(254);
        return new Stilling(
                forLangtFelt,
                akkuratForLang,
                forLangtFelt,
                akkuratPasse,
                "Dummy job description",
                "2023-12-31",
                "Dummy kilde",
                "Dummy medium",
                "http://dummy.url",
                "DummyExternalId"
        );
    }

}
