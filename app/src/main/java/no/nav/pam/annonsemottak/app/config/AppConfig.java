package no.nav.pam.annonsemottak.app.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import net.javacrumbs.shedlock.spring.ScheduledLockConfiguration;
import net.javacrumbs.shedlock.spring.ScheduledLockConfigurationBuilder;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import no.nav.pam.annonsemottak.Application;
import no.nav.pam.annonsemottak.PathDefinition;
import no.nav.pam.annonsemottak.app.rest.HeaderFilter;
import no.nav.pam.annonsemottak.feed.OptionalValueMixIn;
import no.nav.pam.annonsemottak.feed.StillingMixIn;
import no.nav.pam.annonsemottak.receivers.HttpClientProxy;
import no.nav.pam.annonsemottak.stilling.*;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletPath;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletRegistrationBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.*;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.DispatcherServlet;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.sql.DataSource;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.concurrent.TimeUnit;


@Configuration
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "PT10S")
@EnableTransactionManagement
@ComponentScan(basePackageClasses = {Application.class})
public class AppConfig {

    @Value("${proxy.url}")
    private URL proxyUrl;

    @Bean
    public FilterRegistrationBean headerFilter() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new HeaderFilter());
        filterRegistrationBean.addUrlPatterns(PathDefinition.API + "/*");
        filterRegistrationBean.setEnabled(true);
        return filterRegistrationBean;
    }

    @Bean
    public DispatcherServlet dispatcherServlet() {
        return new DispatcherServlet();
    }

    @Bean
    public DispatcherServletPath dispatcherServletRegistration() {
        return new DispatcherServletRegistrationBean(dispatcherServlet(), "/*");
    }

    @Primary
    @Bean
    public ObjectMapper jacksonMapper() {

        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);


        //Renames selected Stilling field names for the feed
        objectMapper.addMixIn(Stilling.class, StillingMixIn.class);

        // Following classes are wrapped in an Optional and result in nested objects in JSON
        // Removes nesting and assigns String value directly to the property
        objectMapper.addMixIn(Arbeidsgiver.class, OptionalValueMixIn.class);
        objectMapper.addMixIn(Kommentarer.class, OptionalValueMixIn.class);
        objectMapper.addMixIn(Merknader.class, OptionalValueMixIn.class);
        objectMapper.addMixIn(Saksbehandler.class, OptionalValueMixIn.class);

        return objectMapper;
    }

    @Bean
    @Profile({"prod", "dev"})
    // TODO: Using trustall for the client, until we move the app to NAIS. What could possible go wrong?
    public HttpClientProxy getUnsafeClient()
            throws GeneralSecurityException {

        X509TrustManager trustAllX509Manager = mockX509TrustManager();
        SSLContext sc = getSslContext(trustAllX509Manager);
        OkHttpClient client = new OkHttpClient()
                .newBuilder()
                .readTimeout(60, TimeUnit.SECONDS)
                .sslSocketFactory(sc.getSocketFactory(), trustAllX509Manager)
                .hostnameVerifier((s, sslSession) -> true)
                .proxy(proxyUrl == null ? Proxy.NO_PROXY : new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyUrl.getHost(), proxyUrl.getPort())))
                .build();
        HttpClientProxy proxy = new HttpClientProxy();
        proxy.setHttpClient(client);
        return proxy;

    }

    private SSLContext getSslContext(X509TrustManager trustAllX509Manager)
            throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, new TrustManager[]{trustAllX509Manager}, new SecureRandom());
        return sc;
    }

    @Bean
    @Profile({"test"})
    public HttpClientProxy getUnsafeClientUtenProxy() {
        try {

            X509TrustManager trustAllX509Manager = mockX509TrustManager();
            SSLContext sc = getSslContext(trustAllX509Manager);
            OkHttpClient client = new OkHttpClient()
                    .newBuilder()
                    .readTimeout(60, TimeUnit.SECONDS)
                    .sslSocketFactory(sc.getSocketFactory(), trustAllX509Manager)
                    .hostnameVerifier((s, sslSession) -> true)
                    .build();
            HttpClientProxy proxy = new HttpClientProxy();
            proxy.setHttpClient(client);
            return proxy;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private X509TrustManager mockX509TrustManager() {
        return new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };
    }

    @Bean
    public ScheduledLockConfiguration shedLockScheduler(LockProvider lockProvider) {
        return ScheduledLockConfigurationBuilder
                .withLockProvider(lockProvider)
                .withPoolSize(10)
                .withDefaultLockAtMostFor(Duration.ofMinutes(10))
                .build();
    }

    @Bean
    public LockProvider lockProvider(DataSource dataSource) {
        return new JdbcTemplateLockProvider(dataSource);
    }

}
