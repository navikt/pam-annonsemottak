package no.nav.pam.annonsemottak.receivers.xmlstilling;

import no.nav.pam.annonsemottak.markdown.HtmlToMarkdownConverter;
import no.nav.pam.annonsemottak.receivers.Kilde;
import no.nav.pam.annonsemottak.receivers.common.PropertyNames;
import no.nav.pam.annonsemottak.stilling.Stilling;

import java.time.LocalDateTime;

import static no.nav.pam.annonsemottak.receivers.common.PropertyNames.*;

class XmlStillingMapper {

    private static final String STILLINGSPROSENT = "STILLINGSPROSENT";
    static final String STILLINGSTITTEL_ER_IKKE_SATT = "[Stillingstittel er ikke satt]";

    static Stilling fromDto(XmlStillingDto dto) {

        Stilling stilling = new Stilling(
                extractStillingtittel(dto),
                null,
                dto.getArbeidsgiver(),
                HtmlToMarkdownConverter.parse(dto.getArbeidsgiverBedriftspresentasjon()),
                HtmlToMarkdownConverter.parse(dto.getStillingsbeskrivelse()),
                dto.getSoknadsfrist().map(LocalDateTime::toString).orElse(""),
                Kilde.XML_STILLING.toString(),
                dto.getEksternBrukerRef(),
                null,
                dto.getEksternId()
        );

        stilling.setExpires(dto.getSistePubliseringsdato());
        stilling.setPublished(dto.getPubliseresFra());

        stilling.getProperties().put(ANTALL_STILLINGER, stringFrom(dto.getAntallStillinger()));
        stilling.getProperties().put(FYLKE, stringFrom(dto.getArbeidssted()));
        stilling.getProperties().put(STILLINGSPROSENT, stringFrom(dto.getStillingsprosent()));
        stilling.getProperties().put(KONTAKTPERSON, stringFrom(dto.getKontaktinfoPerson()));
        stilling.getProperties().put(KONTAKTPERSON_TELEFON, stringFrom(dto.getKontaktinfoTelefon()));
        stilling.getProperties().put(KONTAKTPERSON_EPOST, stringFrom(dto.getKontaktinfoEpost()));
        stilling.getProperties().put(LOCATION_ADDRESS, stringFrom(dto.getArbeidsgiverAdresse()));
        stilling.getProperties().put(LOCATION_POSTCODE, stringFrom(dto.getArbeidsgiverPostnummer()));
        stilling.getProperties().put(EMPLOYER_URL, stringFrom(dto.getArbeidsgiverWebadresse()));
        stilling.getProperties().put(TILTREDELSE, stringFrom(dto.getLedigFra()));


        stilling.setSystemModifiedDate(dto.getMottattTidspunkt());

        stilling.setArenaId(dto.getArenaId());

        return stilling;
    }

    private static String extractStillingtittel(XmlStillingDto dto) {
        String result = HtmlToMarkdownConverter.parse(dto.getStillingstittel()).trim();
        return result.isEmpty() ? STILLINGSTITTEL_ER_IKKE_SATT : result;
    }

    private static <T> String stringFrom(T value) {
        return value == null ? "" : value.toString();
    }

}
