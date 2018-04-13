package org.geolatte.mapserver.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * A simple model for an Http Request
 *
 * Created by Karel Maesen, Geovise BVBA on 13/04/2018.
 */
public class HttpRequest {

    private final Map<String, List<String>> headers;
    private final Map<String, String> queryParams;


    public HttpRequest(Map<String, List<String>> headers, Map<String, String> queryParams) {
        this.headers = headers;
        this.queryParams = queryParams;
    }


    public Optional<String> getQueryParam(String paramName) {
        return Optional.ofNullable(queryParams.get(paramName));
    }

    public List<String> getHeader(String header) {
        return headers.getOrDefault(header, new ArrayList<>());
    }
}
