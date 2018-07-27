package org.geolatte.mapserver.rxhttp;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.geolatte.geom.C2D;
import org.geolatte.geom.Envelope;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static java.lang.String.format;

/**
 * Created by Karel Maesen, Geovise BVBA on 27/07/2018.
 */
public class MockFeatureServer {

    final private WireMockServer wireMockServer;

    public MockFeatureServer() {
        wireMockServer = new WireMockServer(wireMockConfig().port(8080)); //No-args constructor will start on port 8080, no HTTPS
    }

    public void start() {
        wireMockServer.start();
    }

    public void stop() {
        wireMockServer.stop();
    }

    public void buildStub(Envelope<C2D> env, String returnBody) {
        stubFor(get(anyUrl())
                .withHeader("Accept", equalTo("application/json"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(returnBody)
                )
        );
    }

    private String bboxAsString(Envelope<C2D> env) {
        return format("%.2f,%.2f,%.2f,%.2f",
                env.lowerLeft().getX(), env.lowerLeft().getY(),
                env.upperRight().getX(), env.upperRight().getY());
    }

    public void buildStub(Envelope<C2D> env) {
        buildStub(env, JSONBODY);
    }

    private final String JSONBODY =
            "{\"id\":\"10000301\",\"type\":\"Feature\",\"properties\":{\"nummer\":\"aab\"},\"geometry\":{\"type\":\"GeometryCollection\",\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"EPSG:31370\"}},\"bbox\":[97662,180039.31,97662,180039.31],\"geometries\":[{\"type\":\"Point\",\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"EPSG:31370\"}},\"bbox\":[97662,180039.31,97662,180039.31],\"coordinates\":[97662,180039.31]}]}}\n" +
                    "{\"id\":\"10000351\",\"type\":\"Feature\",\"properties\":{\"nummer\":\"baa\"},\"geometry\":{\"type\":\"GeometryCollection\",\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"EPSG:31370\"}},\"bbox\":[106175.99,175881.46,106175.99,175881.46],\"geometries\":[{\"type\":\"Point\",\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"EPSG:31370\"}},\"bbox\":[106175.99,175881.46,106175.99,175881.46],\"coordinates\":[106175.99,175881.46]}]}}\n" +
                    "{\"id\":\"10000401\",\"type\":\"Feature\",\"properties\":{\"nummer\":\"aba\"},\"geometry\":{\"type\":\"GeometryCollection\",\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"EPSG:31370\"}},\"bbox\":[106175.99,175881.46,106175.99,175881.46],\"geometries\":[{\"type\":\"Point\",\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"EPSG:31370\"}},\"bbox\":[106175.99,175881.46,106175.99,175881.46],\"coordinates\":[106175.99,175881.46]}]}}";

}
