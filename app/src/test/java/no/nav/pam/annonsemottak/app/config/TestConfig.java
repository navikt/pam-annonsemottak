package no.nav.pam.annonsemottak.app.config;

import no.nav.pam.annonsemottak.receivers.HttpClientProvider;
import okhttp3.OkHttpClient;
import org.junit.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

@TestConfiguration
public class TestConfig {

    @Primary
    @Bean("proxyHttpClient")
    public HttpClientProvider testClient() {
        try {

            X509TrustManager trustAllX509Manager = mockX509TrustManager();
            SSLContext sc = getSslContext(trustAllX509Manager);
            OkHttpClient client = new OkHttpClient()
                    .newBuilder()
                    .readTimeout(60, TimeUnit.SECONDS)
                    .sslSocketFactory(sc.getSocketFactory(), trustAllX509Manager)
                    .hostnameVerifier((s, sslSession) -> true)
                    .build();
            return new HttpClientProvider(client);
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

    private SSLContext getSslContext(X509TrustManager trustAllX509Manager)
            throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, new TrustManager[]{trustAllX509Manager}, new SecureRandom());
        return sc;
    }
}
