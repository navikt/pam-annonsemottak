package no.nav.pam.annonsemottak.markdown;

import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

public class HtmlSanitizer {

    public static String sanitize(String html){
        PolicyFactory policy = Sanitizers.FORMATTING
                .and(Sanitizers.LINKS)
                .and(Sanitizers.BLOCKS)
                .and(Sanitizers.TABLES);

        return policy.sanitize(html);
    }
}
