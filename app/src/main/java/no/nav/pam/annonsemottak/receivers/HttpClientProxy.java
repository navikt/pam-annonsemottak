package no.nav.pam.annonsemottak.receivers;

import okhttp3.OkHttpClient;


public class HttpClientProxy {

    private OkHttpClient httpClient;

    public OkHttpClient getHttpClient(){
        return httpClient;
    }

    public void setHttpClient(OkHttpClient client){
        this.httpClient = client;
    }
}
