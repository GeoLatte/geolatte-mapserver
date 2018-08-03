package org.geolatte.mapserver.tilemap;

import org.geolatte.geom.C2D;
import org.geolatte.geom.Envelope;
import org.geolatte.geom.crs.CoordinateReferenceSystem;
import org.geolatte.geom.crs.CrsRegistry;
import org.geolatte.mapserver.LayerRegistry;
import org.geolatte.mapserver.ServiceLocator;
import org.geolatte.mapserver.boot.BootServiceLocator;
import org.geolatte.mapserver.image.Image;
import org.geolatte.mapserver.image.ImageFormat;
import org.geolatte.mapserver.layers.RenderableTileMapLayer;
import org.geolatte.mapserver.ows.GetMapRequest;
import org.geolatte.mapserver.protocols.OsmGetMapRequest;
import org.geolatte.mapserver.rxhttp.MockFeatureServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import static org.geolatte.mapserver.img.ImageOpsTestSupport.*;
import static org.junit.Assert.assertThat;

/**
 * Created by Karel Maesen, Geovise BVBA on 27/07/2018.
 */
public class TestRenderableTileMapLayer {

    private MockFeatureServer mockServer = new MockFeatureServer();
    private RenderableTileMapLayer layer;

    final private static CoordinateReferenceSystem CRS = CrsRegistry.getProjectedCoordinateReferenceSystemForEPSG(31370);

    @Before
    public void setUp(){
        mockServer.start();
        ServiceLocator registry = BootServiceLocator.instance();
        LayerRegistry layers = registry.layerRegistry();
        layer = (RenderableTileMapLayer)layers.getLayer("vkb").get();
    }

    @After
    public void tearDown(){
        mockServer.stop();
    }

    @Test
    public void testFeaturesAreCorrectlyRendered() throws IOException {
        Envelope<C2D> bbox = new Envelope<>(10, 10, 90, 90, CRS);
        mockServer.buildStub(new Envelope<>(7514065.0,-7514065.0,12523443.0,12523443.0, CRS));

        CompletableFuture<Image> img = layer.createMapImage(getMapRequest(bbox));
        Image tmp =  imageAfterIO(img.join(), ImageFormat.PNG);
        assertImageEquals(tmp, readTileImage("dynamic-render.png", true));

    }


    private GetMapRequest getMapRequest(Envelope<C2D> bbox){
        return new OsmGetMapRequest(
                bbox,
                "vkb",
                CRS.getCrsId(),
                new Dimension(512,512),
                true,
                Color.BLACK,
                null,
                "image/png"
        );
    }

}
