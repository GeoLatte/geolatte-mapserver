package org.geolatte.mapserver;

/**
 * Created by Karel Maesen, Geovise BVBA on 19/07/2018.
 */
public class Capabilities {

    private final ServiceMetadata serviceMetadata;

    private final LayerRegistry layerRegistry;

    public Capabilities(ServiceMetadata serviceMetadata, LayerRegistry layerRegistry) {
        this.serviceMetadata = serviceMetadata;
        this.layerRegistry = layerRegistry;
    }

    public ServiceMetadata getServiceMetadata() {
        return serviceMetadata;
    }

    public LayerRegistry getLayerRegistry() {
        return layerRegistry;
    }
}
