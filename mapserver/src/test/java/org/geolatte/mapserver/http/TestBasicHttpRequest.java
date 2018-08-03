package org.geolatte.mapserver.http;

import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.AnyOf.anyOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Created by Karel Maesen, Geovise BVBA on 04/07/2018.
 */
public class TestBasicHttpRequest {

    private HttpRequest request;

    @Before
    public void setUp() throws URISyntaxException {
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Accept", Arrays.asList("text/plain", "application/json"));
        request = new BasicHttpRequest(new URI("http://localhost/wms?ows=getMap&layer=a&layer=b"), "GET", headers);
    }

    @Test
    public void testMethod() {
        assertEquals("GET", request.method());
    }

    @Test
    public void testHeaderAllValues() {
        assertThat(request.headers().allValues("accept"), hasItems("text/plain", "application/json"));
    }

    @Test
    public void testParseQueryParamLayers() {
        HttpQueryParams params = request.parseQuery();
        assertThat(params.allValues("layer"), hasItems("a", "b"));
    }

    @Test
    public void testParseQueryParamLayersIgnoreCase() {
        HttpQueryParams params = request.parseQuery();
        assertThat(params.allValues("LAYER"), hasItems("a", "b"));
    }

    @Test
    public void testParseQueryParamLayersFirst() {
        HttpQueryParams params = request.parseQuery();
        assertThat(params.firstValue("layer"), is(Optional.of("a")) );
    }

    @Test
    public void testParseQueryParamLayersFirstIgnoreCase() {
        HttpQueryParams params = request.parseQuery();
        assertThat(params.firstValue("lAYer"), is(Optional.of("a")) );
    }


    @Test
    public void testParseQueryParamsRequest() {
        HttpQueryParams params = request.parseQuery();
        assertThat(params.firstValue("ows"), is(Optional.of("getMap")));
    }
    @Test
    public void testParseQueryParamNames() {
        HttpQueryParams params = request.parseQuery();
        assertThat(params.allParams(), hasItems("layer", "ows"));
    }

    @Test
    public void testParseMissingParam(){
        HttpQueryParams params = request.parseQuery();
        assertThat(params.firstValue("version"), is(Optional.empty()));
    }

    @Test
    public void testParseMissingParams(){
        HttpQueryParams params = request.parseQuery();
        assertThat(params.allValues("version"), is(Collections.emptyList()));
    }

}
