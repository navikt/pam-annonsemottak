package no.nav.pam.annonsemottak.annonsemottak.amedia.filter;

import no.nav.pam.annonsemottak.stilling.Stilling;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * For annoneser som bruker webcruiter. Vi skal få de inn på andre måter.
 */
class WebcruiterFilter extends StillingFilter {

    private static final Predicate<Stilling> FILTER_WEBCRUITER =
        s -> StringUtils.contains(s.getUrl(), "www.webcruiter.no") ||
            StringUtils.contains(s.getJobDescription(), "via Webcruiter");
    private final Function<Stilling, Stilling> MAP_WEBCRUITER = s -> nyTittel(s,
        "*" + this.getClass().getSimpleName() + "* " + s.getTitle());

    @Override
    public List<Stilling> doFilter(List<Stilling> stillinger) {
        List<Stilling> viaWebcruiter = stillinger.stream()
            .filter(FILTER_WEBCRUITER)
            .map(MAP_WEBCRUITER)
            .collect(Collectors.toList());

        List<Stilling> ikkeViaWebcruiter = stillinger.stream()
            .filter(FILTER_WEBCRUITER.negate())
            .collect(Collectors.toList());

        logFilter(
            stillinger.size(),
            ikkeViaWebcruiter.size(),
            viaWebcruiter,
            FilterAarsak.WEBCRUITER);
        return ikkeViaWebcruiter;
    }
}
