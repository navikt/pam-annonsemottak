package no.nav.pam.annonsemottak.markdown;

import com.vladsch.flexmark.convert.html.FlexmarkHtmlParser;

import com.vladsch.flexmark.util.data.MutableDataSet;
import org.apache.commons.text.StringEscapeUtils;

import static com.vladsch.flexmark.convert.html.FlexmarkHtmlParser.TYPOGRAPHIC_QUOTES;

/**
 * One parser, one configuration, same HTML-to-markdown behaviour for all sources.
 */
public class HtmlToMarkdownConverter {

    // Set options here, see https://goo.gl/2xedeq for sample.
    private static final MutableDataSet OPTIONS = new MutableDataSet();
    private static final int MAX_BLANK_LINES = 2;

    static {
        OPTIONS.set(TYPOGRAPHIC_QUOTES, false);
    }

    public static String parse(String html) {
        if (html == null) {
            return null;
        }

        html = StringEscapeUtils.unescapeHtml4(html);
        html = HtmlSanitizer.sanitize(html);
        html = FlexmarkHtmlParser.parse(html, MAX_BLANK_LINES, OPTIONS);

        return removeBr(html);
    }

    /**
     * Flexmark bug: in certain cases <br /> is left unconverted
     */
    private static String removeBr(String html) {
        return html.replaceAll("<br\\s?/>", "");
    }
}
