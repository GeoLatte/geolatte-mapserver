package org.geolatte.mapserver.util;

import java.util.*;

import static java.util.stream.Collectors.toMap;

/**
 * Created by Karel Maesen, Geovise BVBA on 04/07/2018.
 */
public class CaseInsensitiveMultiMap {

    final private Map<String, List<String>> map;

    public CaseInsensitiveMultiMap(Map<String, List<String>> map) {
        this.map = deepCopy(map);
    }

    public CaseInsensitiveMultiMap() {
        this(new HashMap<>());
    }

    public void put(String key, String... value) {
        List<String> vals = this.map.computeIfAbsent(key, k -> new ArrayList<>());
        vals.addAll(Arrays.asList(value));
    }

    /**
     * Creates a deep clone of this instance
     * @return
     */
    public CaseInsensitiveMultiMap deepCopy() {
        return new CaseInsensitiveMultiMap( deepCopy(map) );
    }

    public List<String> allValues(String name) {
        for( String key: map.keySet()) {
            if (key.equalsIgnoreCase(name)) {
                return Collections.unmodifiableList(map.get(key));
            }
        }
        return Collections.emptyList();
    }


    public Optional<String> firstValue(String name) {
        for( String key: map.keySet()) {
            if (key.equalsIgnoreCase(name)) {
                List<String> values = map.get(key);
                return values.isEmpty() ? Optional.empty() : Optional.of(values.get(0));
            }
        }
        return Optional.empty();
    }

    public Map<String, List<String>> map() {
        return Collections.unmodifiableMap(map);
    }


    public Set<String> keySet() {
        return map.keySet();
    }

    private static Map<String, List<String>> deepCopy(Map<String, List<String>> m) {
        return m.entrySet().stream().collect(toMap(Map.Entry::getKey, e -> new ArrayList<>(e.getValue())));
    }

}
