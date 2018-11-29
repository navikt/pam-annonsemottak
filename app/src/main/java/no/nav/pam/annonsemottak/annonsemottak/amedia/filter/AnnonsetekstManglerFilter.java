package no.nav.pam.annonsemottak.annonsemottak.amedia.filter;

import no.nav.pam.annonsemottak.stilling.Stilling;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * For annonsetekster som mangler eller er for korte
 */
class AnnonsetekstManglerFilter extends StillingFilter {

    private static final Integer MIN_LENGDE_ANNONSE = 30;


    private static final Predicate<Stilling> FILTER_MANGLER_ANNONSETEKST =
        s -> StringUtils.isBlank(s.getJobDescription())
            || s.getJobDescription().length() < MIN_LENGDE_ANNONSE;


    @Override
    public List<Stilling> doFilter(List<Stilling> stillinger) {
        List<Stilling> manglerAnnonsetekst = stillinger.stream()
            .filter(FILTER_MANGLER_ANNONSETEKST)
            .collect(Collectors.toList());

        List<Stilling> harAnnonsetekst = stillinger.stream()
            .filter(FILTER_MANGLER_ANNONSETEKST.negate())
            .collect(Collectors.toList());

        logFilter(
            stillinger.size(),
            harAnnonsetekst.size(),
            manglerAnnonsetekst,
            FilterAarsak.FOR_LITE_STILLINGSBESKRIVELSE);

        return harAnnonsetekst;
    }
}
