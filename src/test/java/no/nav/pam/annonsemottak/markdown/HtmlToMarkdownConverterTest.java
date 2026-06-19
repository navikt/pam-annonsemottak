package no.nav.pam.annonsemottak.markdown;


import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Methods {@code knownVulnerability*} are based on
 * <a href=https://github.com/showdownjs/showdown/wiki/Markdown's-XSS-Vulnerability-(and-how-to-mitigate-it)>this page</a>.
 */
public class HtmlToMarkdownConverterTest {

    @Test
    public void keepGuillemets() {
        String markdown = "«samskaping»";
        String expectedHtml = "<p>«samskaping»</p>\n";
        String expectedMarkdown = markdown + "\n";

        String html = MarkdownToHtmlConverter.parse(markdown);
        assertEquals(expectedHtml, html);

        String convertedMarkdown = HtmlToMarkdownConverter.parse(markdown);
        assertEquals(expectedMarkdown, convertedMarkdown);
    }

    @Test
    public void knownVulnerability1() {

        String markdown = """
                This is a regular paragraph.
                
                <script>alert('xss');</script>
                
                This is another regular paragraph.""";
        String html = MarkdownToHtmlConverter.parse(markdown);
        assertThat(html, not(containsString("<script>alert('xss');</script>")));

    }

    @Test
    public void knownVulnerability2() {

        String markdown = "hello <a name=\"n\" href=\"javascript:alert('xss')\">*you*</a>";
        String html = MarkdownToHtmlConverter.parse(markdown);
        assertThat(html, not(containsString("<a name=\"n\" href=\"javascript:alert('xss')\">")));
    }

    @Test
    public void knownVulnerability3() {

        String markdown = "[some text](javascript:alert('xss'))";
        String html = MarkdownToHtmlConverter.parse(markdown);
        assertThat(html, not(containsString("<a href=\"javascript:alert('xss')\">")));

    }

    @Test
    public void knownVulnerability4() {

        String markdown = """
                > hello <a name="n"
                > href="javascript:alert('xss')">*you*</a>""";
        String html = MarkdownToHtmlConverter.parse(markdown);
        assertThat(html, not(containsString("""
                <a name="n"
                href="javascript:alert('xss')">""")));

    }


    @Test
    public void should_parse_sample_html_1() {
        String fragment = "<p>Gjemnes sykehjem/ skjermet avdeling</p> <p style=\"margin: 0cm 0cm 8pt;\">Ledig fra 01.12.2017</p> <p>&nbsp;</p> <p>Gjemnes sykehjem ligger sentralt til Batnfjordsøra sentrum. Sykehjemmet består av en somatisk avdeling med 21 plasser og 1 avlastningsplass, skjermet avdeling med 7 langtidsplasser og korttidsavdeling med 4 plasser for observasjon og rehabilitering. I tillegg er det en plass avsatt til palliasjon.</p> <p>Stillingen har 20 % administrasjonstid i forbindelse med fagansvar.</p> <p><strong>Arbeidsoppgaver:</strong></p> <ul> <li>Målrettet pleie og omsorg til pasientene ved skjermet enhet.</li> <li>Fagansvar på skjermet enhet</li> <li>Medisinansvar</li> <li>Dokumentasjon</li> <li>Stedfortreder for seksjonsleder ved sykehjemmet</li> </ul> <p><strong>Kvalifikasjonskrav:</strong></p> <ul> <li>Offentlig godkjent sykepleier med gjennomført ABC demens</li> <li>Gode datakunnskaper</li> <li>Må beherske skriftlig og muntlig norsk</li> </ul> <p><strong>Egnethet for stillingen vil bli vektlagt:</strong></p> <p>I tillegg til faglige kvalifikasjoner vil personlig egnethet bli vektlagt.</p> <p>Søker må ha gode samarbeidsevner, endringsvillig og kunne arbeide selvstendig.</p> <p><a class=\"InnholdLinkTekst external-link\" href=\"https://skjema.kf.no/more/wizard/wizard.jsp?wizardid=1009&amp;ouref=1557\">Her finner du link til søknadsskjema</a></p> <p><strong>Søknadsfrist: 12.11.2017</strong></p> <p>Tilsetting skjer etter vanlig kommunale vilkår i samsvar med lov- og avtaleverk. Medlemskap i KLP. Lønn etter avtale. Som rekrutteringstiltak tilbyr Gjemnes kommune 10. års ansiennitet ved tiltredelse. Politiattest kreves.</p> <p>&nbsp;</p> <p><strong>Kontaktpersoner: </strong>Nærmere opplysninger omkring stillingen kan du få ved å kontakte seksjonsleder Te 71291173/97546702</p>";
        String expected = """
                Gjemnes sykehjem/ skjermet avdeling
                
                Ledig fra 01.12.2017
                
                Gjemnes sykehjem ligger sentralt til Batnfjordsøra sentrum. Sykehjemmet består av en somatisk avdeling med 21 plasser og 1 avlastningsplass, skjermet avdeling med 7 langtidsplasser og korttidsavdeling med 4 plasser for observasjon og rehabilitering. I tillegg er det en plass avsatt til palliasjon.
                
                Stillingen har 20 % administrasjonstid i forbindelse med fagansvar.
                
                **Arbeidsoppgaver:**
                
                * Målrettet pleie og omsorg til pasientene ved skjermet enhet.
                * Fagansvar på skjermet enhet
                * Medisinansvar
                * Dokumentasjon
                * Stedfortreder for seksjonsleder ved sykehjemmet
                
                **Kvalifikasjonskrav:**
                
                * Offentlig godkjent sykepleier med gjennomført ABC demens
                * Gode datakunnskaper
                * Må beherske skriftlig og muntlig norsk
                
                **Egnethet for stillingen vil bli vektlagt:**
                
                I tillegg til faglige kvalifikasjoner vil personlig egnethet bli vektlagt.
                
                Søker må ha gode samarbeidsevner, endringsvillig og kunne arbeide selvstendig.
                
                [Her finner du link til søknadsskjema](https://skjema.kf.no/more/wizard/wizard.jsp?wizardid=1009&ouref=1557)
                
                **Søknadsfrist: 12.11.2017**
                
                Tilsetting skjer etter vanlig kommunale vilkår i samsvar med lov- og avtaleverk. Medlemskap i KLP. Lønn etter avtale. Som rekrutteringstiltak tilbyr Gjemnes kommune 10. års ansiennitet ved tiltredelse. Politiattest kreves.
                
                **Kontaktpersoner:**Nærmere opplysninger omkring stillingen kan du få ved å kontakte seksjonsleder Te 71291173/97546702
                """;


        assertEquals(expected, HtmlToMarkdownConverter.parse(fragment));
    }

    @Test
    public void should_parse_sample_html_2() {
        String fragment = "<div> Kvalifikasjonskrav:</div> <ul> <li> Utdanning som minimum barnevernspedagog eller sosionom&nbsp;</li> <li> Oppdatert kompetanse frå arbeid i kommunalt barnevern&nbsp;&nbsp;</li> <li> God kjennskap til Lov om barnevernstenester og anna relevant lovverk.</li> <li> Anna relevant praksis.</li> <li> Gode datakunnskapar.</li> <li> Førarkort klasse B</li> </ul> <div> Vi legg vekt på:</div> <ul> <li> God skriftleg og munnleg framstillingsevne</li> <li> Personlege eigenskapar som til dømes evne til samarbeid og kommunikasjon&nbsp;</li> <li> Evne til å arbeide både i team og sjølvstendig</li> <li> Evne til å bidra i ein lærande organisasjon</li> </ul> <div> Vi tilbyr:</div> <ul> <li> løn etter avtale i høve til kvalifikasjonar og kompetanse</li> <li> spennande utfordringar i eit utviklingsorientert miljø&nbsp;&nbsp;</li> <li> systematisk rettleiing</li> <li> løysingsorientert organisasjonskultur bygd på tillit og handlingsrom&nbsp;</li> <li> inkluderande arbeidslivsverksemd</li> <li> god pensjonsordning i KLP</li> </ul> <div> For nærare opplysningar ta kontakt med barnevernsjef Brynhild Solvang eller fagteamleiar Aina Øyehaug Opsvik</div> <div> &nbsp;</div> <div> <strong>Søknadsfrist: 24.11.17.&nbsp;</strong>ID 907</div> <div> &nbsp;</div> <div> Personlege eigenskapar vert tillagt stor vekt.</div> <div> &nbsp;</div> <div> Aktuelle søkarar vert innkalla til intervju.</div> <div> &nbsp;</div> <div> Volda kommune har god offentleg pensjonsordning i KLP</div> <div> &nbsp;</div> <div> Kommunen har nynorsk som administrasjonsspråk.<br> <br> <div style=\"font-family: Arial, Helvetica, sans-serif; font-size: 15px; background-color: rgb(255, 255, 255);\"> Vi ber deg nytte det elektroniske søknadsskjemaet som du finn i lenke til høgre på sida,<br> eller her:&nbsp;<a class=\"InnholdLinkTekst InnholdLinkTekst \" href=\"https://kommune24-7.no/1519/bruker?retur=%2f1519%2f703102&amp;shortname=703102\" style=\"color: rgb(31, 124, 190); font-size: 0.9em;\">søknadskjema ledig stilling</a></div> <div style=\"font-family: Arial, Helvetica, sans-serif; font-size: 15px; background-color: rgb(255, 255, 255);\"> &nbsp;</div> <div style=\"font-family: Arial, Helvetica, sans-serif; font-size: 15px; background-color: rgb(255, 255, 255);\"> CV må fyllast ut i søknaden.<br> &nbsp;</div> <div style=\"font-family: Arial, Helvetica, sans-serif; font-size: 15px; background-color: rgb(255, 255, 255);\"> Attester og vitnemål skal leverast på førespurnad.&nbsp;</div> <div style=\"font-family: Arial, Helvetica, sans-serif; font-size: 15px; background-color: rgb(255, 255, 255);\"> &nbsp;</div> </div> <div> <strong>Om offentlege søkjarliste</strong></div> <div> I samsvar med offentleglova § 25, kan opplysningar om søkjar bli unnateke i offentleg søkjarliste, dersom søkjaren sjølv bed om det. Søkjarar som ønskjer at namn skal vere unnateke offentleg søkjarliste, skal grunngje dette. Kommunen er pliktig til å vurdere meiroffentlegheit og vi gjer ei konkret vurdering av søkjar sitt ønskje om fritak - opp mot dei omsyn som talar for offentlegheit. Om kommunen meinar at grunngjevinga for fritak ikkje er tilstrekkeleg, vil søkjar bli kontakta slik at det er mogleg å trekkje søknaden før endeleg søkarliste blir laga.&nbsp;</div> <div> &nbsp;</div> ";
        String expected = """
                Kvalifikasjonskrav:
                
                * Utdanning som minimum barnevernspedagog eller sosionom
                * Oppdatert kompetanse frå arbeid i kommunalt barnevern
                * God kjennskap til Lov om barnevernstenester og anna relevant lovverk.
                * Anna relevant praksis.
                * Gode datakunnskapar.
                * Førarkort klasse B
                
                Vi legg vekt på:
                
                * God skriftleg og munnleg framstillingsevne
                * Personlege eigenskapar som til dømes evne til samarbeid og kommunikasjon
                * Evne til å arbeide både i team og sjølvstendig
                * Evne til å bidra i ein lærande organisasjon
                
                Vi tilbyr:
                
                * løn etter avtale i høve til kvalifikasjonar og kompetanse
                * spennande utfordringar i eit utviklingsorientert miljø
                * systematisk rettleiing
                * løysingsorientert organisasjonskultur bygd på tillit og handlingsrom
                * inkluderande arbeidslivsverksemd
                * god pensjonsordning i KLP
                
                For nærare opplysningar ta kontakt med barnevernsjef Brynhild Solvang eller fagteamleiar Aina Øyehaug Opsvik \s
                **Søknadsfrist: 24.11.17.**ID 907 \s
                Personlege eigenskapar vert tillagt stor vekt. \s
                Aktuelle søkarar vert innkalla til intervju. \s
                Volda kommune har god offentleg pensjonsordning i KLP \s
                Kommunen har nynorsk som administrasjonsspråk. \s
                
                Vi ber deg nytte det elektroniske søknadsskjemaet som du finn i lenke til høgre på sida, \s
                eller her: [søknadskjema ledig stilling](https://kommune24-7.no/1519/bruker?retur=%2f1519%2f703102&shortname=703102) \s
                CV må fyllast ut i søknaden. \s
                Attester og vitnemål skal leverast på førespurnad. \s
                **Om offentlege søkjarliste** \s
                I samsvar med offentleglova § 25, kan opplysningar om søkjar bli unnateke i offentleg søkjarliste, dersom søkjaren sjølv bed om det. Søkjarar som ønskjer at namn skal vere unnateke offentleg søkjarliste, skal grunngje dette. Kommunen er pliktig til å vurdere meiroffentlegheit og vi gjer ei konkret vurdering av søkjar sitt ønskje om fritak - opp mot dei omsyn som talar for offentlegheit. Om kommunen meinar at grunngjevinga for fritak ikkje er tilstrekkeleg, vil søkjar bli kontakta slik at det er mogleg å trekkje søknaden før endeleg søkarliste blir laga. \s
                """;
        assertEquals(expected, HtmlToMarkdownConverter.parse(fragment));
    }

    @Test
    public void should_handle_markdown_in_html() {
        String fragment = "<p>\nHer er det masse tekst som ikke er html, men som har noe som er markdown [Klikk her](javascript:alert('xss')) og masse tekst her også\n</p>";
        String expectedMarkdown = "Her er det masse tekst som ikke er html, men som har noe som er markdown \\[Klikk her\\](javascript:alert('xss')) og masse tekst her også\n";
        assertEquals(expectedMarkdown, HtmlToMarkdownConverter.parse(fragment));

        String expectedHtml = "<p>Her er det masse tekst som ikke er html, men som har noe som er markdown [Klikk her](javascript:alert(&#39;xss&#39;)) og masse tekst her også</p>\n";
        assertEquals(expectedHtml, MarkdownToHtmlConverter.parse(expectedMarkdown));

    }

    @Test
    public void should_handle_semi_escaped_html() {
        String semiEscapedHtml = "&lt;strong>Kvalifikasjoner:&lt;/strong>&lt;/p>&lt;ul>" +
                "&lt;li>Erfaring med ReactJS eller andre Javascriptbibliotek&lt;/li>" +
                "&lt;li>Erfaring med eller kjennskap til Google Analytics&lt;/li>" +
                "&lt;li>God forståelse for brukervennlighet, universell utforming og responsivt design&lt;/li>" +
                "&lt;li>Kjennskap til kontinuerlige leveranser, og kunne se forretningsprosesser og IT-løsninger i sammenheng&lt;/li>" +
                "&lt;li>God skriftlig og muntlig fremstillingsevne på norsk&lt;/li>&lt;li>Høyere utdannelse er en fordel&lt;/li>" +
                "&lt;li>Det er en fordel om du har erfaring med C#, Java eller NodeJS, samt EPiServer og skytjenester&lt;/li>" +
                "&lt;li>God på dialog og samarbeid&lt;/li>&lt;/ul>&lt;p>";

        String expectedMarkup = """
                **Kvalifikasjoner:**
                
                * Erfaring med ReactJS eller andre Javascriptbibliotek
                * Erfaring med eller kjennskap til Google Analytics
                * God forståelse for brukervennlighet, universell utforming og responsivt design
                * Kjennskap til kontinuerlige leveranser, og kunne se forretningsprosesser og IT-løsninger i sammenheng
                * God skriftlig og muntlig fremstillingsevne på norsk
                * Høyere utdannelse er en fordel
                * Det er en fordel om du har erfaring med C#, Java eller NodeJS, samt EPiServer og skytjenester
                * God på dialog og samarbeid
                
                
                
                """;


        String converted = HtmlToMarkdownConverter.parse(semiEscapedHtml);
        assertEquals(expectedMarkup, converted);
    }

    @Test
    public void should_not_leave_br_after_parsing(){
        String s = "&lt;p>&lt;strong>Hos oss vil du møte et profesjonelt fagmiljø i en positiv og hektisk hverdag. Vi har et uformelt arbeidsmiljø hvor trivsel og høy aktivitet er i fokus. Vi jobber aktivt med HMS og har mål om null skader på arbeidsplassen. Vi har en ny og godt vedlikeholdt maskinpark og investerer kontinuerlig i nytt utstyr.&lt;/strong>&lt;/p>&lt;p>&lt;strong>Arbeidsoppgaver:&lt;/strong>&lt;/p>&lt;ul>&lt;li>Reparasjon og vedlikehold av diverse stillasmateriell&lt;/li>&lt;li>Lagerarbeid&lt;/li>&lt;/ul>&lt;p> &lt;/p>&lt;p>&lt;strong>Vi ønsker følgende kvalifikasjoner og egenskaper:&lt;/strong>&lt;/p>&lt;ul>&lt;li>Erfaring fra lagerarbeid&lt;/li>&lt;li>Erfaring fra sveisearbeid er ønskelig&lt;/li>&lt;li>Truckførerbevis&lt;/li>&lt;li>Gode språkkunnskaper i norsk eller engelsk&lt;/li>&lt;li>Evne til å takle en hektisk arbeidshverdag&lt;/li>&lt;li>God til å samarbeide og skape et godt arbeidsmiljø&lt;/li>&lt;li>Positiv og engasjert&lt;/li>&lt;/ul>&lt;p>&lt;br />&lt;strong>Vi tilbyr:&lt;/strong>&lt;/p>&lt;ul>&lt;li>Profesjonelt fagmiljø&lt;/li>&lt;li>Faglig utviklingsmuligheter&lt;/li>&lt;li>Et selskap i vekst&lt;/li>&lt;li>Konkurransedyktige betingelser&lt;/li>&lt;li>Gode pensjons- og forsikringsordninger&lt;/li>&lt;li>Aksjespareprogram&lt;/li>&lt;li>Firmahytter&lt;/li>&lt;li>Bonusordning&lt;/li>&lt;/ul>&lt;p> &lt;/p>&lt;p>Dersom dette høres interessant ut, ser vi frem til å motta din CV og søknad snarest.&lt;/p>&lt;p> &lt;/p>&lt;p> &lt;/p>";
        String expected = """
                **Hos oss vil du møte et profesjonelt fagmiljø i en positiv og hektisk hverdag. Vi har et uformelt arbeidsmiljø hvor trivsel og høy aktivitet er i fokus. Vi jobber aktivt med HMS og har mål om null skader på arbeidsplassen. Vi har en ny og godt vedlikeholdt maskinpark og investerer kontinuerlig i nytt utstyr.**
                
                **Arbeidsoppgaver:**
                
                * Reparasjon og vedlikehold av diverse stillasmateriell
                * Lagerarbeid
                
                **Vi ønsker følgende kvalifikasjoner og egenskaper:**
                
                * Erfaring fra lagerarbeid
                * Erfaring fra sveisearbeid er ønskelig
                * Truckførerbevis
                * Gode språkkunnskaper i norsk eller engelsk
                * Evne til å takle en hektisk arbeidshverdag
                * God til å samarbeide og skape et godt arbeidsmiljø
                * Positiv og engasjert
                
                
                
                **Vi tilbyr:**
                
                * Profesjonelt fagmiljø
                * Faglig utviklingsmuligheter
                * Et selskap i vekst
                * Konkurransedyktige betingelser
                * Gode pensjons- og forsikringsordninger
                * Aksjespareprogram
                * Firmahytter
                * Bonusordning
                
                Dersom dette høres interessant ut, ser vi frem til å motta din CV og søknad snarest.
                
                """;

        assertEquals(expected, HtmlToMarkdownConverter.parse(s));
    }
}
