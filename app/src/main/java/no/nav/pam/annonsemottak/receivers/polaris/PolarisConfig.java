package no.nav.pam.annonsemottak.receivers.polaris;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.pam.annonsemottak.receivers.HttpClientProxy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PolarisConfig {

    @Bean
    public PolarisConnector polarisConnector(
            HttpClientProxy proxy,
            @Value("${polaris.url}") String polarisUrl,
            ObjectMapper jacksonMapper) {

        return new PolarisConnector(proxy, polarisUrl, jacksonMapper);
    }
}
