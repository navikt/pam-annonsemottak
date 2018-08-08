package no.nav.pam.annonsemottak.annonsemottak.finn;

import okhttp3.HttpUrl;
import org.w3c.dom.Document;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

class FinnServiceDocument {

    private final XPath xPath = XPathFactory.newInstance().newXPath();
    private final Document document;

    FinnServiceDocument(Document document) {
        this.document = document;
    }

    HttpUrl getHrefFromCollectionInWorkspace(String workspaceTitle, String collectionTitle)
            throws XPathExpressionException {
        return HttpUrl.parse((String) xPath
                .compile("/service/workspace[title='" + workspaceTitle + "']/collection[title='" + collectionTitle + "']/@href")
                .evaluate(document, XPathConstants.STRING));
    }

}
