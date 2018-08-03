package org.geolatte.mapserver.integration;

import org.geolatte.mapserver.http.HttpService;
import org.geolatte.mapserver.ows.OwsHttpService;
import org.geolatte.mapserver.http.BasicHttpRequest;
import org.geolatte.mapserver.http.HttpRequest;
import org.geolatte.mapserver.http.HttpResponse;
import org.geolatte.mapserver.image.Image;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.geolatte.mapserver.img.ImageOpsTestSupport.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Karel Maesen, Geovise BVBA on 19/07/2018.
 */
public class HttpServiceTest {

    private HttpService service = new OwsHttpService();
    private Map<String, List<String>> baseRequestParameters;

    @Test
    public void testGetMapFromTileMap() throws IOException, InterruptedException, ExecutionException, TimeoutException {

        HttpRequest request = buildGetMapRequest();
        HttpResponse response = service.process(request).get(10, TimeUnit.SECONDS);

        assertNotNull(response);
        assertEquals("image/png", response.headers().firstValue("Content-type").get());
        Image expected =readTileImage("getmaposm.png", true);
        Image received = fromBytes(response.body());

//        writeImageToFile(received, ImageFormat.PNG); //to derive a first test image
        assertImageEquals(expected, received);

    }

    @Test
    public void testGetCapabilities() throws IOException, InterruptedException, ExecutionException, TimeoutException {
        HttpRequest request = buildGetCapabilitiesRequest();
        HttpResponse response = service.process(request).get(10, TimeUnit.SECONDS);

        assertNotNull(response);
        writeTextFileToTmp(response.body(), "xml");
    }


    // Helper methods
    private HttpRequest buildGetCapabilitiesRequest() {
        return new BasicHttpRequest.Builder()
                .method("GET")
                .uri("http://localhost/wms?REQUEST=GeTCapabilities&VERSION=1.3.0&SERVICE=WMS")
                .addHeader("Accept", "application/xml")
                .build();
    }

    private HttpRequest buildGetMapRequest() {
        return new BasicHttpRequest.Builder()
                    .method("GET")
                    .uri("http://localhost/wms?REQUEST=GETMAP&LAYERS=osm&crs=3857&BBOX=-20030000,-20030000,20030000,20030000&width=512&height=512&FORMAT=image/png")
                    .addHeader("Accept", "image/png")
                    .build();
    }

}
