package no.nav.pam.annonsemottak.annonsemottak.finn;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.pam.annonsemottak.annonsemottak.GenericDateParser;
import no.nav.pam.annonsemottak.annonsemottak.Kilde;
import no.nav.pam.annonsemottak.annonsemottak.Medium;
import no.nav.pam.annonsemottak.annonsemottak.common.PropertyNames;
import no.nav.pam.annonsemottak.markdown.HtmlToMarkdownConverter;
import no.nav.pam.annonsemottak.stilling.Stilling;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class FinnAdMapper {

    private static final Logger LOG = LoggerFactory.getLogger(FinnAdMapper.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Create a {@link Stilling} based on a {@link FinnAd}.
     *
     * @param ad The {@link FinnAd}.
     * @return A {@link Stilling}, or {@code null} if the {@link FinnAd} representation was incomplete.
     */
    static Stilling toStilling(FinnAd ad) {

        if (hasIncompleteInformation(ad)) {
            return null;
        }

        Stilling s = new Stilling(
                HtmlToMarkdownConverter.parse(ad.getTitle()).trim(),
                ad.getLocation().getCity(),
                getEmployer(ad),
                getArbeidsgiveromtaleAsMarkdown(ad.getCompany().getIngress()),
                getAnnonsetekstAsMarkdown(ad.getGeneralText()),
                ad.getApplicationDeadline(),
                Kilde.FINN.toString(),
                Medium.FINN.toString(),
                ad.getUrl(),
                ad.getIdentifier()
        );

        s.getProperties().putAll(getKeyValueMap(ad));
        s.setExpires(GenericDateParser.parse(ad.getApplicationDeadline())
                .orElse(FinnDateConverter.convertDate(ad.getExpires())));

        return s;
    }

    private static boolean hasIncompleteInformation(FinnAd ad) {
        if (StringUtils.isBlank(ad.getTitle())) {
            LOG.warn("Title was empty for ad with ID {} at {}", ad.getIdentifier(), ad.getUrl());
            return true;
        }
        if (nullOrEmpty(ad.getGeneralText())) {
            LOG.warn("Text was empty for ad with ID {} at {}", ad.getIdentifier(), ad.getUrl());
            return true;
        }
        return false;
    }

    private static boolean nullOrEmpty(List<FinnAd.GeneralText> contents) {
        return contents == null ||
                contents
                        .stream()
                        .filter(Objects::nonNull)
                        .noneMatch(text -> StringUtils.isNotBlank(text.getTitle()) || StringUtils.isNotBlank(text.getValue()));
    }

    // Sometimes Company name from Finn is empty, in that case attempt to use Author name. Employer can't be blank
    private static String getEmployer(FinnAd a) {
        if (a.getCompany() != null && StringUtils.isNoneBlank(a.getCompany().getName())) {
            return a.getCompany().getName();
        } else if (a.getAuthor() != null && StringUtils.isNoneBlank(a.getAuthor().getName())) {
            return a.getAuthor().getName();
        } else {
            return "Finn";
        }
    }

    private static String getArbeidsgiveromtaleAsMarkdown(String htmlFragment) {
        return HtmlToMarkdownConverter.parse(htmlFragment);
    }

    private static String getAnnonsetekstAsMarkdown(List<FinnAd.GeneralText> generalText) {
        StringBuilder builder = new StringBuilder();
        for (FinnAd.GeneralText text : generalText) {
            if (text.getTitle() != null) {
                builder
                        .append("<h2>")
                        .append(text.getTitle())
                        .append("</h2>");
            }
            builder
                    .append(text.getValue());
        }
        return HtmlToMarkdownConverter.parse(builder.toString());
    }

    private static Map<String, String> getKeyValueMap(FinnAd ad) {
        Map<String, String> keyValueMap = new HashMap<>();
        keyValueMap.put(PropertyNames.UPDATED_DATE, ad.getUpdated());
        keyValueMap.put("submitted", ad.getDateSubmitted());
        // Expires date has been assigned to Stilling expires field
        keyValueMap.put(PropertyNames.EXTERNAL_PUBLISH_DATE, ad.getPublished());
        keyValueMap.put("edited", ad.getEdited());
        // What should be in identifier is used in Stilling.getExternalId, see constructor.
        keyValueMap.put("private", String.valueOf(ad.isPrivate()));
        keyValueMap.put(PropertyNames.LOCATION_ADDRESS, ad.getLocation().getAddress());
        keyValueMap.put(PropertyNames.LOCATION_POSTCODE, ad.getLocation().getPostalCode());
        keyValueMap.put(PropertyNames.LOCATION_CITY, ad.getLocation().getCity());
        keyValueMap.put(PropertyNames.LOCATION_COUNTRY, ad.getLocation().getCountry());
        // What should be in company.name is used in Stilling.getArbeidsgiver, see constructor.
        // What should be in company.ingress is used in Stilling.getEmployerDescription, see constructor.
        keyValueMap.put(PropertyNames.EMPLOYER_URL, ad.getCompany().getUrl());
        keyValueMap.put(PropertyNames.KEYWORDS, concatenate(ad.getKeywords()));
        keyValueMap.put(PropertyNames.OCCUPATIONS, concatenate(ad.getOccupations(), ";"));
        keyValueMap.put("managerRole", ad.getManagerRole());
        keyValueMap.put("providerId", ad.getProviderId());
        keyValueMap.put("situation", ad.getSituation());

        // TODO: Next step, standardize these values with constants or consider adding as local property
        keyValueMap.put("media.logo.url", concatenate(ad.getLogoUrlList()));
        keyValueMap.put(PropertyNames.LOGO_URL_LISTING, ad.getAuthor().getUrlListLogo());
        keyValueMap.put(PropertyNames.LOGO_URL_MAIN, ad.getAuthor().getUrlMainLogo());

        // Keys that are semantically common with other sources
        if (ad.getGeoLocation() != null) {
            keyValueMap.put(PropertyNames.GEO_LATITUDE, ad.getGeoLocation().getLatitude());
            keyValueMap.put(PropertyNames.GEO_LONGITUDE, ad.getGeoLocation().getLongitude());
        }
        keyValueMap.put(PropertyNames.SOKNADSLENKE, ad.getLinkToApply().stream().findAny().orElse(null));
        keyValueMap.put(PropertyNames.TILTREDELSE, ad.getStartDate());
        keyValueMap.put(PropertyNames.ANTALL_STILLINGER, ad.getNo_of_positions());
        keyValueMap.put(PropertyNames.REFERANSENUMMER, ad.getExternalAdId());
        keyValueMap.put(PropertyNames.VARIGHET, ad.getDuration());
        keyValueMap.put(PropertyNames.HELTIDDELTID, ad.getExtent());
        keyValueMap.put(PropertyNames.BRANSJER, concatenate(ad.getIndustry(), ";"));
        keyValueMap.put(PropertyNames.ANNONSOR, ad.getAuthor().getName());
        keyValueMap.put(PropertyNames.ADRESSE, concatenate(ad.getWorkplaces()));
        keyValueMap.put(PropertyNames.SEKTOR, ad.getSector());
        keyValueMap.put(PropertyNames.STILLINGSTITTEL, ad.getJobTitle());
        keyValueMap.put(PropertyNames.APPLICATION_EMAIL, ad.getApplicationEmail());
        keyValueMap.put(PropertyNames.APPLICATION_MAIL, ad.getApplicationAddress());
        keyValueMap.put(PropertyNames.APPLICATION_LABEL, ad.getApplicationLabel());

        keyValueMap.put(PropertyNames.KONTAKTINFO, toJsonString(ad.getContacts()));

        keyValueMap.entrySet().removeIf(entry -> entry.getValue() == null || entry.getValue().isEmpty());
        return keyValueMap;
    }

    private static String concatenate(Collection<String> elements) {
        return String.join(",", elements);
    }

    private static String concatenate(Collection<String> elements, String separator) {
        return String.join(separator, elements);
    }

    private static String toJsonString(Object o) {
        if (o == null) {
            return null;
        }

        try {
            return objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
