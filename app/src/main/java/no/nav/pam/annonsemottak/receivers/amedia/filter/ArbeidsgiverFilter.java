package no.nav.pam.annonsemottak.receivers.amedia.filter;

import no.nav.pam.annonsemottak.stilling.Arbeidsgiver;
import no.nav.pam.annonsemottak.stilling.Stilling;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * For arbeidsgivere som vi ikke ønsker annonser fra.
 */
class ArbeidsgiverFilter extends StillingFilter {


    @Override
    public List<Stilling> doFilter(List<Stilling> stillinger) {

        List<Stilling> svartelistet = stillinger.stream()
            .filter(FILTER_ER_SVARTELISTET)
            .collect(Collectors.toList());

        List<Stilling> ikkeSvartelistet = stillinger.stream()
            .filter(FILTER_ER_SVARTELISTET.negate())
            .collect(Collectors.toList());

        logFilter(
            stillinger.size(),
            ikkeSvartelistet.size(),
            svartelistet,
        FilterAarsak.ARBEIDSGIVER_FILTRES_BORT);

        return ikkeSvartelistet;
    }

    private static final Predicate<Stilling> FILTER_ER_SVARTELISTET = s -> Arrays
        .stream(EmployerBlacklist.values())
        .anyMatch(
            black -> s.getArbeidsgiver()
                .map(Arbeidsgiver::asString)
                .filter(arb -> arb.equalsIgnoreCase(black.getCompanyname()))
                .isPresent());
}
