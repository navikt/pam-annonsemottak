package no.nav.pam.annonsemottak.markdown;

import com.vladsch.flexmark.convert.html.FlexmarkHtmlParser;
import com.vladsch.flexmark.util.options.DataHolder;
import com.vladsch.flexmark.util.options.MutableDataSet;

/**
 * One parser, one configuration, same HTML-to-markdown behaviour for all sources.
 */
public class HtmlToMarkdownConverter {

    // Set options here, see https://goo.gl/2xedeq for sample.
    private static final DataHolder OPTIONS = new MutableDataSet();
    private static final int MAX_BLANK_LINES = 2;

    public static String parse(String html) {
        if (html == null) {
            return null;
        }

        html = HtmlSanitizer.sanitize(html);

        return FlexmarkHtmlParser.parse(html, MAX_BLANK_LINES, OPTIONS);
    }
}
