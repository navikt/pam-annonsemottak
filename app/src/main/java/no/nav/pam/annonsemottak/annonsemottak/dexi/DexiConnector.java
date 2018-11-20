package no.nav.pam.annonsemottak.annonsemottak.dexi;


import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.pam.annonsemottak.Tuple;
import no.nav.pam.annonsemottak.annonsemottak.HttpClientProxy;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.io.Reader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DexiConnector {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final String DEXI_ACCOUNT_ID;
    private final String DEXI_API_KEY;
    private final String API_ENDPOINT;
    private final HttpClientProxy proxy;
    private final String md5;

    public DexiConnector(HttpClientProxy proxy, String dexiAccount, String dexiApikey, String dexiEndpoint)
    {
        this.proxy = proxy;
        this.DEXI_ACCOUNT_ID=dexiAccount;
        this.DEXI_API_KEY=dexiApikey;
        this.API_ENDPOINT=dexiEndpoint;
        try {
            String input = DEXI_ACCOUNT_ID + DEXI_API_KEY;
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(input.getBytes(), 0, input.length());
            md5 = new BigInteger(1, digest.digest()).toString(16);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    List<DexiConfiguration> getConfigurations(String filterConfiguration) throws IOException {
        return getRuns(new Tuple<>("limit", "1000")).stream()
                .map(m -> new DexiConfiguration(
                        m.get("robotId"),
                        m.get("robotName"),
                        m.get("_id"),
                        m.get("name")))
                .filter(dexiConfiguration -> dexiConfiguration.getJobName().equals(filterConfiguration))
                .collect(Collectors.toList());
    }

    List<Map<String, String>> getRuns(Tuple... params)
            throws IOException {
        return (List<Map<String, String>>) execute(getUrl("runs/", params)).get("rows");
    }

    private Map getLatestResultsFor(String id)
            throws IOException {
        return execute(getUrl("runs/" + id + "/latest/result"));

    }

    private Map execute(Request request)
            throws IOException {
        try (Response response = proxy.getHttpClient().newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code " + response.code());
            }
            return deserialize(response.body().charStream());
        }
    }

    Map deserialize(Reader reader) throws IOException {
        return objectMapper.readValue(reader, Map.class);
    }

    private Request getUrl(String path, Tuple<String, String>... params) {
        HttpUrl.Builder builder = HttpUrl.parse(API_ENDPOINT).newBuilder().addPathSegments(path);
        if (params != null) {
            Arrays.stream(params).forEach(t -> builder.addQueryParameter(t.x, t.y));
        }
        HttpUrl url = builder.build();
        return new Request.Builder()
                .url(url)
                .addHeader("X-DexiIO-Access", md5)
                .addHeader("X-DexiIO-Account", DEXI_ACCOUNT_ID)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .build();
    }

    List<Map<String, String>> getLatestResultForJobID(String id)
            throws IOException {
        return convertToProperJson(getLatestResultsFor(id));
    }

    List<Map<String, String>> convertToProperJson(Map map) {
        List<String> headers = (List<String>) map.get("headers");
        List<List<Object>> rows = (List<List<Object>>) map.get("rows");

        return rows.stream()
                .map(row -> toMap(headers, row))
                .collect(Collectors.toList());
    }

    private Map<String, String> toMap(List<String> headers, List<Object> row) {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < row.size(); i++) {
            Object o = row.get(i);
            String content;
            if (o instanceof List) {
                content = ((List) o).stream().collect(Collectors.joining(",")).toString();
            } else {
                content = (o != null)? o.toString() : null;
            }

            map.put(headers.get(i), content);
        }

        //Remove Dexi Pipe input headers as these are unnecessary
        map.remove("url");
        map.remove("headers");
        map.remove("body");

        return map;
    }

    public boolean isPingSuccessful(){
        try {
            return proxy.getHttpClient().newCall(getUrl("runs/", null)).execute().isSuccessful();
        } catch (IOException e) {
            return false;
        }

    }
}
