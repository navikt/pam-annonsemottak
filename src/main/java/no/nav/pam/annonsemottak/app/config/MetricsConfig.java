package no.nav.pam.annonsemottak.app.config;

import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.jpa.HibernateMetrics;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.springframework.boot.actuate.metrics.web.client.RestTemplateExchangeTags;
import org.springframework.boot.actuate.metrics.web.client.RestTemplateExchangeTagsProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpResponse;

import java.util.Arrays;
import java.util.Collections;

import static org.slf4j.LoggerFactory.getLogger;

@Configuration
public class MetricsConfig {

    private static final Logger LOG = getLogger(MetricsConfig.class);
    @Bean
    public RestTemplateExchangeTagsProvider restTemplateTagConfigurer() {
        LOG.info("Limiting tags in rest template");
        return new CustomRestTemplateExchangeTagsProvider();
    }

    private static class CustomRestTemplateExchangeTagsProvider implements RestTemplateExchangeTagsProvider {
        @Override
        public Iterable<Tag> getTags(String urlTemplate, org.springframework.http.HttpRequest request, ClientHttpResponse response) {
            // we only use path for tags, because of hitting a limit of tags. The cardinality for uri might cause issue.
            return Arrays.asList(
                    RestTemplateExchangeTags.method(request),
                    RestTemplateExchangeTags.uri(request.getURI().getPath()),
                    RestTemplateExchangeTags.status(response),
                    RestTemplateExchangeTags.clientName(request));
        }
    }

    @Bean
    public HibernateMetrics hibernateMetrics(SessionFactory sessionFactory) {
        return new HibernateMetrics(sessionFactory, "em", Collections.emptyList());
    }
}
