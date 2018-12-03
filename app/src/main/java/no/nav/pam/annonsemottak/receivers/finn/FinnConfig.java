package no.nav.pam.annonsemottak.receivers.finn;

import no.nav.pam.annonsemottak.receivers.HttpClientProxy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FinnConfig {

    @Bean
    public FinnConnector finnConnector(
            HttpClientProxy proxy,
            @Value("${finn.url}") String serviceDocumentUrl,
            @Value("${finn.api.password}") String apiKey,
            @Value("${finn.polite.delay.millis:200}") int politeRequestDelayInMillis) {

        return new FinnConnector(proxy, serviceDocumentUrl, apiKey, politeRequestDelayInMillis);
    }
}
