package no.nav.pam.annonsemottak.receivers.polaris;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.pam.annonsemottak.receivers.Kilde;
import no.nav.pam.annonsemottak.receivers.Medium;
import no.nav.pam.annonsemottak.receivers.common.PropertyNames;
import no.nav.pam.annonsemottak.receivers.polaris.model.PolarisAd;
import no.nav.pam.annonsemottak.receivers.polaris.model.PolarisCategory;
import no.nav.pam.annonsemottak.stilling.Stilling;
import no.nav.pam.annonsemottak.stilling.StillingBuilder;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

class PolarisAdMapper {

    private static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static Stilling mapToStilling(PolarisAd polarisAd) {

        Stilling stilling = new StillingBuilder(
                polarisAd.title,
                polarisAd.location.city,
                polarisAd.companyName,
                polarisAd.companyInformation,
                polarisAd.text,
                (polarisAd.applicationDeadlineDate != null) ? polarisAd.applicationDeadlineDate.toString() : polarisAd.applicationDeadlineText,
                Kilde.POLARIS.value(),
                Medium.POLARIS.value(),
                formatUrl(polarisAd.url),
                polarisAd.positionId)
                .expires(polarisAd.bookings.endDate)
                .withProperties(extractProperties(polarisAd))
                .build();

        stilling.setSystemModifiedDate(polarisAd.dateTimeModified);

        return stilling;
    }

    private static Map<String, String> extractProperties(PolarisAd polarisAd) {
        Map<String, String> properties = new HashMap<>();
        properties.put(PropertyNames.ANNONSOR, polarisAd.bookings.publication);
        properties.put(PropertyNames.EXTERNAL_PUBLISH_DATE, polarisAd.bookings.startDate.toString());
        properties.put(PropertyNames.LOCATION_CITY, polarisAd.location.city);
        properties.put(PropertyNames.LOCATION_MUNICIPAL, polarisAd.location.municipality);
        properties.put(PropertyNames.LOCATION_POSTCODE, polarisAd.location.postal);
        properties.put(PropertyNames.LOCATION_ADDRESS, polarisAd.location.street);
        properties.put(PropertyNames.GEO_LATITUDE, polarisAd.location.latitude);
        properties.put(PropertyNames.GEO_LONGITUDE, polarisAd.location.longitude);

        properties.put(PropertyNames.TILTREDELSE,
                (StringUtils.isNotBlank(polarisAd.accessionText)) ? polarisAd.accessionText : polarisAd.accessionDate.toString());
        properties.put(PropertyNames.APPLICATION_LABEL, polarisAd.applicationMarked);
        properties.put(PropertyNames.APPLICATION_EMAIL, polarisAd.applicationRecipientEmail);
        properties.put(PropertyNames.LOGO_URL_MAIN, formatUrl(polarisAd.companyLogo));
        properties.put(PropertyNames.EMPLOYER_URL, polarisAd.companyWebsite);
        properties.put(PropertyNames.CREATED_DATE, polarisAd.dateTimeCreated.toString());
        properties.put(PropertyNames.UPDATED_DATE, polarisAd.dateTimeModified.toString());
        properties.put(PropertyNames.OCCUPATIONS, polarisAd.employmentLevel);
        properties.put(PropertyNames.VARIGHET, polarisAd.employmentType);
        properties.put(PropertyNames.SOKNADSLENKE, polarisAd.externalSystemUrl);
        properties.put(PropertyNames.KEYWORDS, polarisAd.keywords);
        properties.put("salary", polarisAd.salary);
        properties.put(PropertyNames.ANTALL_STILLINGER, polarisAd.vacancies.toString());
        properties.put(PropertyNames.SEKTOR, polarisAd.sector);
        properties.put(PropertyNames.BRANSJER, categoriesAsString(polarisAd.categories));
        properties.put(PropertyNames.KONTAKTINFO, toJsonString(polarisAd.contacts));

        properties.values().removeIf(StringUtils::isBlank);
        return properties;
    }

    private static String categoriesAsString(List<PolarisCategory> list) {
        StringJoiner sj = new StringJoiner(";");

        list.stream().forEach(c -> {
            sj.add(c.name);
            c.subCategories.stream().forEach(sc -> sj.add(sc.name));
        });

        return sj.toString();
    }

    private static String formatUrl(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }

        return value.replaceFirst("//", "");
    }

    //TODO: REFACTOR all objectMappers in static mappers
    private static String toJsonString(Object o) {
        if (o == null) {
            return null;
        }

        try {
            return OBJECT_MAPPER.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
