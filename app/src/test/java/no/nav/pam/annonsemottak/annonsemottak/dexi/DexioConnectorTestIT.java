package no.nav.pam.annonsemottak.annonsemottak.dexi;

import no.nav.pam.annonsemottak.stilling.Stilling;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static no.nav.pam.annonsemottak.stilling.StillingTestdataBuilder.stilling;

@RunWith(SpringRunner.class)
@DataJpaTest
@Rollback
@Transactional
public class DexioConnectorTestIT {

    @Autowired
    DexiConnector dexiConnector;

    private String annonsetekst = "Annonsetekst";
    private String annonsetittel = "Annonsetittel";
    private String annonseURL = "AnnonseURL";
    private String annonsor = "Annonsor";
    private String antallStillinger = "Antall stillinger";
    private String arbeidsgiver = "Arbeidsgiver";
    private String arbeidsgiveromtale = "Arbeidsgiveromtale";
    private String arbeidspraak = "Arbeidspraak";
    private String arbeidssted = "Arbeidssted";
    private String bransjer = "Bransjer";
    private String fagfelt = "Fagfelt";
    private String fylke = "Fylke";
    private String heltidDeltid = "HeltidDeltid";
    private String kilde = "Kilde";
    private String kontaktinfo = "Kontaktinfo";
    private String mottattDato = "Mottatt dato";
    private String referansenummer = "Referansenummer";
    private String soknadsfrist = "Soknadsfrist";
    private String soknadslenke = "Soknadslenke";
    private String tiltredelse = "Tiltredelse";
    private String stillingstype = "Type stilling";

    @BeforeClass
    public static void beforeClass() {
        System.setProperty("dexi.url", "https://api.dexi.io");
    }

    @AfterClass
    public static void afterClass() {
        System.clearProperty("dexi.url");
    }

    @Test
    public void testAPI()
            throws IOException {
        List<DexiConfiguration> robots = dexiConnector.getConfigurations("produksjon");
        System.out.println(robots.size());
    }

    @Test
    public void testConnectorHero()
            throws IOException {
        List<Map<String, String>> latestResultsForJob = dexiConnector.getLatestResultForJobID("72441a26-4d72-4cae-b348-7b5efe5714d0");
        List<Stilling> stillinger = latestResultsForJob.stream().map(this::toStilling).collect(Collectors.toList());


        for (String key : latestResultsForJob.get(0).keySet()) {
            System.out.println(key);
        }
    }

    @Test
    public void testConnectorBergenKommunue()
            throws IOException {
        List<Map<String, String>> latestResultsForJob = dexiConnector.getLatestResultForJobID("92aca435-0df2-4b05-8150-d9f4b84ff087");
        List<Stilling> stillings = latestResultsForJob.stream().map(this::toStilling).collect(Collectors.toList());

        for (Stilling stilling : stillings) {
            System.out.println(stilling);
        }
        for (String key : latestResultsForJob.get(0).keySet()) {
            System.out.println(key);
        }
    }

    private Stilling toStilling(Map<String, String> map) {

        Map<String, String> props = map.entrySet().stream()
                .filter(m -> !Arrays.asList(
                        annonsetittel,
                        arbeidssted,
                        arbeidsgiver,
                        arbeidsgiveromtale, annonsetekst, kilde, soknadsfrist).contains(m.getKey()))
                .filter(m -> m.getValue() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return stilling()
                .tittel(map.get(annonsetittel))
                .arbeidssted(map.get(arbeidssted))
                .arbeidsgiver(map.get(arbeidsgiver))
                .arbeidsgiverbeskrivelse(map.get(arbeidsgiveromtale))
                .stillingstekst(map.get(annonsetekst))
                .medium(map.get(kilde))
                .utløpsdato(map.get(soknadsfrist))
                .properties(props)
                .build();
    }

    @Test
    public void testRuns()
            throws IOException {
        System.out.println(dexiConnector.getRuns());
    }

    @Test
    public void parseJson() {
        String x = "{\"headers\":[\"Tittel\",\"Dato\",\"Sted\",\"Arbeidsgiver\",\"error\"],\"rows\":[[\"Direktør konsept og plan\",\"03. nov 2016\",\"Oslo\",\"Jernbaneverket\",null],[\"Prosjektøkonom - Strategi og utviklingsavdelingen\",\"07. nov 2016\",\"Oslo\",\"Statsbygg\",null],[\"Dokumentkontroller\",\"06. nov 2016\",\"Akershus,Oslo,Østfold\",\"Jernbaneverket\",null],[\"Enhetsleder Vann og avløp - Kommunal drift \",\"27. okt 2016\",\"Nannestad\",\"Nannestad kommune\",null],[\"Ingeniør VA og vei - Kommunal drift \",\"31. okt 2016\",\"Nannestad \",\"Nannestad kommune\",null],[\"Serviceingeniør Automasjon\",\"10. nov 2016\",\"GK Inneklima Oslo\",\"GK Norge\",null],[\"Serviceleder Tønsberg\",\"13. nov 2016\",\"GK Inneklima Tønsberg\",\"GK Norge\",null],[\"Fiko søker dyktig webutvikler\",\"01. des 2016\",\"Vest-Agder\",\"Experis\",null],[\"Vil du jobbe i Energi Norge?\",\"04. nov 2016\",\"Majorstuen, Oslo\",\"Energi Norge AS\",null],[\"Etterretningstjenesten søker en dyktig medarbeider til stilling innen kommunikasjonsinnsamling\",\"13. nov 2016\",\"Oslo\",\"Etterretningstjenesten\",null],[\"Erfaren geotekniker Bergen\",\"20. nov 2016\",\"Bergen\",\"Sweco Norge\",null],[\"Discipline Engineer - Mechanical\",\"26. okt 2016\",\"Hordaland\",\"Experis\",null],[\"Specialist Antigen (Product development/production)\",\"02. nov 2016\",\"Overhalla\",\"PHARMAQ\",null],[\"Systemkonsulent Eiendomsforvaltning\",\"11. nov 2016\",\"Sandvika\",\"Norconsult\",null],[\"Byggeleder Prosjekt Tromsø\",\"10. nov 2016\",\"Tromsø\",\"Statens vegvesen\",null],[\"Bli med på å utvikle et system for overvåking av isbreer med satellitt i Norge!\",\"06. nov 2016\",\"Oslo\",\"Norges vassdrags- og energidirektorat (NVE)\",null],[\"Senior-/spesialrådgiver forskningsinfrastruktur\",\"11. nov 2016\",\"Oslo\",\"Forskningsrådet\",null],[\"Overingeniør/senioringeniør\",\"28. okt 2016\",\"Hamar\",\"Statens vegvesen\",null],[\"Senioringeniør til drift av sikkerhetsmekanismer \",\"13. nov 2016\",\"Oslo\",\"Etterretningstjenesten\",null],[\"Senioringeniør, ansvarlig for IT-testmiljø\",\"13. nov 2016\",\"Oslo\",\"Etterretningstjenesten\",null],[\"Pexip søker junior fullstack utvikler!\",null,\"Lysaker\",\"Academic Work\",null],[\"Pexip ser etter senior fullstack utvikler!\",null,\"Lysaker\",\"Academic Work\",null],[\"Systemutvikler \",\"13. nov 2016\",\"Oslo\",\"Etterretningstjenesten\",null],[\"Etterretningstjenesten søker rådgiver \",\"13. nov 2016\",\"Kirkenes\",\"Etterretningstjenesten\",null],[\"Vi søker kontorsjef til å lede innsamlingsoperasjoner mot det digitale rom \",\"13. nov 2016\",\"Oslo\",\"Etterretningstjenesten\",null]],\"totalRows\":25}\n";

    }

}