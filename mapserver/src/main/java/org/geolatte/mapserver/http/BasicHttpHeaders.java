package org.geolatte.mapserver.http;

import org.geolatte.mapserver.util.CaseInsensitiveMultiMap;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by Karel Maesen, Geovise BVBA on 04/07/2018.
 */
public class BasicHttpHeaders implements HttpHeaders {

    private final CaseInsensitiveMultiMap headers;

    public BasicHttpHeaders(Map<String, List<String>> headers) {
        this(new CaseInsensitiveMultiMap(headers));
    }

    public BasicHttpHeaders(CaseInsensitiveMultiMap ciMap) {
        this.headers = ciMap.deepCopy();
    }

    @Override
    public List<String> allValues(String name) {
        return headers.allValues(name);
    }

    @Override
    public Optional<String> firstValue(String name) {
        return headers.firstValue(name);
    }

    @Override
    public Map<String, List<String>> map() {
        return headers.map();
    }


}
