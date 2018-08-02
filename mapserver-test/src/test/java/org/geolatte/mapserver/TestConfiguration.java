package org.geolatte.mapserver;

import org.geolatte.mapserver.layers.RenderableTileMapLayer;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;


/**
 * Created by Karel Maesen, Geovise BVBA on 25/07/2018.
 */
public class TestConfiguration {

    @Test
    public void testSuccessfullLayerRegistryConfiguration(){

        LayerRegistry registry = ServiceLocator.defaultInstance().layerRegistry();
        Layer vkb = registry.getLayer("vkb").get();
        assertThat( vkb , instanceOf(RenderableTileMapLayer.class));
    }

}
