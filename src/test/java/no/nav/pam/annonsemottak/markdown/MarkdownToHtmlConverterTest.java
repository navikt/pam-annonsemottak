package no.nav.pam.annonsemottak.markdown;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MarkdownToHtmlConverterTest {

    @Test
    public void parse_sampleMarkdown() {
        String markdown = """
                ***Vil du være med å definere fremtiden innen funksjonelle modulbygg?***
                
                **Senior Prosjektleder**
                
                Over lengre tid har vi bygd opp en sterk portefølje i Midt-Norge innen modulbasert bygging. Vi har kunder og inngåtte rammeavtaler med de store private aktører og offentlige byggherrer. Våre konsepter møter morgendagens krav til funksjon, design og byggeforskrifter. Kort byggetid, flyttbarhet, lave kostnader, fleksibilitet, skreddersøm, miljøvennlige produkter, innendørs produksjon hos våre underleverandører mm, gir våre kunder mange fordeler ved å velge løsninger fra oss.
                
                ++Våre konsepter:++
                
                *Living* - hoteller, leiligheter og studenthybler.
                
                *Public* - skoler, barnehager og omsorgsboliger.
                
                *Industry* - kontorer og entreprenørmoduler.
                
                *Camp* - kontorer, borigger og kantine.
                
                Vår oppdragsmengde er økende og vi søker etter en forretningsorientert senior prosjektleder som kan bli med å ta ansvar for videre utvikling av våre mange spennende prosjekter i Midt-Norge. Du kommer i nær dialog med våre kunder, og må være god på å forstå kundebehov og drivende dyktig på oppfølging i prosjekter. På avdelingen i Trondheim vil du jobbe tett sammen med vår prosjektleder og byggeleder, og rapportere til prosjektsjef i Norge. Vi er organisert slik at du henter støtteressurser i selskapet ved behov innen f.eks. tekniske løsninger og krav, anbud/ tilbudsarbeid, økonomi, HR, kontrakt etc.""";
        String expected = """
                <p><em><strong>Vil du være med å definere fremtiden innen funksjonelle modulbygg?</strong></em></p>
                <p><strong>Senior Prosjektleder</strong></p>
                <p>Over lengre tid har vi bygd opp en sterk portefølje i Midt-Norge innen modulbasert bygging. Vi har kunder og inngåtte rammeavtaler med de store private aktører og offentlige byggherrer. Våre konsepter møter morgendagens krav til funksjon, design og byggeforskrifter. Kort byggetid, flyttbarhet, lave kostnader, fleksibilitet, skreddersøm, miljøvennlige produkter, innendørs produksjon hos våre underleverandører mm, gir våre kunder mange fordeler ved å velge løsninger fra oss.</p>
                <p>&#43;&#43;Våre konsepter:&#43;&#43;</p>
                <p><em>Living</em> - hoteller, leiligheter og studenthybler.</p>
                <p><em>Public</em> - skoler, barnehager og omsorgsboliger.</p>
                <p><em>Industry</em> - kontorer og entreprenørmoduler.</p>
                <p><em>Camp</em> - kontorer, borigger og kantine.</p>
                <p>Vår oppdragsmengde er økende og vi søker etter en forretningsorientert senior prosjektleder som kan bli med å ta ansvar for videre utvikling av våre mange spennende prosjekter i Midt-Norge. Du kommer i nær dialog med våre kunder, og må være god på å forstå kundebehov og drivende dyktig på oppfølging i prosjekter. På avdelingen i Trondheim vil du jobbe tett sammen med vår prosjektleder og byggeleder, og rapportere til prosjektsjef i Norge. Vi er organisert slik at du henter støtteressurser i selskapet ved behov innen f.eks. tekniske løsninger og krav, anbud/ tilbudsarbeid, økonomi, HR, kontrakt etc.</p>
                """;
        assertEquals(expected, MarkdownToHtmlConverter.parse(markdown));
    }

    @Test
    public void markdownBulletpointsShouldBecomeProperHtmlBulletpoints() {
        String markdown = """
                #### Arbeidsoppgaver
                
                * Undervisning. Individuell og /eller gruppeundervisning
                
                #### Kvalifikasjoner
                
                * Minimum 3 årig høyskole-/universitetsutdanning innenfor musikk, dans og teater.
                * Dersom du har tatt hele eller deler av utdanningen din i utlandet, må denne godkjennes av NOKUT.
                
                #### Utdanningsretning
                
                * Pedagogikk
                * Utdanningstittel: Musikk og kulturskolelærer
                
                #### Utdanningsnivå
                
                * Høyskole / Universitet, årsstudie / grunnfag
                * Høyskole / Universitet, Diplom- / Bachelorgrad
                
                #### Personlige egenskaper
                
                * Ansvarsbevisst og selvstendig.
                * God kommunikasjonsevne med barn og unge.
                
                #### Språk
                
                * Norsk
                
                #### Vi tilbyr
                
                * Lønn ihht. tariff.
                * Pensjonsordning
                
                """;
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
        String markdown = """
                GK Elektro har fokus på et sterkt fagmiljø, godt samarbeid og en positiv hverdag. De ansatte i GK Elektro jobber tett sammen for felles mål og arbeidsmiljøet er lystbetont og uformelt. Avdelingen betjener entrepriseprosjekter i hele Trøndelag område. Du må være fagorientert og trives med å jobbe i et flerfaglig teknisk miljø sammen med øvrige GK-fag.
                
                Som avdelingsleder i GK Elektro har du det overordnede ansvaret for å organisere, utvikle og ivareta den daglige driften i avdelingen, samt være en motivator for avdelingens ansatte.
                
                I mai 2017 flytter vi inn i nye flotte lokaler hvor vi blir samlokalisert med GK Inneklima og GK Rør for et enda bedre tverrfaglig samarbeid.
                
                **Viktigste arbeidsoppgaver:**
                * Personal, resultat og budsjettansvar
                * Lede avdelingen iht. bedriftens verdier, handlingsplaner og budsjett
                * Utarbeide forslag til og evaluere operative mål, herunder handlingsplan, budsjett, bemanningsplan og markedsplan
                * Ansvarlig for opplæring i GKs prosess- og produksjonsrutiner (VIP og prosjektoppfølging)
                * Markedsføre avdelingen samt skape gode og tillitsfulle relasjoner med kunder og leverandører
                * Ha tett samarbeid med prosjektledere,kundeansvarlig mht. tilbud, anbud og prosjekter, samt følge opp ferdigmelding av prosjekt
                * Rapportere til distriktssjef
                
                **Ønskede kvalifikasjoner: og egenskaper:**
                * Fortrinnsvis ingeniørbakgrunn, eventuelt Teknisk Fagskole
                * Prosjektleder- og rådgivererfaring
                * Erfaring med kalkulasjon av prosjekter
                * Fordel med ledererfaring
                * Gode kommunikasjonsevner og evne til å skape gode relasjoner både internt og ektsternt
                * Resultatorientert og fokus på økonomi
                * Strukturert
                * Generelt god IT kunnskap
                
                **Vi kan tilby:**
                * En interessant stilling i en offensiv bedrift i vekst og med solid økonomi
                * Utfordrende arbeidsoppgaver og gode utviklingsmuligheter
                * Kurs og etterutdanning
                * Gode datasystemer for prosjekt- og økonomistyring
                * Gode pensjons-, lønns- og forsikringsforhold
                * Bil- og telefonordning
                * Aktiv bedriftsidrett
                
                **Referanse:** **Avdelingsleder GK Elektro Trondheim**
                
                **Kontaktperson:** Tom Erik Olsen, Distriktssjef, tel. 9587 3574
                
                [Søk stilling](https://candidate.hr-manager.net/ApplicationForm/SinglePageApplicationForm.aspx?cid=139&departmentId=21542&ProjectId=181721&MediaId=5)
                
                """;
        String html = MarkdownToHtmlConverter.parse(markdown);
        assertThat(html, containsString("""
                <p><strong>Viktigste arbeidsoppgaver:</strong></p>
                <ul><li>Personal, resultat og budsjettansvar</li>"""));
    }

    @Test
    public void parse_markdownWithSection() {
        String markdown = """
                Seksjoner
                <section id="en">
                
                ## Seksjon 1
                *Ting i seksjon* - cool!
                
                </section>
                Utenfor seksjon
                
                <section id="to">
                
                ### Seksjon 2
                
                </section>
                """;

        String expected = """
                <p>Seksjoner</p>
                <section id="en">
                <h2>Seksjon 1</h2>
                <p><em>Ting i seksjon</em> - cool!</p>
                </section>
                Utenfor seksjon
                <section id="to">
                <h3>Seksjon 2</h3>
                </section>
                """;

        assertEquals(expected, MarkdownToHtmlConverter.parse(markdown));
    }


}
