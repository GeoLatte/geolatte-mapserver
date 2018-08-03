package org.geolatte.mapserver.config;

import org.geolatte.mapserver.*;
import org.geolatte.mapserver.features.FeatureSourceFactory;
import org.geolatte.mapserver.tilemap.TileMetadata;
import org.geolatte.mapserver.layers.TileMapLayer;
import org.geolatte.mapserver.tilemap.TileSet;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static junit.framework.TestCase.assertTrue;
import static org.geolatte.mapserver.image.ImageFormat.JPEG;
import static org.geolatte.mapserver.image.ImageFormat.PNG;
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

    private final FeatureSourceFactoryRegistry fsfregistry = new FeatureSourceFactoryRegistry() {
        @Override
        public Optional<FeatureSourceFactory> featureSourceFactoryForType(String canonicalName) {
            return Optional.empty();
        }
    };

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
        registry = provider.layerSourceRegistry(fsfregistry);
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
    public void testConstantTileMapLayer(){
        Optional<Layer> layerOpt = registry.getLayer("myTilemap");
        assertTrue(layerOpt.isPresent());
        Layer layer = layerOpt.get();
        testTileMapStructure((TileMapLayer) layer);
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


}
