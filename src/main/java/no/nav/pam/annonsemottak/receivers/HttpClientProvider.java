package no.nav.pam.annonsemottak.receivers;

import okhttp3.OkHttpClient;


public class HttpClientProvider {

    private final OkHttpClient httpClient;

    public HttpClientProvider(OkHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public OkHttpClient get(){
        return httpClient;
    }

}
