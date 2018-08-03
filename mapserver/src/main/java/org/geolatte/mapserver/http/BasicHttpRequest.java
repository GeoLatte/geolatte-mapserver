package org.geolatte.mapserver.http;

import org.geolatte.mapserver.util.CaseInsensitiveMultiMap;
import org.python.google.common.base.Strings;

import java.net.URI;
import java.util.*;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

/**
 * Created by Karel Maesen, Geovise BVBA on 04/07/2018.
 */
public class BasicHttpRequest implements HttpRequest {

    private final URI uri;
    private final String method;
    private final HttpHeaders headers;

    BasicHttpRequest(URI uri, String method, Map<String, List<String>> headers) {
        if (uri == null) {
            throw new IllegalArgumentException("URI cannot be null");
        }
        this.uri = uri;
        if (Strings.isNullOrEmpty(method)) {
            throw new IllegalArgumentException("Method parameter cannot be null or empty");
        }
        this.method = method.toUpperCase();
        Map<String,List<String>> headersMap = headers == null ? Collections.emptyMap() : headers;
        this.headers = new BasicHttpHeaders(headersMap);
    }

    @Override
    public URI uri() {
        return uri;
    }

    @Override
    public String method() {
        return method;
    }

    @Override
    public HttpHeaders headers() {
        return this.headers;
    }

    @Override
    public HttpQueryParams parseQuery() {
        return new StdHttpQueryParams(uri());
    }

    public static class Builder {
        private URI uri;
        private String method;
        private CaseInsensitiveMultiMap headers = new CaseInsensitiveMultiMap();

        public Builder uri(String uri) {
            this.uri = URI.create(uri);
            return this;
        }

        public Builder method(String method){
            this.method = method;
            return this;
        }

        public Builder addHeader(String name, String... value) {
            headers.put(name, value);
            return this;
        }

        public HttpRequest build(){
            return new BasicHttpRequest(this.uri, this.method, headers.map());
        }
    }

    private static class StdHttpQueryParams implements HttpQueryParams {

        private final CaseInsensitiveMultiMap params;

        private StdHttpQueryParams(URI uri) {
            String queryString = uri.getQuery();
            if (Strings.isNullOrEmpty(queryString)) {
                params = new CaseInsensitiveMultiMap(Collections.emptyMap());
            } else {
                params = new CaseInsensitiveMultiMap(
                        Arrays.stream(queryString.split("&"))
                            .map(this::splitQueryParameter)
                            .collect(groupingBy(AbstractMap.SimpleImmutableEntry::getKey, LinkedHashMap::new,
                                    mapping(Map.Entry::getValue, toList())))
                );
            }
        }

        @Override
        public List<String> allValues(String name) {
            return params.allValues(name);
        }

        @Override
        public Optional<String> firstValue(String name) {
            return params.firstValue(name);
        }

        @Override
        public Set<String> allParams() {
            return params.keySet();
        }

        private AbstractMap.SimpleImmutableEntry<String, String> splitQueryParameter(String it) {
            final int idx = it.indexOf("=");
            final String key = idx > 0 ? it.substring(0, idx) : it;
            final String value = idx > 0 && it.length() > idx + 1 ? it.substring(idx + 1) : null;
            return new AbstractMap.SimpleImmutableEntry<>(key, value);
        }
    }
}
