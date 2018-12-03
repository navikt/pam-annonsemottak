package no.nav.pam.annonsemottak.receivers.polaris;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.pam.annonsemottak.receivers.Kilde;
import no.nav.pam.annonsemottak.receivers.Medium;
import no.nav.pam.annonsemottak.receivers.common.PropertyNames;
import no.nav.pam.annonsemottak.receivers.polaris.model.PolarisAd;
import no.nav.pam.annonsemottak.receivers.polaris.model.PolarisCategory;
import no.nav.pam.annonsemottak.stilling.Stilling;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.StringJoiner;

public class PolarisAdMapper {

    private static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static Stilling mapToStilling(PolarisAd polarisAd) {

        Stilling stilling = new Stilling(
                polarisAd.title,
                polarisAd.location.city,
                polarisAd.companyName,
                polarisAd.companyInformation,
                polarisAd.text,
                (polarisAd.applicationDeadlineDate != null) ? polarisAd.applicationDeadlineDate.toString() : polarisAd.applicationDeadlineText,
                Kilde.POLARIS.value(),
                Medium.POLARIS.value(),
                polarisAd.url,
                polarisAd.positionId
        );

        stilling.setExpires(polarisAd.bookings.endDate);
        stilling.setSystemModifiedDate(polarisAd.dateTimeModified);

        stilling.getProperties().put(PropertyNames.ANNONSOR, polarisAd.bookings.publication);
        stilling.getProperties().put(PropertyNames.EXTERNAL_PUBLISH_DATE, polarisAd.bookings.startDate.toString());
        stilling.getProperties().put(PropertyNames.LOCATION_CITY, polarisAd.location.city);
        stilling.getProperties().put(PropertyNames.LOCATION_MUNICIPAL, polarisAd.location.municipality);
        stilling.getProperties().put(PropertyNames.LOCATION_POSTCODE, polarisAd.location.postal);
        stilling.getProperties().put(PropertyNames.LOCATION_ADDRESS, polarisAd.location.street);
        stilling.getProperties().put(PropertyNames.GEO_LATITUDE, polarisAd.location.latitude);
        stilling.getProperties().put(PropertyNames.GEO_LONGITUDE, polarisAd.location.longitude);

        stilling.getProperties().put(PropertyNames.TILTREDELSE,
                (StringUtils.isNotBlank(polarisAd.accessionText)) ? polarisAd.accessionText : polarisAd.accessionDate.toString());
        stilling.getProperties().put(PropertyNames.APPLICATION_LABEL, polarisAd.applicationMarked);
        stilling.getProperties().put(PropertyNames.APPLICATION_EMAIL, polarisAd.applicationRecipientEmail);
        stilling.getProperties().put(PropertyNames.LOGO_URL_MAIN, polarisAd.companyLogo);
        stilling.getProperties().put(PropertyNames.EMPLOYER_URL, polarisAd.companyWebsite);
        stilling.getProperties().put(PropertyNames.CREATED_DATE, polarisAd.dateTimeCreated.toString());
        stilling.getProperties().put(PropertyNames.UPDATED_DATE, polarisAd.dateTimeModified.toString());
        stilling.getProperties().put(PropertyNames.OCCUPATIONS, polarisAd.employmentLevel);
        stilling.getProperties().put(PropertyNames.VARIGHET, polarisAd.employmentType);
        stilling.getProperties().put(PropertyNames.SOKNADSLENKE, polarisAd.externalSystemUrl);
        stilling.getProperties().put(PropertyNames.KEYWORDS, polarisAd.keywords);
        stilling.getProperties().put("salary", polarisAd.salary);
        stilling.getProperties().put(PropertyNames.ANTALL_STILLINGER, polarisAd.vacancies.toString());
        stilling.getProperties().put(PropertyNames.SEKTOR, polarisAd.sector);
        stilling.getProperties().put(PropertyNames.BRANSJER, categoriesAsString(polarisAd.categories));
        stilling.getProperties().put(PropertyNames.KONTAKTINFO, toJsonString(polarisAd.contacts));

        stilling.getProperties().values().removeIf(StringUtils::isBlank);


        return stilling;
    }

    private static String categoriesAsString(List<PolarisCategory> list) {
        StringJoiner sj = new StringJoiner(";");

        list.stream().forEach(c -> {
            sj.add(c.name);
            c.subCategories.stream().forEach(sc -> sj.add(sc.name));
        });

        return sj.toString();
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
