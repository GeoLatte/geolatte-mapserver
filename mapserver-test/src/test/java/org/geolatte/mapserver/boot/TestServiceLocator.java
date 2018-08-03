package org.geolatte.mapserver.boot;

import org.geolatte.mapserver.ServiceLocator;
import org.geolatte.mapserver.image.Imaging;
import org.geolatte.mapserver.protocols.ProtocolAdapter;
import org.geolatte.mapserver.LayerRegistry;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Karel Maesen, Geovise BVBA on 04/07/2018.
 */
public class TestServiceLocator {

    ServiceLocator registry = ServiceLocator.defaultInstance();

    @Test
    public void testIserviceRegistryImaging(){
        Imaging imaging = registry.imaging();
        assertNotNull(imaging);
    }

    @Test
    public void testIserviceRegistryProtocolAdapter(){
        ProtocolAdapter protocolAdapter = registry.protocolAdapter();
        assertNotNull(protocolAdapter);
    }

    @Test
    public void testLayerRegistry() {
        LayerRegistry lsr = registry.layerRegistry();
        assertNotNull(lsr);
    }

}
