package org.geolatte.mapserver.config;

import org.geolatte.mapserver.*;
import org.geolatte.mapserver.boot.StdFeatureSourceFactory;
import org.geolatte.mapserver.image.Imaging;
import org.geolatte.mapserver.layers.DynamicLayer;
import org.geolatte.mapserver.layers.RenderableTileMapLayer;
import org.geolatte.mapserver.protocols.ProtocolAdapter;
import org.geolatte.mapserver.render.Renderer;
import org.geolatte.mapserver.tilemap.TileMetadata;
import org.geolatte.mapserver.layers.TileMapLayer;
import org.geolatte.mapserver.tilemap.TileSet;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

import static java.util.Arrays.asList;
import static junit.framework.TestCase.assertTrue;
import static org.geolatte.mapserver.image.ImageFormat.JPEG;
import static org.geolatte.mapserver.image.ImageFormat.PNG;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Karel Maesen, Geovise BVBA on 06/07/2018.
 */
public class TestServiceConfiguration {

    final private static Logger logger = LoggerFactory.getLogger(TestServiceConfiguration.class);

    private ServiceMetadata serviceMetadata;

    private LayerRegistry registry;

    private ServiceMetadata.ServiceIdentification expectedSI;
    private List<ServiceMetadata.Operation> expectedOperations;

    private ServiceLocator dummyLocator = mkServiceLocator();

    private final FeatureSourceFactoryRegistry fsfregistry = new StdFeatureSourceFactory(Arrays.asList(new FeatureSourceFactoryDouble()));

    @Before
    public void setup() {
        expectedSI = new ServiceMetadata.ServiceIdentification(
                "urn:ogc:service:wms_1_3_0",
                "1.3.0",
                "Test WMS",
                "This is an abstract",
                asList("keyword1", "keyword2")
        );

        expectedOperations = asList(
                new ServiceMetadata.GetMapOperation("http://example.com", asList(JPEG, PNG)),
                new ServiceMetadata.GetCapabilitiesOperation("http://example.com/wms")
        );

        ConfigServicProvider provider = new ConfigServicProvider("test-mapserver");
        serviceMetadata = provider.serviceMetadata();
        registry = provider.layerRegistry(fsfregistry, dummyLocator);

    }

    @Test
    public void testServiceMetadataIsBuilt() {
        assertNotNull(serviceMetadata);
    }

    @Test
    public void testOnelineResource() {
        assertEquals("http://example.com/wms", serviceMetadata.getOnlineResource());
    }

    @Test
    public void testServiceIdentification() {
        assertNotNull(serviceMetadata.getServiceIdentification());
        assertEquals(expectedSI, serviceMetadata.getServiceIdentification());
    }

    @Test
    public void testProvider(){
        assertEquals("Geolatte", serviceMetadata.getServiceProvider().getProviderName());
    }

    @Test
    public void testOperations(){
        assertEquals(expectedOperations, serviceMetadata.getOperations());
    }

    @Test
    public void testLayerRegistryIsbuilt() {
        assertNotNull(registry);
    }

    @Test
    public void testTileMapLayer(){
        Optional<Layer> layerOpt = registry.getLayer("myTilemap");
        assertTrue(layerOpt.isPresent());
        Layer layer = layerOpt.get();
        testTileMapStructure((TileMapLayer) layer);
    }

    @Test
    public void testRenderableTileMapLayer(){
        Optional<Layer> layerOpt = registry.getLayer("vkbtm");
        assertTrue(layerOpt.isPresent());
        Layer layer = layerOpt.get();
        assertThat( layer, instanceOf(RenderableTileMapLayer.class));
    }

    @Test
    public void testDynamicLayer(){
        Optional<Layer> layerOpt = registry.getLayer("vkb");
        assertTrue(layerOpt.isPresent());
        Layer layer = layerOpt.get();
        assertThat( layer, instanceOf(DynamicLayer.class));
        Renderer renderer = ((DynamicLayer)layer).getRenderer();
        assertThat(renderer, notNullValue());

    }



    private void testTileMapStructure(TileMapLayer layer){
        assertEquals("myTilemap", layer.getName());
        assertEquals("src/test/resources/tiles/osm", layer.getTileMap().getServiceUrl());
        assertEquals(new TileMetadata( new Dimension(512, 256), "image/jpeg", ".jpeg"), layer.getTileMap().getTileMetadata());
        testTileSets(layer.getTileMap().getTileSets());
    }

    private void testTileSets(List<TileSet> tileSets) {
        assertEquals(4, tileSets.size());
        double uup = 20000;
        for (int i = 0; i < tileSets.size(); i++){
            uup = uup / 2;
            TileSet set = tileSets.get(i);
            assertEquals(i, set.getOrder());
            assertEquals(uup , set.unitsPerPixel(), 0.00001);

        }
    }

    private ServiceLocator mkServiceLocator() {
        return new ServiceLocator() {

            @Override
            public Imaging imaging() {
                return null;
            }

            @Override
            public ProtocolAdapter protocolAdapter() {
                return null;
            }

            @Override
            public LayerRegistry layerRegistry() {
                return null;
            }

            @Override
            public ServiceMetadata serviceMetadata() {
                return null;
            }

            @Override
            public ExecutorService executorService() {
                return null;
            }

            @Override
            public PainterFactory painterFactory() {
                return null;
            }
        };
    }



}
