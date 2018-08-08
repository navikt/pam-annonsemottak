package no.nav.pam.annonsemottak.app.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import net.javacrumbs.shedlock.spring.SpringLockableTaskSchedulerFactory;
import no.nav.pam.annonsemottak.Application;
import no.nav.pam.annonsemottak.annonsemottak.HttpClientProxy;
import no.nav.pam.annonsemottak.annonsemottak.amedia.AmediaConnector;
import no.nav.pam.annonsemottak.annonsemottak.dexi.DexiConnector;
import no.nav.pam.annonsemottak.annonsemottak.finn.FinnConnector;
import no.nav.pam.annonsemottak.api.PathDefinition;
import no.nav.pam.annonsemottak.app.rest.HeaderFilter;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletPath;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletRegistrationBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.*;
import org.springframework.scheduling.TaskScheduler;
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
import java.util.concurrent.TimeUnit;


@Configuration
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

        return new ObjectMapper()
                .registerModule(new JodaModule())
                .registerModule(new Jdk8Module());
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
    public DexiConnector dexiConnector(HttpClientProxy proxy,
                                       @Value("${dexi.api.username}") String dexiUsername,
                                       @Value("${dexi.api.password}") String dexiPassword,
                                       @Value("${dexi.url}") String dexiUrl) {
        return new DexiConnector(proxy, dexiUsername, dexiPassword, dexiUrl);
    }

    @Bean
    public FinnConnector finnConnector(
            HttpClientProxy proxy,
            @Value("${finn.service.document.url:https://cache.api.finn.no/iad}") String serviceDocumentUrl,
            @Value("${finn.api.password:someboguskey}") String apiKey,
            @Value("${finn.polite.delay.millis:200}") int politeRequestDelayInMillis) {
        return new FinnConnector(proxy, serviceDocumentUrl, apiKey, politeRequestDelayInMillis);
    }

    @Bean
    public AmediaConnector amediaConnector(HttpClientProxy proxy,
                                           @Value("${amedia.url}") String amediaUrl) {

        return new AmediaConnector(proxy, amediaUrl);
    }

    @Bean
    public TaskScheduler taskScheduler(LockProvider lockProvider) {
        return SpringLockableTaskSchedulerFactory.newLockableTaskScheduler(10, lockProvider);
    }

    @Bean
    public LockProvider lockProvider(DataSource dataSource) {
        return new JdbcTemplateLockProvider(dataSource);
    }

}
