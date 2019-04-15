package org.geolatte.mapserver.rxhttp;

import be.wegenenverkeer.rxhttp.HttpClientError;
import org.geolatte.geom.C2D;
import org.geolatte.geom.Envelope;
import org.geolatte.geom.Feature;
import org.geolatte.geom.Position;
import org.geolatte.geom.crs.CoordinateReferenceSystem;
import org.geolatte.geom.crs.CoordinateReferenceSystems;
import org.geolatte.maprenderer.map.PlanarFeature;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.geolatte.mapserver.coordinatetransforms.GeolatteTransformFactory;
import org.geolatte.mapserver.transform.TransformFactory;
import org.geolatte.mapserver.transform.Transform;
import rx.Observable;
import rx.observers.TestSubscriber;

import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.geolatte.geom.builder.DSL.c;
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

        RxHttpFeatureSourceConfig config = new RxHttpFeatureSourceConfig();
        config.setHost("http://localhost:8080");
        config.setTemplate("/query?bbox=<bbox>");
        config.setCrs("EPSG:31370");
        featureSource = new RxHttpFeatureSource(config, new GeoJsonFeatureDeserializerFactory(), new GeolatteTransformFactory() );
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

        Envelope<C2D> bbox = new Envelope<>(556600, 6446270,667920, 6621290, CoordinateReferenceSystems.WEB_MERCATOR);
        mockServer.buildStub(bbox);

        Observable<PlanarFeature> result = featureSource.query(bbox);

        TestSubscriber<Feature> sub = new TestSubscriber<>();
        result.subscribe(sub);
        sub.awaitTerminalEvent(DEFAULT_TIME_OUT, TimeUnit.MILLISECONDS);
        sub.assertNoErrors();
        assertNotNull(result);
        assertEquals(3, sub.getValueCount());

        assertEquals(
                CoordinateReferenceSystems.WEB_MERCATOR,
                sub.getOnNextEvents().get( 0 ).getGeometry().getCoordinateReferenceSystem()
        );


    }

    @Test
    public void testEmptyResult(){
        Envelope<C2D> bbox = new Envelope<>(556600, 6446270,667920, 6621290, CoordinateReferenceSystems.WEB_MERCATOR);
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

