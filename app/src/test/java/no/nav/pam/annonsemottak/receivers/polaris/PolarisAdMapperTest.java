package no.nav.pam.annonsemottak.receivers.polaris;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.pam.annonsemottak.receivers.common.PropertyNames;
import no.nav.pam.annonsemottak.receivers.polaris.model.PolarisAd;
import no.nav.pam.annonsemottak.stilling.Stilling;
import org.assertj.core.api.SoftAssertions;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

public class PolarisAdMapperTest {

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void should_map_correctly_to_stilling() throws IOException {

        File file = new File("src/test/resources/polaris.samples/result_with_single_ad.json");
        List<PolarisAd> polarisAds = objectMapper.readValue(file, new TypeReference<List<PolarisAd>>() {
        });

        assertEquals(1, polarisAds.size());
        PolarisAd ad = polarisAds.get(0);

        Stilling stilling = PolarisAdMapper.mapToStilling(ad);

        SoftAssertions softAssert = new SoftAssertions();
        assertThat(stilling.getExternalId()).isEqualTo("9564");
        assertThat(stilling.getJobDescription()).isEqualTo("<p style=\"vertical-align: middle; margin-bottom: 15px; padding: 0px; border: 0px; font-variant-numeric: inherit; font-variant-east-asian: inherit; font-stretch: inherit; font-size: 15px; line-height: inherit; font-family: PFBeauSansProLight, Tahoma, Arial, Helvetica, Verdana, sans-serif; color: rgb(68, 68, 68);\">Måløy legekontor har ledig 1 fastlegehjemmel fra 01.03.2019.</p><p style=\"vertical-align: middle; margin-bottom: 15px; padding: 0px; border: 0px; font-variant-numeric: inherit; font-variant-east-asian: inherit; font-stretch: inherit; font-size: 15px; line-height: inherit; font-family: PFBeauSansProLight, Tahoma, Arial, Helvetica, Verdana, sans-serif; color: rgb(68, 68, 68);\">Legekontoret er godt etablert i kommunens rådhus sentralt i Måløy.</p><p style=\"vertical-align: middle; margin-bottom: 15px; padding: 0px; border: 0px; font-variant-numeric: inherit; font-variant-east-asian: inherit; font-stretch: inherit; font-size: 15px; line-height: inherit; font-family: PFBeauSansProLight, Tahoma, Arial, Helvetica, Verdana, sans-serif; color: rgb(68, 68, 68);\">Antall innbyggere i Vågsøy er nå ca. 6000. Fra 01.01.2020 skal Vågsøy slå seg sammen med Flora kommune til Kinn kommune. Kinn kommune vil ha ca. 17 500 innbyggere.</p><p style=\"vertical-align: middle; margin-bottom: 15px; padding: 0px; border: 0px; font-variant-numeric: inherit; font-variant-east-asian: inherit; font-stretch: inherit; font-size: 15px; line-height: inherit; font-family: PFBeauSansProLight, Tahoma, Arial, Helvetica, Verdana, sans-serif; color: rgb(68, 68, 68);\">Kontoret har nye og moderne lokale som ble tatt i bruk i 2008.<br style=\"vertical-align: middle;\">I rådhuset finner du også: Helsestasjon, barnevern, Nav, sentraladministrasjon og servicekontor.<br style=\"vertical-align: middle;\">Kommunen har 8 fastlegehjemler + 1 turnuskandidat i kontoret. Alderssammensetning på legene i kontoret er fra 29 til 69 år. Listestørrelse 900 pasienter.<br style=\"vertical-align: middle;\">Kontoret har tilsatt leger og hjelpepersonell med lang erfaring og bred faglig kompetanse.<br style=\"vertical-align: middle;\">Arbeidsmiljøet ved kontoret er godt og preget av stå-på-vilje og arbeidsglede.<br style=\"vertical-align: middle;\">Senteret nytter Infodoc elektronisk journalsystem og Melin betalingssentral.</p><p style=\"vertical-align: middle; margin-bottom: 15px; padding: 0px; border: 0px; font-variant-numeric: inherit; font-variant-east-asian: inherit; font-stretch: inherit; font-size: 15px; line-height: inherit; font-family: PFBeauSansProLight, Tahoma, Arial, Helvetica, Verdana, sans-serif; color: rgb(68, 68, 68);\"><strong style=\"vertical-align: middle; margin: 0px; padding: 0px; border: 0px; font-style: inherit; font-variant: inherit; font-stretch: inherit; font-size: inherit; line-height: inherit; font-family: inherit;\">Til legehjemmelen ligger:</strong></p><ul style=\"vertical-align: middle; color: rgb(68, 68, 68); font-family: PFBeauSansProLight, Tahoma, Arial, Helvetica, Verdana, sans-serif; font-size: 15px;\"><li style=\"vertical-align: middle; margin: 0px; padding: 0px; border: 0px; font: inherit;\">Kommunen er tilsluttet interkommunal legevakt lokalisert til Nordfjord sjukehus kveld, natt og helg (ca. 45 min kjøring fra Måløy).</li><li style=\"vertical-align: middle; margin: 0px; padding: 0px; border: 0px; font: inherit;\">Interkommunal legevakt med ca 3 vakter pr. mnd.</li><li style=\"vertical-align: middle; margin: 0px; padding: 0px; border: 0px; font: inherit;\">Tilplikting til kommunal bistilling etter gjeldende avtaler.</li><li style=\"vertical-align: middle; margin: 0px; padding: 0px; border: 0px; font: inherit;\">Kommunal legevakt dagtid 08.00 - 16.00 mandag til fredag.</li></ul><p style=\"vertical-align: middle; margin-bottom: 15px; padding: 0px; border: 0px; font-variant-numeric: inherit; font-variant-east-asian: inherit; font-stretch: inherit; font-size: 15px; line-height: inherit; font-family: PFBeauSansProLight, Tahoma, Arial, Helvetica, Verdana, sans-serif; color: rgb(68, 68, 68);\"><strong style=\"vertical-align: middle; margin: 0px; padding: 0px; border: 0px; font-style: inherit; font-variant: inherit; font-stretch: inherit; font-size: inherit; line-height: inherit; font-family: inherit;\">Vi ønsker oss søkere med:</strong></p><ul style=\"vertical-align: middle; color: rgb(68, 68, 68); font-family: PFBeauSansProLight, Tahoma, Arial, Helvetica, Verdana, sans-serif; font-size: 15px;\"><li style=\"vertical-align: middle; margin: 0px; padding: 0px; border: 0px; font: inherit;\">Interesse for allmennmedisin.</li><li style=\"vertical-align: middle; margin: 0px; padding: 0px; border: 0px; font: inherit;\">Gyldig norsk autorisasjon – norsk turnustjeneste.</li><li style=\"vertical-align: middle; margin: 0px; padding: 0px; border: 0px; font: inherit;\">Gode norskkunnskaper både skriftlig og muntlig.</li></ul><p style=\"vertical-align: middle; margin-bottom: 15px; padding: 0px; border: 0px; font-variant-numeric: inherit; font-variant-east-asian: inherit; font-stretch: inherit; font-size: 15px; line-height: inherit; font-family: PFBeauSansProLight, Tahoma, Arial, Helvetica, Verdana, sans-serif; color: rgb(68, 68, 68);\"><strong style=\"vertical-align: middle; margin: 0px; padding: 0px; border: 0px; font-style: inherit; font-variant: inherit; font-stretch: inherit; font-size: inherit; line-height: inherit; font-family: inherit;\">Vi tilbyr:</strong></p><ul style=\"vertical-align: middle; color: rgb(68, 68, 68); font-family: PFBeauSansProLight, Tahoma, Arial, Helvetica, Verdana, sans-serif; font-size: 15px;\"><li style=\"vertical-align: middle; margin: 0px; padding: 0px; border: 0px; font: inherit;\">Fleksibilitet.</li><li style=\"vertical-align: middle; margin: 0px; padding: 0px; border: 0px; font: inherit;\">Varierte primærlegeoppgaver.</li><li style=\"vertical-align: middle; margin: 0px; padding: 0px; border: 0px; font: inherit;\">Godt tverrfaglig samarbeid.</li><li style=\"vertical-align: middle; margin: 0px; padding: 0px; border: 0px; font: inherit;\">En framtidsretta arbeidsplass i utvikling.</li><li style=\"vertical-align: middle; margin: 0px; padding: 0px; border: 0px; font: inherit;\">Tilrettelegging ved spesialistutdanning.</li></ul><p style=\"vertical-align: middle; margin-bottom: 15px; padding: 0px; border: 0px; font-variant-numeric: inherit; font-variant-east-asian: inherit; font-stretch: inherit; font-size: 15px; line-height: inherit; font-family: PFBeauSansProLight, Tahoma, Arial, Helvetica, Verdana, sans-serif; color: rgb(68, 68, 68);\"><strong style=\"vertical-align: middle; margin: 0px; padding: 0px; border: 0px; font-style: inherit; font-variant: inherit; font-stretch: inherit; font-size: inherit; line-height: inherit; font-family: inherit;\">Avtale fastlege:</strong></p><p style=\"vertical-align: middle; margin-bottom: 15px; padding: 0px; border: 0px; font-variant-numeric: inherit; font-variant-east-asian: inherit; font-stretch: inherit; font-size: 15px; line-height: inherit; font-family: PFBeauSansProLight, Tahoma, Arial, Helvetica, Verdana, sans-serif; color: rgb(68, 68, 68);\">Stillingen krever ingen investeringer. Driftsavtalen med kommunen er en såkalt null-avtale der kommunen drifter praksis i bytte mot per capita tilskuddet.</p><p style=\"vertical-align: middle; margin-bottom: 15px; padding: 0px; border: 0px; font-variant-numeric: inherit; font-variant-east-asian: inherit; font-stretch: inherit; font-size: 15px; line-height: inherit; font-family: PFBeauSansProLight, Tahoma, Arial, Helvetica, Verdana, sans-serif; color: rgb(68, 68, 68);\">Lønn for offentlig arbeid etter individuell avtale.</p><p style=\"vertical-align: middle; margin-bottom: 15px; padding: 0px; border: 0px; font-variant-numeric: inherit; font-variant-east-asian: inherit; font-stretch: inherit; font-size: 15px; line-height: inherit; font-family: PFBeauSansProLight, Tahoma, Arial, Helvetica, Verdana, sans-serif; color: rgb(68, 68, 68);\">Vi gjør oppmerksom på at etter offentleglova § 25 kan opplysninger om søkerne bli offentlige sjøl om søker har bedt om ikke å bli ført opp på søkerliste. <strong style=\"vertical-align: middle; margin: 0px; padding: 0px; border: 0px; font-style: inherit; font-variant: inherit; font-stretch: inherit; font-size: inherit; line-height: inherit; font-family: inherit;\">Søkere som ønsker unntak fra offentlig søkerliste må begrunne dette særskilt i søknadsteksten i søknaden.</strong></p><p style=\"vertical-align: middle; margin-bottom: 15px; padding: 0px; border: 0px; font-variant-numeric: inherit; font-variant-east-asian: inherit; font-stretch: inherit; font-size: 15px; line-height: inherit; font-family: PFBeauSansProLight, Tahoma, Arial, Helvetica, Verdana, sans-serif; color: rgb(68, 68, 68);\"><strong style=\"vertical-align: middle; margin: 0px; padding: 0px; border: 0px; font-style: inherit; font-variant: inherit; font-stretch: inherit; font-size: inherit; line-height: inherit; font-family: inherit;\">Er dette interessant for deg? Vil du vite mer om kommunen og vi som jobber ved kontoret kan du se denne lille filmen som vi har laget: </strong><a href=\"https://www.youtube.com/watch?v=i8P_HnlZ0FU\" style=\"vertical-align: middle; margin: 0px; padding: 0px; border: 0px; font: inherit; color: rgb(0, 122, 202);\"><strong style=\"vertical-align: middle; margin: 0px; padding: 0px; border: 0px; font-style: inherit; font-variant: inherit; font-stretch: inherit; font-size: inherit; line-height: inherit; font-family: inherit;\"><span style=\"vertical-align: middle; margin: 0px; padding: 0px; border: 0px; font: inherit; text-decoration-line: underline;\">Doc in the city</span></strong></a></p>");
        assertThat(stilling.getTitle()).isEqualTo("Fastlege");
        assertThat(stilling.getUrl()).isEqualTo("www.fjordabladet.no/stillingledig/#/landingPage/landingpage/9564");
        assertThat(stilling.getDueDate()).isEqualTo("2018-09-25T00:00");
        assertThat(stilling.getEmployerDescription()).isEqualTo("<p><span style=\"color: rgb(62, 56, 50); font-family: Arial, Helvetica, sans-serif; font-size: 12.25px;\">Vågsøy kommune er ei naturperle som ligg ytst i Nordfjord mellom Ålesund og Bergen. Næringslivet er aktivt med hovudvekt på marine næringar. Kommunen har eit godt utbygd barnehage- og skuletilbod, kultur- og idrettstilbod og eit fatastisk turterreng. Kommunen har omlag 6100 innbyggjarar. Måløy er kommunesenter. For meir informasjon om regionen sjå www.nordfjord.no.</span><br></p>");
        assertThat(stilling.getArbeidsgiver().get().asString()).isEqualTo("Vågsøy Kommune");
        assertThat(stilling.getPlace()).isEqualTo("Selbu");
        assertThat(stilling.getExpires()).isEqualTo("2018-11-30T23:59:59");

        assertThat(stilling.getProperties().get(PropertyNames.EXTERNAL_PUBLISH_DATE)).isEqualTo("2018-09-25T00:00");
        assertThat(stilling.getProperties().get(PropertyNames.TILTREDELSE)).isEqualTo("test1");
        assertThat(stilling.getProperties().get(PropertyNames.APPLICATION_LABEL)).isEqualTo("test1");
        assertThat(stilling.getProperties().get(PropertyNames.APPLICATION_EMAIL)).isEqualTo("test@nav.no");
        assertThat(stilling.getProperties().get(PropertyNames.EMPLOYER_URL)).isEqualTo("www.vagsoy.kommune.no/");
        assertThat(stilling.getProperties().get(PropertyNames.LOGO_URL_MAIN)).isEqualTo("media.webassistenten.no/api/image/get/jobs/75706C6F6164735C356166613032356331303232626632612E706E67");
        assertThat(stilling.getProperties().get(PropertyNames.CREATED_DATE)).isEqualTo("2018-09-25T08:44:56.083");
        assertThat(stilling.getProperties().get(PropertyNames.UPDATED_DATE)).isEqualTo("2018-09-25T09:06:10.077");
        assertThat(stilling.getProperties().get(PropertyNames.OCCUPATIONS)).isNull();
        assertThat(stilling.getProperties().get(PropertyNames.VARIGHET)).isEqualTo("Fast");
        assertThat(stilling.getProperties().get(PropertyNames.SOKNADSLENKE)).isEqualTo("https://vagsoy.easycruit.com/vacancy/2149635/140851");
        assertThat(stilling.getProperties().get(PropertyNames.KEYWORDS)).isNull();
        assertThat(stilling.getProperties().get("salary")).isNull();
        assertThat(stilling.getProperties().get(PropertyNames.SEKTOR)).isEqualTo("Offentlig");
        assertThat(stilling.getProperties().get(PropertyNames.ANTALL_STILLINGER)).isEqualTo("1");
        assertThat(stilling.getProperties().get(PropertyNames.LOCATION_ADDRESS)).isEqualTo("Mebondvegen  ");
        assertThat(stilling.getProperties().get(PropertyNames.LOCATION_MUNICIPAL)).isEqualTo("Selbu");
        assertThat(stilling.getProperties().get(PropertyNames.LOCATION_POSTCODE)).isEqualTo("7580");
        assertThat(stilling.getProperties().get(PropertyNames.GEO_LONGITUDE)).isEqualTo("11.03573689999996");
        assertThat(stilling.getProperties().get(PropertyNames.GEO_LATITUDE)).isEqualTo("63.22019390000001");
        assertThat(stilling.getProperties().get(PropertyNames.ANNONSOR)).isEqualTo("Nordfjordsamkøyringa");

        assertThat(stilling.getProperties().get(PropertyNames.BRANSJER)).isEqualTo("Helse / Sosial;Lege / Tannlege");
        assertThat(stilling.getProperties().get(PropertyNames.KONTAKTINFO)).isEqualTo(  "[{\"" +
                "title\":\"Kommunelege\",\"email\":\"trond.inselseth@vagsoy.kommune.no\",\"mobile\":\"975 15 105\",\"name\":" +
                "\"Testname Testlastname\",\"phone\":\"000 15 105\"},{\"title\":null,\"email\":null,\"mobile\":\"979 60 915\",\"name\":" +
                "\"Jeanette Jensen\",\"phone\":null}]");

        softAssert.assertAll();
    }
}
