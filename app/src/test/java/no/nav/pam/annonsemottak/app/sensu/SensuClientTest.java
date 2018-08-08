package no.nav.pam.annonsemottak.app.sensu;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

public class SensuClientTest {

    @Test
    public void createsValidSensuEvent() {
        final String sensuEvent = SensuClient.createSensuEvent("myevent", "blablabla");
        assertTrue("contains correct handler", sensuEvent.contains("\"handlers\":[\"events\"]"));
        assertTrue("type is set to metric", sensuEvent.contains("\"type\":\"metric\""));
    }

    @Test
    public void datapointWithoutTagsIsValid() {
        String line = SensuClient.toLineProtocol("measurement", null, ImmutableMap.of("value", 69));
        assertTrue("has no comma after measurement name", line.startsWith("measurement value=69"));
    }

    @Test
    public void supportsMultipleFieldsAndTags() {
        ImmutableMap<String, Object> tags = ImmutableMap.of("tag1", "x", "tag2", "b");
        ImmutableMap<String, Object> fields = ImmutableMap.of("value", 69, "othervalue", "something", "banan", true);
        String line = SensuClient.toLineProtocol("measurement", tags, fields);
        assertTrue("adheres to protocol with multiple fields and tags", line.startsWith("measurement,tag1=x,tag2=b value=69,othervalue=\\\"something\\\",banan=true"));
    }

    @Test
    public void stringFieldsAreEscapedProperly() {
        ImmutableMap<String, Object> fields = ImmutableMap.of("value", 69, "othervalue", "6.9.0", "banan", true);
        final String event = SensuClient.createSensuEvent("measurement", SensuClient.toLineProtocol("measurement", null, fields));
        assertTrue("fields with type String must be escaped to survive transport", event.contains("measurement value=69,othervalue=\\\"6.9.0\\\",banan=true"));
    }

    @Test(expected = RuntimeException.class)
    public void noFieldsInDatapointYieldsRuntimeException() {
        SensuClient.toLineProtocol("measurement", ImmutableMap.of("x", "y"), null);
    }
}