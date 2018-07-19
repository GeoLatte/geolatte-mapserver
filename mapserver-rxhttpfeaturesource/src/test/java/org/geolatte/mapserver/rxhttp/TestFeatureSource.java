package org.geolatte.mapserver.rxhttp;

import be.wegenenverkeer.rxhttp.HttpClientError;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.geolatte.geom.C2D;
import org.geolatte.geom.Envelope;
import org.geolatte.geom.Feature;
import org.geolatte.geom.crs.CoordinateReferenceSystems;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import rx.Observable;
import rx.observers.TestSubscriber;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


/**
 * Created by Karel Maesen, Geovise BVBA on 20/07/2018.
 */
public class TestFeatureSource {

    final private int DEFAULT_TIME_OUT = 200;
    private RxHttpFeatureSource featureSource;

    private WireMockServer wireMockServer;

//TODO -- why doesn't this work???
//    public WireMockRule wireMockRule = new WireMockRule(); // No-args constructor defaults to port 8080

    @Before
    public void setUp(){
        wireMockServer = new WireMockServer(wireMockConfig().port(8080)); //No-args constructor will start on port 8080, no HTTPS
        wireMockServer.start();

        RxHttpFeatureSourceConfig config = new RxHttpFeatureSourceConfig(
        );
        config.setHost("http://localhost:8080");
        config.setTemplate("/query?bbox=<bbox>");
        featureSource = new RxHttpFeatureSource(config);
    }

    @After
    public void close(){
        if (featureSource != null) {
            featureSource.close();
        }
        wireMockServer.stop();
    }

    @Test
    public void testFeatureSource(){
        buildStub(JSONBODY);


        Observable<Feature<C2D, ?>> result = featureSource.query(new Envelope<C2D>(10, 10, 20, 20, CoordinateReferenceSystems.WEB_MERCATOR));

        TestSubscriber<Feature> sub = new TestSubscriber<>();
        result.subscribe(sub);
        sub.awaitTerminalEvent(DEFAULT_TIME_OUT, TimeUnit.MILLISECONDS);
        sub.assertNoErrors();
        assertNotNull(result);
        assertEquals(3, sub.getValueCount());


    }

    @Test
    public void testEmptyResult(){
        buildStub("\n");


        Observable<Feature<C2D, ?>> result = featureSource.query(new Envelope<C2D>(10, 10, 20, 20, CoordinateReferenceSystems.WEB_MERCATOR));

        TestSubscriber<Feature> sub = new TestSubscriber<>();
        result.subscribe(sub);
        sub.awaitTerminalEvent(DEFAULT_TIME_OUT, TimeUnit.MILLISECONDS);
        sub.assertNoErrors();
        assertNotNull(result);
        assertEquals(0, sub.getValueCount());

    }

    @Test
    public void testNotFound(){
        stubFor(get(urlEqualTo("/query?bbox=10.000000,10.000000,20.000000,20.000000"))
                .withHeader("Accept", equalTo("application/json"))
                .willReturn(aResponse()
                        .withStatus(404)
                )
        );

        Observable<Feature<C2D, ?>> result = featureSource.query(new Envelope<C2D>(10, 10, 20, 20, CoordinateReferenceSystems.WEB_MERCATOR));

        TestSubscriber<Feature> sub = new TestSubscriber<>();
        result.subscribe(sub);
        sub.awaitTerminalEvent(DEFAULT_TIME_OUT, TimeUnit.MILLISECONDS);
        sub.assertError( HttpClientError.class );


    }

    private void buildStub(String s) {
        stubFor(get(urlEqualTo("/query?bbox=10.000000,10.000000,20.000000,20.000000"))
                .withHeader("Accept", equalTo("application/json"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(s)
                )
        );
    }


    private final String JSONBODY =
            "{\"id\":\"10000301\",\"type\":\"Feature\",\"properties\":{\"nummer\":\"aab\"},\"geometry\":{\"type\":\"GeometryCollection\",\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"EPSG:31370\"}},\"bbox\":[97662,180039.31,97662,180039.31],\"geometries\":[{\"type\":\"Point\",\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"EPSG:31370\"}},\"bbox\":[97662,180039.31,97662,180039.31],\"coordinates\":[97662,180039.31]}]}}\n" +
            "{\"id\":\"10000351\",\"type\":\"Feature\",\"properties\":{\"nummer\":\"baa\"},\"geometry\":{\"type\":\"GeometryCollection\",\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"EPSG:31370\"}},\"bbox\":[106175.99,175881.46,106175.99,175881.46],\"geometries\":[{\"type\":\"Point\",\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"EPSG:31370\"}},\"bbox\":[106175.99,175881.46,106175.99,175881.46],\"coordinates\":[106175.99,175881.46]}]}}\n" +
            "{\"id\":\"10000401\",\"type\":\"Feature\",\"properties\":{\"nummer\":\"aba\"},\"geometry\":{\"type\":\"GeometryCollection\",\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"EPSG:31370\"}},\"bbox\":[106175.99,175881.46,106175.99,175881.46],\"geometries\":[{\"type\":\"Point\",\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"EPSG:31370\"}},\"bbox\":[106175.99,175881.46,106175.99,175881.46],\"coordinates\":[106175.99,175881.46]}]}}";

}


