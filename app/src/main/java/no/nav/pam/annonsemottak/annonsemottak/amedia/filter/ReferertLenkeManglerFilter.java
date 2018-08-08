package no.nav.pam.annonsemottak.annonsemottak.amedia.filter;

import no.nav.pam.annonsemottak.stilling.Stilling;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * For annonser uten url, og som har referert til en lenke.
 */
class ReferertLenkeManglerFilter extends StillingFilter {

    private static final Predicate<Stilling> FILTER_MANGLER_LENKE =
            s -> StringUtils.isBlank(s.getUrl());

    private static final Pattern SOKEKNAPP_PATTERN =
            Pattern.compile(
                    "(<em>)?&quot;søk (på )?stilling(en|s)?&quot;(</em>)?",
                    Pattern.CASE_INSENSITIVE);

    private static final Predicate<Stilling> FILTER_HAR_SOKEKNAPPTEKST =
            s -> s.getAnnonsetekst() != null && SOKEKNAPP_PATTERN.matcher(s.getAnnonsetekst()).find();


    private final Function<Stilling, Stilling> MAP_MANGLER_REFERERT_LENKE = s -> nyTittel(s,
            "*" + this.getClass().getSimpleName() + "* " + s.getStillingstittel());

    @Override
    public List<Stilling> doFilter(List<Stilling> stillinger) {
        Predicate<Stilling> manglerReferertLenke = FILTER_MANGLER_LENKE.and(FILTER_HAR_SOKEKNAPPTEKST);
        List<Stilling> manglerLenke = stillinger.stream()
                .filter(manglerReferertLenke)
                .collect(Collectors.toList());

        List<Stilling> harLenke = stillinger.stream()
                .filter(manglerReferertLenke.negate())
                .collect(Collectors.toList());

        logFilter(
                stillinger.size(),
                harLenke.size(),
                manglerLenke,
                FilterAarsak.REFERERT_LENKE_MANGLER
                );
        return harLenke;
    }
}
