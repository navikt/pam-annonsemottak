package no.nav.pam.annonsemottak.receivers.finn;

import okhttp3.HttpUrl;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class FinnSearchResultsHandler extends DefaultHandler {

    private String nextPageUrl = null;
    private String lastPageUrl = null;

    private Set<FinnAdHead> finnAdHeads = new HashSet<>();
    private FinnAdHead currentAdHead;
    private String currentElement;
    private StringBuilder currentElementValue;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {

        currentElement = qName;
        currentElementValue = new StringBuilder();

        if (currentAdHead == null) {
            if (qName.equalsIgnoreCase("link")) {
                switch (attributes.getValue("rel")) {
                    case "next":
                        nextPageUrl = attributes.getValue("href");
                        break;
                    case "last":
                        lastPageUrl = attributes.getValue("href");
                        break;
                }
            } else if (qName.equalsIgnoreCase("entry")) {
                currentAdHead = new FinnAdHead();
            }
        } else {
            if (qName.equalsIgnoreCase("link")) {
                if ("self".equals(attributes.getValue("rel"))) {
                    currentAdHead.setLink(HttpUrl.parse(attributes.getValue("href")));
                }
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {

        if (qName.equalsIgnoreCase("entry")) {
            finnAdHeads.add(currentAdHead);
            currentAdHead = null;
        }

        if (currentAdHead != null) {
            switch (qName.toLowerCase()) {
                case "dc:identifier":
                    currentAdHead.setId(currentElementValue.toString());
                    break;
                case "title":
                    currentAdHead.setTitle(currentElementValue.toString());
                    break;
                case "updated":
                    currentAdHead.setUpdated(FinnDateConverter.convertDate(currentElementValue.toString()));
                    break;
                case "published":
                    currentAdHead.setPublished(FinnDateConverter.convertDate(currentElementValue.toString()));
                    break;
                case "age:expires":
                    currentAdHead.setExpires(FinnDateConverter.convertDate(currentElementValue.toString()));
                    break;
                default:
                    break;
            }
        }

        currentElement = null;
        currentElementValue = null;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {

        if (currentAdHead != null && currentElement != null) {
            switch (currentElement.toLowerCase()) {
                case "dc:identifier":
                case "title":
                case "updated":
                case "published":
                case "age:expires":
                    currentElementValue.append(new String(ch, start, length));
                default:
                    break;
            }
        }
    }

    Optional<HttpUrl> getNextPageUrl() {
        return Optional.ofNullable(nextPageUrl == null ? null : HttpUrl.parse(nextPageUrl));
    }

    public Set<FinnAdHead> getFinnAdHeads() {
        return finnAdHeads;
    }
}
