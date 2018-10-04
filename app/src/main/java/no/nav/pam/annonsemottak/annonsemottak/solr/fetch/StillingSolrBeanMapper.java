package no.nav.pam.annonsemottak.annonsemottak.solr.fetch;

import no.nav.pam.annonsemottak.annonsemottak.Kilde;
import no.nav.pam.annonsemottak.annonsemottak.solr.StillingSolrBean;
import no.nav.pam.annonsemottak.markdown.HtmlToMarkdownConverter;
import no.nav.pam.annonsemottak.stilling.IllegalSaksbehandlingCommandException;
import no.nav.pam.annonsemottak.stilling.OppdaterSaksbehandlingCommand;
import no.nav.pam.annonsemottak.stilling.Stilling;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static no.nav.pam.annonsemottak.annonsemottak.common.PropertyNames.*;

class StillingSolrBeanMapper {

    public static final Logger LOG = LoggerFactory.getLogger(StillingSolrBeanMapper.class);

    static Stilling mapToStilling(StillingSolrBean solrBean) {
        String medium = solrBean.getKildetekst();
        LocalDateTime expires = dateToLocalDateTime(solrBean.getSistePubliseringsdato());
        LocalDateTime published = dateToLocalDateTime(solrBean.getPubliseresFra());
        LocalDateTime regDato = dateToLocalDateTime(solrBean.getRegDato());

        Map<String, String> properties = new HashMap<>();
        properties.put(ANTALL_STILLINGER, fieldToString(solrBean.getAntallStillinger()));
        properties.put(FYLKE, arrayListToString(solrBean.getFylke()));
        properties.put(HELTIDDELTID, convertToHeltidOrDeltid(solrBean.getHeltiddeltid()));

        properties.put(LOCATION_ADDRESS, arrayListToString(solrBean.getAdresselinje()));
        properties.put(LOCATION_CITY, solrBean.getAdressepoststed());
        properties.put(LOCATION_COUNTRY, solrBean.getLand());
        properties.put(LOCATION_POSTCODE, arrayListToString(solrBean.getAdressepostnr()));

        properties.put(STILLINGSTITTEL, solrBean.getStillingstype());
        properties.put(APPLICATION_LABEL, solrBean.getSoknadmerkes());
        properties.put(EMPLOYER_URL, solrBean.getKommunikasjonUrl());
        properties.put(VARIGHET, arrayListToString(solrBean.getAnsettelsesforhold()));

        properties.put(KONTAKTPERSON, solrBean.getKontaktperson());
        properties.put(KONTAKTPERSON_EPOST, solrBean.getKommunikasjonEpost());
        properties.put(KONTAKTPERSON_TELEFON, solrBean.getKommunikasjonTelefon());
        properties.put(ARBEIDSTID, arrayListToString(solrBean.getArbeidsordning()));

        properties.put(StillingSolrBeanFieldNames.SOKNADSENDES, solrBean.getSoknadsendes());
        properties.put(StillingSolrBeanFieldNames.REG_DATO, regDato.toString());
        properties.put(StillingSolrBeanFieldNames.STILLINGSPROSENT, fieldToString(solrBean.getStillingsprosent()));

        properties.put(StillingSolrBeanFieldNames.LONNSINFO, fieldToString(solrBean.getLonnsinfo()));
        properties.put(StillingSolrBeanFieldNames.UTDANNING, fieldToString(solrBean.getUtdanning()));
        properties.put(StillingSolrBeanFieldNames.SERTIFIKAT, arrayListToString(solrBean.getSertifikat()));
        properties.put(StillingSolrBeanFieldNames.KOMPETANSE, arrayListToString(solrBean.getKompetanse()));
        properties.put(StillingSolrBeanFieldNames.PRAKSIS, arrayListToString(solrBean.getPraksis()));

        properties.entrySet().removeIf(entry -> entry.getValue() == null || entry.getValue().isEmpty());

        Stilling newStilling = new Stilling(
                solrBean.getTittel(),
                arrayListToString(solrBean.getGeografiskomrade()),
                solrBean.getArbeidsgivernavn(),
                HtmlToMarkdownConverter.parse(solrBean.getBedriftspresentasjon()),
                HtmlToMarkdownConverter.parse(solrBean.getStillingsbeskrivelse()),
                dateToLocalDateTime(solrBean.getSoknadsfrist()).toString(),
                Kilde.STILLINGSOLR.value(),
                medium,
                "",
                fieldToString(solrBean.getId()),
                (solrBean.getSoknadsfrist() != null) ? dateToLocalDateTime(solrBean.getSoknadsfrist()) : expires,
                properties,
                null);

        newStilling.setPublished(published);

        try {
            final Map<String, String> params = new HashMap<>();
            params.put("status", "2");
            params.put("saksbehandler", "System");
            OppdaterSaksbehandlingCommand saksbehandlingCommand = new OppdaterSaksbehandlingCommand(params);
            newStilling.oppdaterMed(saksbehandlingCommand);
        } catch (IllegalSaksbehandlingCommandException e) {
            LOG.debug("Kunne ikke oppdatere stilling med status godkjent", e);
        }

        return newStilling;
    }

    private static String convertToHeltidOrDeltid(ArrayList<String> heltidDeltidList) {
        String heltidDeltid = arrayListToString(heltidDeltidList).toLowerCase();
        if (heltidDeltid.isEmpty()) {
            return "";
        } else if (heltidDeltid.contains("heltid")) {
            return "Heltid";
        }

        return "Deltid";
    }

    private static String fieldToString(Object field) {
        if (field == null) {
            return "";
        }
        return field.toString();
    }

    private static String arrayListToString(ArrayList<String> list) {
        if (list == null) {
            return "";
        }

        return list.stream()
                .map(Object::toString)
                .collect(Collectors.joining(" "));
    }

    private static LocalDateTime dateToLocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
