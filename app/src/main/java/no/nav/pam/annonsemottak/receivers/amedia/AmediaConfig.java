package no.nav.pam.annonsemottak.receivers.amedia;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.pam.annonsemottak.receivers.HttpClientProxy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmediaConfig {

    @Bean
    public AmediaConnector amediaConnector(
            HttpClientProxy proxy,
            @Value("${amedia.url}") String amediaUrl,
            ObjectMapper jacksonMapper) {

        return new AmediaConnector(proxy, amediaUrl, jacksonMapper);
    }
}
