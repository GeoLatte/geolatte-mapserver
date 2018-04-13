package org.geolatte.mapserver.config;

import java.util.List;

/**
 * Created by Karel Maesen, Geovise BVBA on 13/04/2018.
 */
public class TileMapConfig {

    private ResourceType resourceType;
    private String name;
    private boolean forceArgb;
    private String path;
    private List<String> supportedCrs;
    private String tileSourceFactory;

    public TileMapConfig() {
    }


    /**
     * Returns the {@linkplain ResourceType}
     *
     * @return The resource type of the map.
     */
    public ResourceType getType() {
        return resourceType;
    }

    /**
     * Returns the path to the {@code TileMap} resource Json representation, or null if this is not configured.
     *
     * @param tileMap name of the {@code TileMap}
     * @return the path to the resource representation
     */
    public String getPath(String tileMap) {
        return path;
    }

    public List<String> getSupportedCrs() {
        return supportedCrs;
    }

    /**
     * Returns the {@code TileImageSourceFactory} to be used for this {@code TileMap}.
     *
     * @return the fully-qualified class name of the {@code TileImageSourceFactory}
     */
    public String getTileImageSourceFactoryClass() {
        return tileSourceFactory;
    }

    /**
     * Returns whether this {@code TileMap} is configured to force the conversion of tiles to ARGB.
     *
     * @return true if forceArgb is enabled in the configuration
     */
    public boolean isForceArgb() {
        return forceArgb;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    public void setForceArgb(boolean forceArgb) {
        this.forceArgb = forceArgb;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setSupportedCrs(List<String> supportedCrs) {
        this.supportedCrs = supportedCrs;
    }

    public void setTileSourceFactory(String tileSourceFactory) {
        this.tileSourceFactory = tileSourceFactory;
    }

    /**
     * The type of a resource. Either {@code URL} or {@code FILE}.
     */
    public enum ResourceType {
        URL,
        FILE
    }
}
