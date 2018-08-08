package no.nav.pam.annonsemottak.annonsemottak.finn;

import okhttp3.HttpUrl;
import org.joda.time.DateTime;

/**
 * Represents ad entry from Finn search result
 */
public class FinnAdHead {

    private String id;
    private String title;
    private HttpUrl link;
    private DateTime published;
    private DateTime updated;
    private DateTime expires;

    public FinnAdHead() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public HttpUrl getLink() {
        return link;
    }

    public void setLink(HttpUrl link) {
        this.link = link;
    }

    public DateTime getPublished() {
        return published;
    }

    public void setPublished(DateTime published) {
        this.published = published;
    }

    public DateTime getUpdated() {
        return updated;
    }

    public void setUpdated(DateTime updated) {
        this.updated = updated;
    }

    public DateTime getExpires() {
        return expires;
    }

    public void setExpires(DateTime expires) {
        this.expires = expires;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FinnAdHead that = (FinnAdHead) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        return link != null ? link.equals(that.link) : that.link == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (link != null ? link.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "FinnAdHead{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", link=" + link +
                ", published=" + published +
                ", updated=" + updated +
                ", expires=" + expires +
                '}';
    }
}
