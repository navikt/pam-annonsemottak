package no.nav.pam.annonsemottak.receivers.finn;


import tools.jackson.databind.ObjectMapper;
import no.nav.pam.annonsemottak.Application;
import no.nav.pam.annonsemottak.TestcontainersConfiguration;
import org.springframework.context.annotation.Import;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

@SpringBootTest(classes = Application.class)
@Import(TestcontainersConfiguration.class)
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
