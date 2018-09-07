package no.nav.pam.annonsemottak.temp.feedclient;

import no.nav.pam.annonsemottak.Application;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.client.RestTemplate;

@Configuration
@ComponentScan(basePackageClasses = {Application.class}, basePackages = {"no.nav.pam.feed"})
@EnableJpaRepositories(basePackages = {"no.nav.pam.annonsemottak.annonsemottak.externalRun", "no.nav.pam.annonsemottak.stilling", "no.nav.pam.feed"})
@EntityScan(basePackages = {"no.nav.pam.annonsemottak.annonsemottak.externalRun", "no.nav.pam.annonsemottak.stilling", "no.nav.pam.feed"})
public class FeedClientConfig {


    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

}
