package no.nav.pam.annonsemottak.annonsemottak.amedia.filter;

import no.nav.pam.annonsemottak.stilling.Stilling;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class SokeknappFilter extends StillingFilter {


    private static final Pattern SOKEKNAPP_PATTERN =
            Pattern.compile(
                    "(<em>)?&quot;søk (på )?stilling(en|s)?&quot;(</em>)?(([ -])knappen)?(( til hø[yg]re)|( på hø[yg]re side)|( oppe til hø[yg]re)|( i hø[yg]re menyen))?",
                    Pattern.CASE_INSENSITIVE);

    private static final Predicate<Stilling> FILTER_HAR_SOKEKNAPPTEKST =
            s -> s.getAnnonsetekst() != null && SOKEKNAPP_PATTERN.matcher(s.getAnnonsetekst()).find();

    private final Function<Stilling, Stilling> MAP_HAR_SOKEKNAPPTEKST =
            s -> nyStillingstekst(s, byttUtSokeknapptekst(s.getAnnonsetekst()));

    static String byttUtSokeknapptekst(String tekst) {

        Matcher matcher = SOKEKNAPP_PATTERN.matcher(tekst);
        if (matcher.find()) {
            return matcher.replaceFirst("annonselenken");
        }
        return tekst;
    }

    @Override
    public List<Stilling> doFilter(List<Stilling> stillinger) {
        List<Stilling> harSokeknapptekst = stillinger.stream()
                .filter(FILTER_HAR_SOKEKNAPPTEKST)
                .map(MAP_HAR_SOKEKNAPPTEKST)
                .collect(Collectors.toList());

        List<Stilling> ikkeSokeknapptekst = stillinger.stream()
                .filter(FILTER_HAR_SOKEKNAPPTEKST.negate())
                .collect(Collectors.toList());

        List<Stilling> nyListe = Stream
                .concat(harSokeknapptekst.stream(), ikkeSokeknapptekst.stream())
                .collect(Collectors.toList());

        logFilter(
                stillinger.size(),
                nyListe.size(),
                harSokeknapptekst,
                FilterAarsak.ENDRER_TEKST_FOR_SOKEKNAPP);

        return nyListe;
    }
}
