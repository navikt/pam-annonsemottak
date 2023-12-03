package no.nav.pam.annonsemottak.markdown;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class MarkdownToHtmlConverterTest {

    @Test
    public void parse_sampleMarkdown() {
        String markdown = "***Vil du være med å definere fremtiden innen funksjonelle modulbygg?***\n" +
                "\n" +
                "**Senior Prosjektleder**\n" +
                "\n" +
                "Over lengre tid har vi bygd opp en sterk portefølje i Midt-Norge innen modulbasert bygging. Vi har kunder og inngåtte rammeavtaler med de store private aktører og offentlige byggherrer. Våre konsepter møter morgendagens krav til funksjon, design og byggeforskrifter. Kort byggetid, flyttbarhet, lave kostnader, fleksibilitet, skreddersøm, miljøvennlige produkter, innendørs produksjon hos våre underleverandører mm, gir våre kunder mange fordeler ved å velge løsninger fra oss.\n" +
                "\n" +
                "++Våre konsepter:++\n" +
                "\n" +
                "*Living* - hoteller, leiligheter og studenthybler.\n" +
                "\n" +
                "*Public* - skoler, barnehager og omsorgsboliger.\n" +
                "\n" +
                "*Industry* - kontorer og entreprenørmoduler.\n" +
                "\n" +
                "*Camp* - kontorer, borigger og kantine.\n" +
                "\n" +
                "Vår oppdragsmengde er økende og vi søker etter en forretningsorientert senior prosjektleder som kan bli med å ta ansvar for videre utvikling av våre mange spennende prosjekter i Midt-Norge. Du kommer i nær dialog med våre kunder, og må være god på å forstå kundebehov og drivende dyktig på oppfølging i prosjekter. På avdelingen i Trondheim vil du jobbe tett sammen med vår prosjektleder og byggeleder, og rapportere til prosjektsjef i Norge. Vi er organisert slik at du henter støtteressurser i selskapet ved behov innen f.eks. tekniske løsninger og krav, anbud/ tilbudsarbeid, økonomi, HR, kontrakt etc.";
        String expected = "<p><em><strong>Vil du være med å definere fremtiden innen funksjonelle modulbygg?</strong></em></p>\n" +
                "<p><strong>Senior Prosjektleder</strong></p>\n" +
                "<p>Over lengre tid har vi bygd opp en sterk portefølje i Midt-Norge innen modulbasert bygging. Vi har kunder og inngåtte rammeavtaler med de store private aktører og offentlige byggherrer. Våre konsepter møter morgendagens krav til funksjon, design og byggeforskrifter. Kort byggetid, flyttbarhet, lave kostnader, fleksibilitet, skreddersøm, miljøvennlige produkter, innendørs produksjon hos våre underleverandører mm, gir våre kunder mange fordeler ved å velge løsninger fra oss.</p>\n" +
                "<p>&#43;&#43;Våre konsepter:&#43;&#43;</p>\n" +
                "<p><em>Living</em> - hoteller, leiligheter og studenthybler.</p>\n" +
                "<p><em>Public</em> - skoler, barnehager og omsorgsboliger.</p>\n" +
                "<p><em>Industry</em> - kontorer og entreprenørmoduler.</p>\n" +
                "<p><em>Camp</em> - kontorer, borigger og kantine.</p>\n" +
                "<p>Vår oppdragsmengde er økende og vi søker etter en forretningsorientert senior prosjektleder som kan bli med å ta ansvar for videre utvikling av våre mange spennende prosjekter i Midt-Norge. Du kommer i nær dialog med våre kunder, og må være god på å forstå kundebehov og drivende dyktig på oppfølging i prosjekter. På avdelingen i Trondheim vil du jobbe tett sammen med vår prosjektleder og byggeleder, og rapportere til prosjektsjef i Norge. Vi er organisert slik at du henter støtteressurser i selskapet ved behov innen f.eks. tekniske løsninger og krav, anbud/ tilbudsarbeid, økonomi, HR, kontrakt etc.</p>\n";
        assertEquals(expected, MarkdownToHtmlConverter.parse(markdown));
    }

    @Test
    public void markdownBulletpointsShouldBecomeProperHtmlBulletpoints() {
        String markdown = "#### Arbeidsoppgaver\n" +
                "\n" +
                "* Undervisning. Individuell og /eller gruppeundervisning\n" +
                "\n" +
                "#### Kvalifikasjoner\n" +
                "\n" +
                "* Minimum 3 årig høyskole-/universitetsutdanning innenfor musikk, dans og teater.\n" +
                "* Dersom du har tatt hele eller deler av utdanningen din i utlandet, må denne godkjennes av NOKUT.\n" +
                "\n" +
                "#### Utdanningsretning\n" +
                "\n" +
                "* Pedagogikk\n" +
                "* Utdanningstittel: Musikk og kulturskolelærer\n" +
                "\n" +
                "#### Utdanningsnivå\n" +
                "\n" +
                "* Høyskole / Universitet, årsstudie / grunnfag\n" +
                "* Høyskole / Universitet, Diplom- / Bachelorgrad\n" +
                "\n" +
                "#### Personlige egenskaper\n" +
                "\n" +
                "* Ansvarsbevisst og selvstendig.\n" +
                "* God kommunikasjonsevne med barn og unge.\n" +
                "\n" +
                "#### Språk\n" +
                "\n" +
                "* Norsk\n" +
                "\n" +
                "#### Vi tilbyr\n" +
                "\n" +
                "* Lønn ihht. tariff.\n" +
                "* Pensjonsordning\n" +
                "\n";
        String html = MarkdownToHtmlConverter.parse(markdown);
        assertThat(html, containsString("<li>Undervisning"));
        assertThat(html, containsString("<li>Minimum"));
        assertThat(html, containsString("<li>Dersom"));
        assertThat(html, containsString("<li>Pedagogikk"));
        assertThat(html, containsString("<li>Utdanningstittel"));
        assertThat(html, containsString("<li>Høyskole"));
        assertThat(html, containsString("<li>Ansvarsbevisst"));
        assertThat(html, containsString("<li>God"));
        assertThat(html, containsString("<li>Norsk"));
        assertThat(html, containsString("<li>Lønn"));
        assertThat(html, containsString("<li>Pensjonsordning"));
    }

    @Test
    public void markdownBulletpointsDidNotBecomeCorrectHtmlUnorderedLists() {
        String markdown = "GK Elektro har fokus på et sterkt fagmiljø, godt samarbeid og en positiv hverdag. De ansatte i GK Elektro jobber tett sammen for felles mål og arbeidsmiljøet er lystbetont og uformelt. Avdelingen betjener entrepriseprosjekter i hele Trøndelag område. Du må være fagorientert og trives med å jobbe i et flerfaglig teknisk miljø sammen med øvrige GK-fag.\n" +
                "\n" +
                "Som avdelingsleder i GK Elektro har du det overordnede ansvaret for å organisere, utvikle og ivareta den daglige driften i avdelingen, samt være en motivator for avdelingens ansatte.\n" +
                "\n" +
                "I mai 2017 flytter vi inn i nye flotte lokaler hvor vi blir samlokalisert med GK Inneklima og GK Rør for et enda bedre tverrfaglig samarbeid.\n" +
                "\n" +
                "**Viktigste arbeidsoppgaver:**\n" +
                "* Personal, resultat og budsjettansvar\n" +
                "* Lede avdelingen iht. bedriftens verdier, handlingsplaner og budsjett\n" +
                "* Utarbeide forslag til og evaluere operative mål, herunder handlingsplan, budsjett, bemanningsplan og markedsplan\n" +
                "* Ansvarlig for opplæring i GKs prosess- og produksjonsrutiner (VIP og prosjektoppfølging)\n" +
                "* Markedsføre avdelingen samt skape gode og tillitsfulle relasjoner med kunder og leverandører\n" +
                "* Ha tett samarbeid med prosjektledere,kundeansvarlig mht. tilbud, anbud og prosjekter, samt følge opp ferdigmelding av prosjekt\n" +
                "* Rapportere til distriktssjef\n" +
                "\n" +
                "**Ønskede kvalifikasjoner: og egenskaper:**\n" +
                "* Fortrinnsvis ingeniørbakgrunn, eventuelt Teknisk Fagskole\n" +
                "* Prosjektleder- og rådgivererfaring\n" +
                "* Erfaring med kalkulasjon av prosjekter\n" +
                "* Fordel med ledererfaring\n" +
                "* Gode kommunikasjonsevner og evne til å skape gode relasjoner både internt og ektsternt\n" +
                "* Resultatorientert og fokus på økonomi\n" +
                "* Strukturert\n" +
                "* Generelt god IT kunnskap\n" +
                "\n" +
                "**Vi kan tilby:**\n" +
                "* En interessant stilling i en offensiv bedrift i vekst og med solid økonomi\n" +
                "* Utfordrende arbeidsoppgaver og gode utviklingsmuligheter\n" +
                "* Kurs og etterutdanning\n" +
                "* Gode datasystemer for prosjekt- og økonomistyring\n" +
                "* Gode pensjons-, lønns- og forsikringsforhold\n" +
                "* Bil- og telefonordning\n" +
                "* Aktiv bedriftsidrett\n" +
                "\n" +
                "**Referanse:** **Avdelingsleder GK Elektro Trondheim**\n" +
                "\n" +
                "**Kontaktperson:** Tom Erik Olsen, Distriktssjef, tel. 9587 3574\n" +
                "\n" +
                "[Søk stilling](https://candidate.hr-manager.net/ApplicationForm/SinglePageApplicationForm.aspx?cid=139&departmentId=21542&ProjectId=181721&MediaId=5)\n" +
                "\n";
        String html = MarkdownToHtmlConverter.parse(markdown);
        assertThat(html, containsString("<p><strong>Viktigste arbeidsoppgaver:</strong></p>\n" +
                "<ul><li>Personal, resultat og budsjettansvar</li>"));
    }

    @Test
    public void parse_markdownWithSection() {
        String markdown = "Seksjoner\n" +
                "<section id=\"en\">\n" +
                "\n" +
                "## Seksjon 1\n" +
                "*Ting i seksjon* - cool!\n" +
                "\n" +
                "</section>\n" +
                "Utenfor seksjon\n" +
                "\n" +
                "<section id=\"to\">\n" +
                "\n" +
                "### Seksjon 2\n" +
                "\n" +
                "</section>\n";

        String expected = "<p>Seksjoner</p>\n" +
                "<section id=\"en\">\n" +
                "<h2>Seksjon 1</h2>\n" +
                "<p><em>Ting i seksjon</em> - cool!</p>\n" +
                "</section>\n" +
                "Utenfor seksjon\n" +
                "<section id=\"to\">\n" +
                "<h3>Seksjon 2</h3>\n" +
                "</section>\n";

        assertEquals(expected, MarkdownToHtmlConverter.parse(markdown));
    }


}
