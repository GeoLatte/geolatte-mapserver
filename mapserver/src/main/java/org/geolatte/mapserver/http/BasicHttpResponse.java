package org.geolatte.mapserver.http;

import org.geolatte.mapserver.util.CaseInsensitiveMultiMap;

/**
 * Created by Karel Maesen, Geovise BVBA on 05/07/2018.
 */
public class BasicHttpResponse implements HttpResponse {

    final public static int OK = 200;
    final public static int SERVER_ERROR = 500;
    final public static int NOT_FOUND = 404;

    private final int status;
    private final HttpHeaders headers;
    private final byte[] body;

    BasicHttpResponse(int status, HttpHeaders headers, byte[] body) {
        this.status = status;
        this.headers = headers;
        this.body = body;
    }

    public static Builder builder(){
        return new Builder();
    }

    @Override
    public HttpHeaders headers() {
        return headers;
    }

    @Override
    public int statusCode(){
        return this.status;
    }

    @Override
    public byte[] body() {
        return body;
    }

    public static class Builder {

        private final CaseInsensitiveMultiMap ciMap = new CaseInsensitiveMultiMap();
        private int status = 200;
        private byte[] bytes = new byte[0];

        public Builder body(byte[] b){
            this.bytes = b;
            return this;
        }

        public Builder setHeader(String header, String value) {
            ciMap.put(header, value);
            return this;
        }

        public Builder ok(){
            status = OK;
            return this;
        }

        public Builder NotFound(){
            status = NOT_FOUND;
            return this;
        }

        public Builder ServerError(){
            status = SERVER_ERROR;
            return this;
        }

        public HttpResponse build() {
            return new BasicHttpResponse(status, new BasicHttpHeaders(ciMap), bytes);
        }

    }
}
