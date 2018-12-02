package no.nav.pam.annonsemottak.rest.payloads;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.Base64;

/**
 * Adds {@code etag} meta data, based on MD5 of JSON representation of content in {@code data}.
 *
 * @param <T> As for {@link BasicPayload}. Will be serialized to JSON with Jackson in order to get content for MD5 checksum/ETag value.
 */
@Configurable
public class EtaggedPayload<T> extends BasicPayload<T> {

    private static final Logger LOG = LoggerFactory.getLogger(EtaggedPayload.class);

    @Qualifier("jacksonMapper")
    @Autowired
    private ObjectMapper objectMapper;

    private EtagMeta meta;

    public EtaggedPayload(T data) {
        super(data);
        this.meta = new EtagMeta();
    }

    @JsonProperty("meta")
    public EtagMeta getMeta() {
        return meta;
    }

    /**
     * Adds {@code etag} field, generated on the fly to reflect changes done in underlying data. The API should make
     * sure to add the value of {@link #getEtag()} to the HTTP header "ETag".
     */
    public class EtagMeta extends Meta {

        @JsonProperty("etag")
        public String getEtag() {
            // Generated on the fly, to reflect changes in data.
            if (getData() == null) {
                return null;
            }
            try {
                final MessageDigest md5 = MessageDigest.getInstance("MD5");
                md5.update(
                       objectMapper
                                .writeValueAsString(getData())
                                .getBytes(Charset.forName("UTF-8"))
                );
                return new String(
                        Base64.
                                getEncoder()
                                .encode(md5.digest())
                );
            } catch (Exception e) {
                LOG.error("Unable to generate ETag value", e);
                return null;
            }
        }

    }

}
