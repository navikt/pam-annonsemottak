package no.nav.pam.annonsemottak.rest.payloads;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static junit.framework.TestCase.assertEquals;

public class ErrorPayloadTest {

    @Test
    public void checkCorrectErrorCodes()
            throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        for (ErrorPayload.DefinedErrors error : ErrorPayload.DefinedErrors.values()) {
            String json = mapper.writeValueAsString(new ErrorPayload(error));
            assertEquals("{\"data\":null,\"meta\":{\"error\":{\"code\":\"" + error.getCode() + "\"}}}", json);
        }
    }

}
