package no.nav.pam.annonsemottak.receivers.dexi;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.pam.annonsemottak.receivers.HttpClientProxy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DexiConfig {

    @Bean
    public DexiConnector dexiConnector(
            HttpClientProxy proxy,
            @Value("${dexi.api.username}") String dexiUsername,
            @Value("${dexi.api.password}") String dexiPassword,
            @Value("${dexi.url}") String dexiUrl,
            ObjectMapper jacksonMapper) {

        return new DexiConnector(proxy, dexiUsername, dexiPassword, dexiUrl, jacksonMapper);
    }
}
