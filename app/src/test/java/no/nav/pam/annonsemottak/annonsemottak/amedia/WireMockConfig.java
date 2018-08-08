package no.nav.pam.annonsemottak.annonsemottak.amedia;


import com.github.tomakehurst.wiremock.WireMockServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@Profile({"test"})
@Configuration
public class WireMockConfig {

    @Bean
    public WireMockServer wireMockServer() {

        WireMockServer wireMockServer = new WireMockServer(7010);

        wireMockServer.stubFor(get(urlMatching(
            ".*?transaction_type:11.*?sort=system_modified_time:asc.*?size=10000.*?_source=false"))
            .willReturn(aResponse().withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(getMockResponse("idResultat"))));

        wireMockServer.stubFor(get(urlMatching(
            ".*?transaction_type:11.*?sort=system_modified_time:asc.*?size="
                + AmediaService.MAXANTALl_TREFF
                + ".*?_source=true"))
            .willReturn(aResponse().withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(getMockResponse("enkeltResultat"))));

        wireMockServer.start();
        return wireMockServer;
    }

    private String getMockResponse(String fil) {
        return new BufferedReader(
            new InputStreamReader(AmediaResponseMapperTest.class.getClassLoader()
                .getResourceAsStream("amedia.io.samples/" + fil + ".json")))
            .lines().collect(Collectors.joining("\n"));
    }


}
