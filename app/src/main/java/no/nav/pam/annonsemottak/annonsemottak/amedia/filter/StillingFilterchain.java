package no.nav.pam.annonsemottak.annonsemottak.amedia.filter;

import no.nav.pam.annonsemottak.stilling.Stilling;
import org.joda.time.DateTimeComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Entrypoint for å kalle filterene, kjører alle filterene i en kjede.
 */
public class StillingFilterchain extends StillingFilter {

    private static final Logger LOG = LoggerFactory.getLogger(StillingFilterchain.class);

    private static final List<StillingFilter> filterchain =
        Arrays.asList(
            new WebcruiterFilter(),
            new AnnonsetekstManglerFilter(),
            new ReferertLenkeManglerFilter(),
            new SokeknappFilter(),
            new ArbeidsgiverFilter()
        );

    @Override
    public List<Stilling> doFilter(List<Stilling> stillinger) {

        List<Stilling> filtrert = doFilter(stillinger, filterchain.iterator());

        LOG.info("Filterkjede, antall før filtrering: {}, antall etter filtrering: {}", stillinger.size(), filtrert.size());

        return filtrert.stream()
            .sorted((s1, s2) -> DateTimeComparator.getInstance()
                .compare(s1.getSystemModifiedDate(), s2.getSystemModifiedDate()))
            .collect(Collectors.toList());
    }

    List<Stilling> doFilter(List<Stilling> s, Iterator<StillingFilter> i) {
        if (!i.hasNext()) {
            return s;
        }
        return doFilter(i.next().doFilter(s), i);
    }

}
