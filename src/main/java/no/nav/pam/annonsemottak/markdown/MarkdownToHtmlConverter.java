package no.nav.pam.annonsemottak.markdown;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.parser.ParserEmulationProfile;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;


/**
 * One parser, one configuration, same markdown-to-HTML behaviour for all sources.
 */
public class MarkdownToHtmlConverter {

    // Using a standard option profile for markdown.
    private static final DataHolder options = new MutableDataSet()
            .setFrom(ParserEmulationProfile.COMMONMARK);

    public static String parse(String markdown) {
        if (markdown == null) {
            return null;
        }
        String html = HtmlRenderer.builder(options).build()
                .render(Parser.builder(options).build()
                        .parse(markdown));

        return HtmlSanitizer.sanitize(html);
    }
}
