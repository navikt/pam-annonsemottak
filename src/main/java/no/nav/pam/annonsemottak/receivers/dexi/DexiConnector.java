package no.nav.pam.annonsemottak.receivers.dexi;


import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.pam.annonsemottak.receivers.HttpClientProvider;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.inject.Named;
import java.io.IOException;
import java.io.Reader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DexiConnector {

    private final ObjectMapper objectMapper;

    private final String DEXI_ACCOUNT_ID;
    private final String API_ENDPOINT;
    private final HttpClientProvider clientProvider;
    private final String md5;

    @Autowired
    public DexiConnector(
            @Named("proxyHttpClient") final HttpClientProvider clientProvider,
            @Value("${dexi.api.username}") final String dexiAccount,
            @Value("${dexi.api.password}") final String dexiApikey,
            @Value("${dexi.url}") final String dexiEndpoint,
            final ObjectMapper jacksonMapper) {
        this.objectMapper = jacksonMapper;
        this.clientProvider = clientProvider;
        this.DEXI_ACCOUNT_ID = dexiAccount;
        this.API_ENDPOINT = dexiEndpoint;
        try {
            String input = DEXI_ACCOUNT_ID + dexiApikey;
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
        try (Response response = clientProvider.get().newCall(request).execute()) {
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
                content = (o != null) ? o.toString() : null;
            }

            map.put(headers.get(i), content);
        }

        //Remove Dexi Pipe input headers as these are unnecessary
        map.remove("url");
        map.remove("headers");
        map.remove("body");

        return map;
    }

    public boolean isPingSuccessful() {
        try {
            return clientProvider.get().newCall(getUrl("runs/", null)).execute().isSuccessful();
        } catch (IOException e) {
            return false;
        }

    }
}
