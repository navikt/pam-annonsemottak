package no.nav.pam.annonsemottak.markdown;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class HtmlSanitizerTest {

    @Test
    public void knownVulnerability1() {

        String markdown = "This is a regular paragraph.\n" +
                "\n" +
                "<script>alert('xss');</script>\n" +
                "\n" +
                "This is another regular paragraph.";
        String expected = "This is a regular paragraph.\n" +
                "\n" +
                "\n" +
                "\n" +
                "This is another regular paragraph.";

        assertEquals(expected, HtmlSanitizer.sanitize(markdown));
    }


    @Test
    public void saninize_removes_unnnecessary_attributes() {
        String fragment = "\t<p> Både tilfeldige timar/dagar og vikariat over lengre tid kan verte aktuelt.</p> " +
                "<p> Vi ber alle som kan tenkje seg å arbeide med barn og unge om å kontakte einingane direkte:</p> " +
                "<table border=\"1\" cellpadding=\"0\" cellspacing=\"0\"> <tbody> <tr> <td style=\"width:307px;\"> " +
                "<p> BARNEHAGAR</p> </td> <td style=\"width:307px;\"> " +
                "<p> SKULAR</p> </td> </tr> <tr> <td style=\"width:307px;\"> " +
                "<p> <strong>Austefjord</strong>: Tlf 97423259</p> " +
                "<p> <strong>Engeset</strong>: Tlf 70079914</p> " +
                "<p> <strong>Folkestad</strong>: Tlf 70073187</p> " +
                "<p> <strong>Lauvstad:</strong> Tlf 70055258</p> " +
                "<p> <strong>Mork</strong>: Tlf 70077790</p> " +
                "<p> <strong>Oppigarden</strong>: Tlf 70330462</p> " +
                "<p> <strong>Sollia</strong>: Tlf 70076993</p> </td> " +
                "<td style=\"width:307px;\"> <p> <strong>Austefjord</strong>: Tlf 70059150</p> " +
                "<p> <strong>Øyra</strong>: Tlf 70074300</p> " +
                "<p> <strong>Bratteberg</strong>: Tlf 70058000</p> " +
                "<p> <strong>Vikebygda</strong>: Tlf 70074040</p> " +
                "<p> <strong>Mork:</strong> Tlf 70076884</p> " +
                "<p> <strong>Dalsfjord</strong>:Tlf 70055064</p> " +
                "<p> <strong>Folkestad: </strong>Tlf 70059620</p> " +
                "<p> <strong>Volda ungdomsskule</strong>: Tlf 70074220</p> " +
                "<p> <strong>Volda Læringssenter</strong>: Tlf 70078596</p> </td> </tr> </tbody> </table> " +
                "<p> &nbsp;</p> " +
                "<p> De kan og kontakte opplæring og oppvekst i Volda kommune ved Nina Hovden Eidheim på epost: <a href=\"mailto:nina.hovden.eidheim@volda.kommune.no\">nina.hovden.eidheim@volda.kommune.no</a></p> <p> Ved kontakt via epost: skriv litt om deg sjølv, bakgrunn, studier, kva aldersgruppe du ser føre deg å arbeide med, omfang mm.</p>";


        String expected = "\t<p> Både tilfeldige timar/dagar og vikariat over lengre tid kan verte aktuelt.</p> " +
                "<p> Vi ber alle som kan tenkje seg å arbeide med barn og unge om å kontakte einingane direkte:</p> " +
                "<table> <tbody><tr><td> " +
                "<p> BARNEHAGAR</p> </td><td> " +
                "<p> SKULAR</p> </td></tr><tr><td> " +
                "<p> <strong>Austefjord</strong>: Tlf 97423259</p> " +
                "<p> <strong>Engeset</strong>: Tlf 70079914</p> " +
                "<p> <strong>Folkestad</strong>: Tlf 70073187</p> " +
                "<p> <strong>Lauvstad:</strong> Tlf 70055258</p> " +
                "<p> <strong>Mork</strong>: Tlf 70077790</p> " +
                "<p> <strong>Oppigarden</strong>: Tlf 70330462</p> " +
                "<p> <strong>Sollia</strong>: Tlf 70076993</p> </td>" +
                "<td> <p> <strong>Austefjord</strong>: Tlf 70059150</p> " +
                "<p> <strong>Øyra</strong>: Tlf 70074300</p> " +
                "<p> <strong>Bratteberg</strong>: Tlf 70058000</p> " +
                "<p> <strong>Vikebygda</strong>: Tlf 70074040</p> " +
                "<p> <strong>Mork:</strong> Tlf 70076884</p> " +
                "<p> <strong>Dalsfjord</strong>:Tlf 70055064</p> " +
                "<p> <strong>Folkestad: </strong>Tlf 70059620</p> " +
                "<p> <strong>Volda ungdomsskule</strong>: Tlf 70074220</p> " +
                "<p> <strong>Volda Læringssenter</strong>: Tlf 70078596</p> </td></tr></tbody> </table> " +
                "<p>  </p> " +
                "<p> De kan og kontakte opplæring og oppvekst i Volda kommune ved Nina Hovden Eidheim på epost: <a href=\"mailto:nina.hovden.eidheim&#64;volda.kommune.no\" rel=\"nofollow\">nina.hovden.eidheim&#64;volda.kommune.no</a></p> <p> Ved kontakt via epost: skriv litt om deg sjølv, bakgrunn, studier, kva aldersgruppe du ser føre deg å arbeide med, omfang mm.</p>";

        assertEquals(expected, HtmlSanitizer.sanitize(fragment));
    }


    @Test
    public void sanitize_links_properly(){
        String fragment = "<br>Søk via vårt <a href=\"https://soknad.kvam.kommune.no/\" target=\"_blank\">digitale søknadssenter</a>" +
                "\n<a id=\"myLink\" href=\"javascript:MyFunction();\">link with js in href</a>" +
                "\n<a id=\"myLink\" href=\"#\" onclick=\"MyFunction();return false;\">link with onclick</a>";

        String expected = "<br />Søk via vårt <a href=\"https://soknad.kvam.kommune.no/\" rel=\"nofollow\">digitale søknadssenter</a>\n" +
                "link with js in href\n" +
                "<a href=\"#\" rel=\"nofollow\">link with onclick</a>";

        assertEquals(expected, HtmlSanitizer.sanitize(fragment));
    }

}
