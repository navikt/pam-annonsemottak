package no.nav.pam.annonsemottak.rest.payloads;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ErrorPayloadTest {

    @Test
    public void checkCorrectErrorCodes()
            throws Exception {
        ObjectMapper mapper = new JsonMapper();
        for (ErrorPayload.DefinedErrors error : ErrorPayload.DefinedErrors.values()) {
            String json = mapper.writeValueAsString(new ErrorPayload(error));
            assertEquals("{\"data\":null,\"meta\":{\"error\":{\"code\":\"" + error.getCode() + "\"}}}", json);
        }
    }

}
