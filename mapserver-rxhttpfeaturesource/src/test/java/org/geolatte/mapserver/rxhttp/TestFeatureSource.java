package org.geolatte.mapserver.rxhttp;

import be.wegenenverkeer.rxhttp.HttpClientError;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.geolatte.geom.C2D;
import org.geolatte.geom.Envelope;
import org.geolatte.geom.Feature;
import org.geolatte.geom.crs.CoordinateReferenceSystems;
import org.geolatte.maprenderer.map.PlanarFeature;
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

    private MockFeatureServer mockServer = new MockFeatureServer();

    @Before
    public void setUp(){
        mockServer.start();

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
        mockServer.stop();
    }

    @Test
    public void testFeatureSource(){
        Envelope<C2D> bbox = new Envelope<>(10, 10, 20, 20, CoordinateReferenceSystems.WEB_MERCATOR);
        mockServer.buildStub(bbox);

        Observable<PlanarFeature> result = featureSource.query(bbox);

        TestSubscriber<Feature> sub = new TestSubscriber<>();
        result.subscribe(sub);
        sub.awaitTerminalEvent(DEFAULT_TIME_OUT, TimeUnit.MILLISECONDS);
        sub.assertNoErrors();
        assertNotNull(result);
        assertEquals(3, sub.getValueCount());


    }

    @Test
    public void testEmptyResult(){
        Envelope<C2D> bbox = new Envelope<>(10, 10, 20, 20, CoordinateReferenceSystems.WEB_MERCATOR);
        mockServer.buildStub(bbox, "\n");

        Observable<PlanarFeature> result = featureSource.query(bbox);

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

        Observable<PlanarFeature> result = featureSource.query(new Envelope<C2D>(10, 10, 20, 20, CoordinateReferenceSystems.WEB_MERCATOR));

        TestSubscriber<Feature> sub = new TestSubscriber<>();
        result.subscribe(sub);
        sub.awaitTerminalEvent(DEFAULT_TIME_OUT, TimeUnit.MILLISECONDS);
        sub.assertError( HttpClientError.class );


    }




}


