package no.nav.pam.annonsemottak.annonsemottak.dexi;

import no.nav.pam.annonsemottak.annonsemottak.GenericDateParser;
import no.nav.pam.annonsemottak.annonsemottak.Kilde;
import no.nav.pam.annonsemottak.markdown.HtmlToMarkdownConverter;
import no.nav.pam.annonsemottak.stilling.Stilling;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class DexiModel {

    static final String ANNONSETITTEL = "Annonsetittel";
    static final String ANNONSEURL = "AnnonseURL";
    static final String ANNONSETEKST = "Annonsetekst";
    static final String INGRESS = "Ingress";
    static final String EXTERNALID = "ExternalId";
    private static final String SOKNADSFRIST = "Soknadsfrist";
    private static final String ARBEIDSSTED = "Arbeidssted";
    static final String ARBEIDSGIVER = "Arbeidsgiver";
    private static final String ARBEIDSGIVEROMTALE = "Arbeidsgiveromtale";
    private static final String KILDE = "Kilde";

    private static final Logger LOG = LoggerFactory.getLogger(DexiModel.class);
    private static final List<String> nonPropertyFields;

    static {
        nonPropertyFields = Arrays.asList(
                ANNONSETITTEL,
                ARBEIDSSTED,
                ARBEIDSGIVER,
                ARBEIDSGIVEROMTALE,
                INGRESS,
                ANNONSETEKST,
                SOKNADSFRIST,
                KILDE,
                ANNONSEURL,
                EXTERNALID);
    }

    /**
     * Create a {@link Stilling} based on a {@code Map} representing basic JSON output from a dexi.io job.
     *
     * @param map       Map representation of JSON.
     * @param robotName The name of the robot delivering output, used as <i>medium</i>.
     * @return A {@link Stilling}, or {@code null} if the JSON representation was incomplete.
     */
    static Stilling toStilling(Map<String, String> map, String robotName) {

        if (hasIncompleteInformation(map)) {
            return null;
        }

        Map<String, String> props = map
                .entrySet()
                .stream()
                .peek(DexiModel::warnIfUnexpectedHtmlInContent)
                .filter(DexiModel::filterOutNonPropertyEntries)
                .filter(DexiModel::filterOutEmptyValuedEntries)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        Stilling stilling = new Stilling(
                HtmlToMarkdownConverter.parse(map.get(ANNONSETITTEL)).trim(),
                map.get(ARBEIDSSTED),
                map.get(ARBEIDSGIVER),
                HtmlToMarkdownConverter.parse(map.get(ARBEIDSGIVEROMTALE)),
                concatenateAndWashAnnonsetekst(HtmlToMarkdownConverter.parse(map.get(INGRESS)), HtmlToMarkdownConverter.parse(map.get(ANNONSETEKST))),
                map.get(SOKNADSFRIST),
                Kilde.DEXI.toString(),
                robotName,
                map.get(ANNONSEURL),
                map.get(EXTERNALID));

        stilling.getProperties().putAll(props);
        stilling.setExpires(GenericDateParser.parse(map.get(SOKNADSFRIST)).orElse(null));

        return stilling;
    }

    private static boolean hasIncompleteInformation(Map<String, String> map) {
        if (StringUtils.isBlank(map.get(ANNONSETITTEL))) {
            LOG.error("Title was empty for ad with ID {} at {}", map.get(EXTERNALID), map.get(ANNONSEURL));
            return true;
        }
        if (StringUtils.isBlank(map.get(ANNONSETEKST))) {
            LOG.error("Text was empty for ad with ID {}at {}", map.get(EXTERNALID), map.get(ANNONSEURL));
            return true;
        }
        if (StringUtils.isBlank(map.get(ARBEIDSGIVER))) {
            LOG.error("Employer was empty for ad with ID {}", map.get(EXTERNALID));
            return true;
        }
        return false;
    }

    private static boolean filterOutEmptyValuedEntries(Map.Entry<String, String> entry) {
        return !StringUtils.isEmpty(entry.getValue());
    }

    private static boolean filterOutNonPropertyEntries(Map.Entry<String, String> entry) {
        return !nonPropertyFields.contains(entry.getKey());
    }

    private static String concatenateAndWashAnnonsetekst(String ingress, String annonsetekst) {
        if (annonsetekst != null && ingress != null) {
            String concatination = ingress.concat(annonsetekst);
            return concatination.replaceAll("\u2022", "<br/>");
        }

        return annonsetekst;
    }

    private static void warnIfUnexpectedHtmlInContent(Map.Entry<String, String> entry) {
        if (!entry.getKey().equals(ARBEIDSGIVEROMTALE) &&
                !entry.getKey().equals(ANNONSETEKST) &&
                entry.getValue() != null &&
                entry.getValue().contains("<")) {
            LOG.warn("Found unexpected '<' in field {}, might be HTML", entry.getKey());
        }
    }

}
