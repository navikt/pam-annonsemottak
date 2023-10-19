package no.nav.pam.annonsemottak.receivers.finn;


import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.pam.annonsemottak.Application;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.util.Set;

import static no.nav.pam.annonsemottak.receivers.finn.FinnService.KNOWN_COLLECTIONS;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
public class FinnServiceIT {


    @Autowired FinnConnector finnConnector;
    @Autowired FinnService finnService;
    @Autowired ObjectMapper objectMapper;

    @Test
    public void start() throws Exception {
        // Good when debug integration against FINN
        Set<FinnAdHead> finnAdHeads = finnConnector.fetchSearchResult();
        //FileUtils.write(new File("finnAdHeads.json"), objectMapper.writeValueAsString(finnAdHeads));
    }
}
