package no.nav.pam.annonsemottak.annonsemottak.amedia.filter;

import no.nav.pam.annonsemottak.stilling.Arbeidsgiver;
import no.nav.pam.annonsemottak.stilling.Stilling;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Superklasse for filterene.
 */
abstract class StillingFilter {

    Logger LOG = LoggerFactory.getLogger(StillingFilter.class);


    protected abstract List<Stilling> doFilter(List<Stilling> stillinger);

    void logFilter(
        int antallFoerFiltrering,
        int antallEtterFiltrering,
        List<Stilling> modifiserteOgSlettedeStillinger,
        FilterAarsak aarsak) {

        LOG.info(
            "Stillinger filtrert fra Amedia: Årsak: {}, Antall før filtrering: {}, Antall etter filtering: {}, Antall modifisert: {}",
            aarsak.name(),
            antallFoerFiltrering,
            antallEtterFiltrering,
            modifiserteOgSlettedeStillinger.size());

        modifiserteOgSlettedeStillinger
            .forEach(s ->
                LOG.info("Stilling filtrert fra Amedia: {}", formatLog(s, aarsak))
            );

    }

    private String formatLog(Stilling stilling, FilterAarsak aarsak) {
        return
            "[Årsak=" + aarsak.name() + "]" +
                "[eksternid=" + stilling.getExternalId() + "]" +
                "[Arbeidsgiver=" + stilling.getArbeidsgiver().map(Arbeidsgiver::asString).orElse("Ikke spesifisert") + "]" +
                "[Annonsetittel=" + stilling.getStillingstittel() + "]" +
                "[Publikasjon=" + stilling.getProperties().get("publications") + "]";
    }

    /*
      Nytt stillingobjekt med ny tittel, shallow copy, men bytter hele arbeidsgiverfeltet.
    */
    Stilling nyTittel(Stilling s, String nyTittel) {
        Stilling stilling = new Stilling(nyTittel, s.getArbeidssted(),
            s.getArbeidsgiver().map(Arbeidsgiver::asString).orElse(null),
            s.getArbeidsgiveromtale(),
            s.getAnnonsetekst(), s.getSoeknadsfrist(), s.getKilde(), s.getMedium(), s.getUrl(),
            s.getExternalId(), s.getExpires(),
            s.getProperties(), s.getSystemModifiedDate());
        stilling.setUuid(s.getUuid());

        return stilling;
    }

    /*
        Nytt stillingobjekt med ny tekst, shallow copy, men bytter hele stillingstekstfeltet.
    */
    Stilling nyStillingstekst(Stilling s, String nyTekst) {
        Stilling stilling = new Stilling(s.getStillingstittel(), s.getArbeidssted(),
            s.getArbeidsgiver().map(Arbeidsgiver::asString).orElse(null),
            s.getArbeidsgiveromtale(),
            nyTekst, s.getSoeknadsfrist(), s.getKilde(), s.getMedium(), s.getUrl(),
            s.getExternalId(), s.getExpires(),
            s.getProperties(), s.getSystemModifiedDate());
        stilling.setUuid(s.getUuid());

        return stilling;
    }

}
