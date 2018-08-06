package org.geolatte.mapserver.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigBeanFactory;
import org.geolatte.geom.crs.CrsId;
import org.geolatte.geom.crs.CrsRegistry;
import org.geolatte.mapserver.FeatureSourceFactoryRegistry;
import org.geolatte.mapserver.Layer;
import org.geolatte.mapserver.ServiceLocator;
import org.geolatte.mapserver.features.FeatureSource;
import org.geolatte.mapserver.features.FeatureSourceConfig;
import org.geolatte.mapserver.render.RenderContext;
import org.geolatte.mapserver.layers.RenderableTileMapLayer;
import org.geolatte.mapserver.tilemap.TileMapBuilder;
import org.geolatte.mapserver.layers.TileMapLayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

/**
 * Created by Karel Maesen, Geovise BVBA on 19/07/2018.
 */
class TileMapLayerBuilder extends LayerBuilder {

    final private Config layerConfig;
    final private TileMapBuilder builder;
    private String painterRef;

    TileMapLayerBuilder(String name, FeatureSourceFactoryRegistry featureSourceFactoryRegistry, Config layerConfig,
                       ServiceLocator locator ){
        super(name, featureSourceFactoryRegistry, locator);
        this.layerConfig = layerConfig;
        this.builder = new TileMapBuilder();
    }

    @Override
    public Layer build(){
        setName();
        setRoot();
        setCrs();
        setEnvelope();
        setTileFormat();

        setOrigin();


        configureTileSets();

        optionallyConfigureSourceAndPainter();

        if (featureSource != null) {
            return new RenderableTileMapLayer(name, builder.build(), RenderContext.from(featureSource, painterRef));
        } else {
            return new TileMapLayer(name, builder.build());
        }
    }

    private void setOrigin() {

        builder.origin(layerConfig.getDouble("origin.x"),
                layerConfig.getDouble("origin.y"));
    }


    private void setRoot() {
        builder.root(layerConfig.getString("root"));
    }

    private void setName() {
        builder.name(this.name);
    }

    private void setTileFormat() {
        String mimeType = layerConfig.getString("tile.format");
        builder.tileHeight(
                layerConfig.getInt("tile.height")
        ).tileWidth(
                layerConfig.getInt("tile.width")
        ).tileExtension(
                layerConfig.getString("tile.extension")
        ).tileMimeType(mimeType);

    }

    private void addSet(TileMapBuilder builder, Config cf) {
        builder.addSet(
                cf.getString("url"),
                cf.getInt("order"),
                cf.getDouble("upp")
        );
    }

    private void configureTileSets() {
        List<? extends Config> tileSetConfigs = layerConfig.getConfigList("tilesets");
        tileSetConfigs.forEach(cf -> addSet(builder, cf));
    }

    private void setEnvelope() {
        builder.envelope(layerConfig.getDoubleList("envelope"));
    }

    private void setCrs() {
        int code = CrsId.parse(layerConfig.getString("crs")).getCode();
        builder.crs(CrsRegistry.getProjectedCoordinateReferenceSystemForEPSG(code));
    }

    private void optionallyConfigureSourceAndPainter() {
        if (layerConfig.hasPath("source")) {
            this.featureSource = mkFeatureSource(layerConfig.getConfig("source"));
            this.painterRef = layerConfig.getString("painter");
        }
    }


}
