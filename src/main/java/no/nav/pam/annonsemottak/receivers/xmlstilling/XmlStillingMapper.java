package no.nav.pam.annonsemottak.receivers.xmlstilling;

import no.nav.pam.annonsemottak.markdown.HtmlToMarkdownConverter;
import no.nav.pam.annonsemottak.receivers.Kilde;
import no.nav.pam.annonsemottak.receivers.common.PropertyNames;
import no.nav.pam.annonsemottak.stilling.Stilling;
import no.nav.pam.annonsemottak.stilling.StillingBuilder;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static no.nav.pam.annonsemottak.receivers.common.PropertyNames.*;

class XmlStillingMapper {

    private static final String STILLINGSPROSENT = "STILLINGSPROSENT";
    static final String STILLINGSTITTEL_ER_IKKE_SATT = "[Stillingstittel er ikke satt]";

    static Stilling fromDto(XmlStillingDto dto) {

        Stilling stilling = new StillingBuilder()
                .title(extractStillingtittel(dto))
                .place(null)
                .employer(dto.getArbeidsgiver())
                .employerDescription(HtmlToMarkdownConverter.parse(dto.getArbeidsgiverBedriftspresentasjon()))
                .jobDescription(HtmlToMarkdownConverter.parse(dto.getStillingsbeskrivelse()))
                .dueDate(dto.getSoknadsfrist().map(LocalDateTime::toString).orElse(""))
                .kilde(Kilde.XML_STILLING.toString())
                .medium(dto.getEksternBrukerRef())
                .url(null)
                .externalId(dto.getEksternId())
                .expires(dto.getSistePubliseringsdato())
                .withProperties(extractProperties(dto))
                .systemModifiedDate(dto.getMottattTidspunkt())
                .published(dto.getPubliseresFra())
                .build();

        stilling.setArenaId(dto.getArenaId());

        return stilling;
    }

    private static Map<String, String> extractProperties(XmlStillingDto dto) {
        Map<String, String> properties = new HashMap<>();

        properties.put(ANTALL_STILLINGER, stringFrom(dto.getAntallStillinger()));
        properties.put(FYLKE, stringFrom(dto.getArbeidssted()));
        properties.put(STILLINGSPROSENT, stringFrom(dto.getStillingsprosent()));
        properties.put(KONTAKTPERSON, stringFrom(dto.getKontaktinfoPerson()));
        properties.put(KONTAKTPERSON_TELEFON, stringFrom(dto.getKontaktinfoTelefon()));
        properties.put(KONTAKTPERSON_EPOST, stringFrom(dto.getKontaktinfoEpost()));
        properties.put(LOCATION_ADDRESS, stringFrom(dto.getArbeidsgiverAdresse()));
        properties.put(LOCATION_POSTCODE, stringFrom(dto.getArbeidsgiverPostnummer()));
        properties.put(EMPLOYER_URL, stringFrom(dto.getArbeidsgiverWebadresse()));
        properties.put(TILTREDELSE, stringFrom(dto.getLedigFra()));

        return properties;
    }

    private static String extractStillingtittel(XmlStillingDto dto) {
        String result = HtmlToMarkdownConverter.parse(dto.getStillingstittel()).trim();
        return result.isEmpty() ? STILLINGSTITTEL_ER_IKKE_SATT : result;
    }

    private static <T> String stringFrom(T value) {
        return value == null ? "" : value.toString();
    }

}
