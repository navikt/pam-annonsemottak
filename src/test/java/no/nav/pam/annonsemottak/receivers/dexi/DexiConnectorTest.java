package no.nav.pam.annonsemottak.receivers.dexi;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.junit.jupiter.MockitoExtension;

import java.io.FileReader;
import java.io.Reader;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@ExtendWith(MockitoExtension.class)
public class DexiConnectorTest {


    @Test
    public void basicDeserializationOfDexiIoJson()
            throws Exception {
        DexiConnector connector = new DexiConnector(null,null,null,null, new ObjectMapper());
        try (Reader reader = new FileReader("src/test/resources/dexi.io/samples/7d7d3a19-b9f1-4ead-ac2e-37612a415826.json")) {
            Map results = connector.deserialize(reader);
            assertEquals(3, results.size());
            assertTrue(results.keySet().contains("headers"));
            assertTrue(results.keySet().contains("rows"));
            assertTrue(results.keySet().contains("totalRows"));
        }
    }

}
