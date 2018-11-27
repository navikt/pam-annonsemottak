package no.nav.pam.annonsemottak;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class MapOperations {

    protected Map<String,String> map;

    public MapOperations(Map<String, String> map) {
        this.map = map;
    }

    public void put(String key, String value){
        map.put(key, value);
    }

    public void put(String key, int value) {
        map.put(key, String.valueOf(value));
    }

    @SuppressWarnings("unchecked")
    public void put (Tuple<String, String>...tuples){
        for (Tuple<String,String> tuple : tuples) {
            map.put(tuple.x, tuple.y);
        }
    }

    protected boolean contains(String key) {
        return map.containsKey(key);
    }

    public Optional<String> getOptional(String key) {
        return Optional.ofNullable(map.get(key));
    }

    public String get(String key){
        return getOptional(key).orElse(null);
    }

    public static MapOperations single(String key, String value) {
        Map<String, String> input = new HashMap<>();
        input.put(key, value);
        return new MapOperations(input);
    }

    public Map<String, String> copy() {
        return new HashMap<>(map);
    }

}
