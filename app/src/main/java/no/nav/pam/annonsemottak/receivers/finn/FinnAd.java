package no.nav.pam.annonsemottak.receivers.finn;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FinnAd {

    private final XPath xPath = XPathFactory.newInstance().newXPath();

    private final String id;
    private final String url;
    private final String title;
    private final String updated;
    private final String published;
    private final String dateSubmitted;
    private final String expires;
    private final String edited;
    private final String identifier;
    private final boolean isPrivate;
    private final String type;
    private final boolean active;
    private final boolean disposed;
    private final List<String> linkToApply;
    private final Location location;
    private final Author author;
    private final String advertiserReference;
    private final String applicationAddress;
    private final String applicationDeadline;
    private final String applicationEmail;
    private final String applicationLabel;
    private final List<Category> categories;
    private final Company company;
    private final String duration;
    private final List<GeneralText> generalText;
    private final List<String> industry;
    private final String jobTitle;
    private final List<String> keywords;
    private final String managerRole;
    private final String providerId;
    private final String sector;
    private final String situation;
    private final String startDate;
    private final List<String> workplaces;
    private final String no_of_positions;
    private final String extent;
    private final String externalAdId;
    private final List<Contact> contacts;
    private final GeoLocation geoLocation;
    private final List<String> logoUrlList;
    private final Set<String> occupations;


    FinnAd(Document document)
            throws XPathExpressionException, FinnConnectorException {
        id = getString(document, "/entry/id");
        url = getString(document, "/entry/link[@rel='alternate']/@href");
        title = getString(document, "/entry/title");
        updated = getString(document, "/entry/updated");
        published = getString(document, "/entry/published");
        dateSubmitted = getString(document, "/entry/dateSubmitted");
        expires = getString(document, "/entry/expires");
        edited = getString(document, "/entry/edited");
        identifier = getString(document, "/entry/identifier");
        isPrivate = getString(document, "/entry/category[@scheme='urn:finn:ad:private']/@term").equals("true");
        type = getString(document, "/entry/category[@scheme='urn:finn:ad:type']/@term");
        active = getString(document, "/entry/category[@scheme='urn:finn:ad:status']/@term").equals("activated");
        disposed = getString(document, "/entry/category[@scheme='urn:finn:ad:disposed']/@term").equals("true");
        linkToApply = getListOfStrings(document, "/entry/link[@rel='http://api.finn.no/relations/apply']/@href");
        geoLocation = buildGeoLocation(document);
        throwExceptionIfNonUniqueXpath(document, "/entry/location");
        location = new Location(
                getString(document, "/entry/location/address"),
                getString(document, "/entry/location/postal-code"),
                getString(document, "/entry/location/city"),
                getString(document, "/entry/location/country")
        );
        throwExceptionIfNonUniqueXpath(document, "/entry/author");
        author = new Author(
                getString(document, "/entry/author/name"),
                getString(document, "/entry/author/uri"),
                getString(document, "/entry/author/identifier"),
                getString(document, "/entry/author/externalref"),
                getString(document, "/entry/author/content[category='MAIN']/@url"),
                getString(document, "/entry/author/content[category='LIST']/@url")
        );
        contacts = getListOfContacts(getNodeList(document, "/entry/contact"));
        advertiserReference = getString(document, "/entry/adata/field[@name='advertiser_reference']/@value");
        applicationDeadline = getString(document, "/entry/adata/field[@name='application_deadline']/@value");
        applicationEmail = getString(document, "/entry/adata/field[@name='application_email']/@value");
        applicationLabel = getString(document, "/entry/adata/field[@name='application_label']/@value");
        categories = getListOfCategories(getNodeList(document, "/entry/adata/field[@name='categories']/value"));
        throwExceptionIfNonUniqueXpath(document, "/entry/field[@name='company']");
        company = new Company(
                getString(document, "/entry/adata/field[@name='company']/field[@name='name']"),
                getString(document, "/entry/adata/field[@name='company']/field[@name='ingress']"),
                getString(document, "/entry/adata/field[@name='company']/field[@name='url']/@value")
        );
        duration = getString(document, "/entry/adata/field[@name='duration']/@value");
        // Skipping external_ad_id for now.
        generalText = getListOfGeneralText(getNodeList(document, "/entry/adata/field[@name='general_text']/value"));
        industry = getListOfStrings(document, "/entry/adata/field[@name='industry']/value");
        jobTitle = getString(document, "/entry/adata/field[@name='job_title']");
        keywords = getListOfStrings(document, "/entry/adata/field[@name='keywords']/value");
        managerRole = getString(document, "/entry/adata/field[@name='manager_role']/@value");
        occupations = getListOfStrings(document, "/entry/adata/field[@name='occupations']/value/field[@name='general']").stream().collect(Collectors.toSet());
        providerId = getString(document, "/entry/adata/field[@name='provider_id']/@value");
        sector = getString(document, "/entry/adata/field[@name='sector']/@value");
        situation = getString(document, "/entry/adata/field[@name='situation']/@value");
        startDate = getString(document, "/entry/adata/field[@name='start_date']/@value");
        workplaces = getListOfStrings(document, "/entry/adata/field[@name='workplaces']/value");
        applicationAddress = getString(document, "/entry/adata/field[@name='application_address']/@value");
        no_of_positions = getString(document, "/entry/adata/field[@name='no_of_positions']/@value");
        extent = getString(document, "/entry/adata/field[@name='extent']/@value");
        externalAdId = getString(document, "/entry/adata/field[@name='external_ad_id']/@value");
        logoUrlList = getListOfStrings(document, "/entry/content[category='LOGO']/@url");
    }

    private GeoLocation buildGeoLocation(Node node) throws XPathExpressionException, FinnConnectorException {
        String accuracy = getString(node, "/entry/point/@accuracy");
        String coordinates = getString(node, "/entry/point").trim();

        if (coordinates.matches("^[-+]?[0-9]*\\.?[0-9]+ [-+]?[0-9]*\\.?[0-9]+$")) {
            String[] split = coordinates.split(" ");
            return new GeoLocation(accuracy, split[0].trim(), split[1].trim());
        } else {
            return null;
        }
    }

    private List<Category> getListOfCategories(NodeList nodes) {
        return new ArrayList<>(0); // TODO: Work halted due to other coding, finish up and continue on other extension fields (see ad.json model).
    }

    private List<Contact> getListOfContacts(NodeList nodes) throws XPathExpressionException {
        List contacts = new ArrayList<>();

        for (int i = 0; i < nodes.getLength(); i++) {
            String name = getNonUniqueString(nodes.item(i), "./name");
            String work = getNonUniqueString(nodes.item(i), "./phone-number[@type='work']");
            String mobile = getNonUniqueString(nodes.item(i), "./phone-number[@type='mobile']");
            String email = getNonUniqueString(nodes.item(i), "./email");
            String title = getNonUniqueString(nodes.item(i), "./@title");

            contacts.add(new Contact(name, email, work, mobile, title));
        }
        return contacts;
    }

    private String getNonUniqueString(Node node, String expression)
            throws XPathExpressionException {
        String value = (String) xPath.compile(expression).evaluate(node, XPathConstants.STRING);

        return (StringUtils.isNoneBlank(value)) ? value : null;
    }

    private List<GeneralText> getListOfGeneralText(NodeList nodes)
            throws XPathExpressionException, FinnConnectorException {
        List<GeneralText> list = new ArrayList<>();
        for (int i = 0; i < nodes.getLength(); i++) {
            list.add(new GeneralText(
                    getString(nodes.item(i), "field[@name='title']/@value"),
                    getString(nodes.item(i), "field[@name='value']")));
        }
        return list;
    }

    private String getString(Node node, String expression)
            throws XPathExpressionException, FinnConnectorException {
        throwExceptionIfNonUniqueXpath(node, expression);
        return (String) xPath.compile(expression).evaluate(node, XPathConstants.STRING);
    }

    private void throwExceptionIfNonUniqueXpath(Node node, String expression)
            throws XPathExpressionException, FinnConnectorException {
        if (getNodeList(node, expression).getLength() > 1) {
            throw new FinnConnectorException("Non-unique XPath expression " + expression + " in ad " + id);
        }
    }

    private NodeList getNodeList(Node node, String expression)
            throws XPathExpressionException {
        return (NodeList) xPath.compile(expression).evaluate(node, XPathConstants.NODESET);
    }

    private List<String> getListOfStrings(Node node, String expression)
            throws XPathExpressionException {
        NodeList nl = getNodeList(node, expression);
        List<String> list = new ArrayList<>(nl.getLength());
        for (int i = 0; i < nl.getLength(); i++) {
            list.add(nl.item(i).getTextContent());
        }
        return list;
    }

    String getId() {
        return id;
    }

    String getUrl() {
        return url;
    }

    String getTitle() {
        return title;
    }

    String getUpdated() {
        return updated;
    }

    String getPublished() {
        return published;
    }

    String getDateSubmitted() {
        return dateSubmitted;
    }

    String getExpires() {
        return expires;
    }

    String getEdited() {
        return edited;
    }

    String getIdentifier() {
        return identifier;
    }

    boolean isPrivate() {
        return isPrivate;
    }

    String getType() {
        return type;
    }

    boolean isActive() {
        return active;
    }

    boolean isDisposed() {
        return disposed;
    }

    List<String> getLinkToApply() {
        return linkToApply;
    }

    Location getLocation() {
        return location;
    }

    Author getAuthor() {
        return author;
    }

    String getAdvertiserReference() {
        return advertiserReference;
    }

    String getApplicationAddress() {
        return applicationAddress;
    }

    String getApplicationDeadline() {
        return applicationDeadline;
    }

    String getApplicationEmail() {
        return applicationEmail;
    }

    String getApplicationLabel() {
        return applicationLabel;
    }

    Company getCompany() {
        return company;
    }

    String getDuration() {
        return duration;
    }

    List<GeneralText> getGeneralText() {
        return generalText;
    }

    List<String> getIndustry() {
        return industry;
    }

    String getJobTitle() {
        return jobTitle;
    }

    List<String> getKeywords() {
        return keywords;
    }

    String getManagerRole() {
        return managerRole;
    }

    String getProviderId() {
        return providerId;
    }

    String getSector() {
        return sector;
    }

    String getSituation() {
        return situation;
    }

    String getStartDate() {
        return startDate;
    }

    List<String> getWorkplaces() {
        return workplaces;
    }

    String getNo_of_positions() {
        return no_of_positions;
    }

    String getExtent() {
        return extent;
    }

    String getExternalAdId() {
        return externalAdId;
    }

    List<Contact> getContacts() {
        return contacts;
    }

    public GeoLocation getGeoLocation() {
        return geoLocation;
    }

    public List<String> getLogoUrlList() {
        return logoUrlList;
    }

    public Set<String> getOccupations() {
        return occupations;
    }

    static class Location {
        private final String address;
        private final String postalCode;
        private final String city;
        private final String country;

        private Location(String address, String postalCode, String city, String country) {
            this.address = address;
            this.postalCode = postalCode;
            this.city = city;
            this.country = country;
        }

        String getAddress() {
            return address;
        }

        String getPostalCode() {
            return postalCode;
        }

        String getCity() {
            return city;
        }

        String getCountry() {
            return country;
        }
    }

    static class Company {
        private final String name;
        private final String ingress;
        private final String url;

        private Company(String name, String ingress, String url) {
            this.name = name;
            this.ingress = ingress;
            this.url = url;
        }

        String getName() {
            return name;
        }

        String getIngress() {
            return ingress;
        }

        String getUrl() {
            return url;
        }
    }

    static class GeoLocation {
        private final String accuracy;
        private final String latitude;
        private final String longitude;

        GeoLocation(String accuracy, String latitude, String longitude) {
            this.accuracy = accuracy;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public String getAccuracy() {
            return accuracy;
        }

        public String getLongitude() {
            return longitude;
        }

        public String getLatitude() {
            return latitude;
        }
    }

    static class Author {
        private final String name;
        private final String uri;
        private final String identifier;
        private final String externalref;
        private final String urlMainLogo;
        private final String urlListLogo;

        private Author(String name, String uri, String identifier, String externalref, String urlMainLogo, String urlListLogo) {
            this.name = name;
            this.uri = uri;
            this.identifier = identifier;
            this.externalref = externalref;
            this.urlMainLogo = urlMainLogo;
            this.urlListLogo = urlListLogo;
        }

        public String getName() {
            return name;
        }

        public String getUri() {
            return uri;
        }

        public String getIdentifier() {
            return identifier;
        }

        public String getExternalref() {
            return externalref;
        }

        public String getUrlMainLogo() {
            return urlMainLogo;
        }

        public String getUrlListLogo() {
            return urlListLogo;
        }
    }

    static class GeneralText {
        private final String title;
        private final String value;

        private GeneralText(String title, String value) {
            this.title = title != null && !title.isEmpty() ? title : null;
            this.value = value != null && !value.isEmpty() ? value : null;
        }

        public String getTitle() {
            return title;
        }

        public String getValue() {
            return value;
        }
    }

    static class Category {
        private final String main;
        private final String sub;

        private Category(String main, String sub) {
            this.main = main;
            this.sub = sub;
        }
    }

    static class Contact {
        private final String name;
        private final String email;
        private final String phone_work;
        private final String phone_mobile;
        private final String title;

        Contact(String name, String email, String phone_work, String phone_mobile, String title) {
            this.name = name;
            this.email = email;
            this.phone_work = phone_work;
            this.phone_mobile = phone_mobile;
            this.title = title;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public String getPhone_work() {
            return phone_work;
        }

        public String getPhone_mobile() {
            return phone_mobile;
        }

        public String getTitle() {
            return title;
        }
    }
}
