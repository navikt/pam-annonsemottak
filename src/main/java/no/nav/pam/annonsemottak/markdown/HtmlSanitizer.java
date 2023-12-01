package no.nav.pam.annonsemottak.markdown;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

public class HtmlSanitizer {

    public static String sanitize(String html){
        PolicyFactory allowSectionTags = new HtmlPolicyBuilder()
                .allowElements("section")
                .allowAttributes("id").onElements("section")
                .toFactory();

        PolicyFactory policy = Sanitizers.FORMATTING
                .and(Sanitizers.LINKS)
                .and(Sanitizers.BLOCKS)
                .and(Sanitizers.TABLES)
                .and(Sanitizers.FORMATTING)
                .and(allowSectionTags);

        return policy.sanitize(html);
    }
}
