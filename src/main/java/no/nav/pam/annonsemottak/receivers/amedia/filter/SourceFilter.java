package no.nav.pam.annonsemottak.receivers.amedia.filter;

import no.nav.pam.annonsemottak.stilling.Stilling;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

class SourceFilter extends StillingFilter {

    private static List<String> exclude_source = Arrays.asList("recman", "easycruit");

    private static final Predicate<Stilling> FILTER_SOURCE =
        s -> (s.getUrl()!=null && isOneOfSource(s.getUrl()));

    @Override
    public List<Stilling> doFilter(List<Stilling> stillinger) {
        List<Stilling> notSource = stillinger.stream()
            .filter(FILTER_SOURCE.negate())
            .collect(Collectors.toList());
        return notSource;
    }

    private static Boolean isOneOfSource(String source) {
        return exclude_source.stream().anyMatch(source::contains);
    }

}
