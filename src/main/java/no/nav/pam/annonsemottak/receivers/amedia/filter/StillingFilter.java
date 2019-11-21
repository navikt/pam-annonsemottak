package no.nav.pam.annonsemottak.receivers.amedia.filter;

import no.nav.pam.annonsemottak.stilling.Arbeidsgiver;
import no.nav.pam.annonsemottak.stilling.Stilling;
import no.nav.pam.annonsemottak.stilling.StillingBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Superklasse for filterene.
 */
abstract class StillingFilter {

    final Logger LOG = LoggerFactory.getLogger(StillingFilter.class);


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
                        "[Annonsetittel=" + stilling.getTitle() + "]" +
                        "[Publikasjon=" + stilling.getProperties().get("publications") + "]";
    }

    /*
      Nytt stillingobjekt med ny tittel, shallow copy, men bytter hele arbeidsgiverfeltet.
    */
    Stilling nyTittel(Stilling s, String nyTittel) {

        return new StillingBuilder()
                .title(nyTittel)
                .place(s.getPlace())
                .employer(s.getArbeidsgiver().map(Arbeidsgiver::asString).orElse(null))
                .employerDescription(s.getEmployerDescription())
                .jobDescription(s.getJobDescription())
                .dueDate(s.getDueDate())
                .kilde(s.getKilde())
                .medium(s.getMedium())
                .url(s.getUrl())
                .externalId(s.getExternalId())
                .withProperties(s.getProperties())
                .expires(s.getExpires())
                .uuid(s.getUuid())
                .systemModifiedDate(s.getSystemModifiedDate())
                .build();
    }

    /*
        Nytt stillingobjekt med ny tekst, shallow copy, men bytter hele stillingstekstfeltet.
    */
    Stilling nyStillingstekst(Stilling s, String nyTekst) {

        return new StillingBuilder()
                .title(s.getTitle())
                .place(s.getPlace())
                .employer(s.getArbeidsgiver().map(Arbeidsgiver::asString).orElse(null))
                .employerDescription(s.getEmployerDescription())
                .jobDescription(nyTekst)
                .dueDate(s.getDueDate())
                .kilde(s.getKilde())
                .medium(s.getMedium())
                .url(s.getUrl())
                .externalId(s.getExternalId())
                .withProperties(s.getProperties())
                .expires(s.getExpires())
                .uuid(s.getUuid())
                .systemModifiedDate(s.getSystemModifiedDate())
                .build();
    }

}
