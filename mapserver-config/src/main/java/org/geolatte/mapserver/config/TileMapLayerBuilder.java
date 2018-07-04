package org.geolatte.mapserver.config;

import com.typesafe.config.Config;
import org.geolatte.geom.crs.CrsId;
import org.geolatte.geom.crs.CrsRegistry;
import org.geolatte.mapserver.image.ImageFormat;
import org.geolatte.mapserver.tilemap.TileMapBuilder;
import org.geolatte.mapserver.tilemap.TileMapLayer;

import java.util.List;

/**
 * Created by Karel Maesen, Geovise BVBA on 19/07/2018.
 */
class TileMapLayerBuilder {

    final private String name;
    final private Config layerConfig;
    final private TileMapBuilder builder;

    TileMapLayerBuilder(String name, Config layerConfig){
        this.name = name;
        this.layerConfig = layerConfig;
        this.builder = new TileMapBuilder();
    }

    TileMapLayer build(){
        setName();
        setRoot();
        setCrs();
        setEnvelope();
        setTileFormat();

        setOrigin();


        tileSetConfigs()
                .forEach(cf -> addSet(builder, cf));

        return new TileMapLayer(name, builder.build());
    }

    private void setOrigin() {

        builder.origin(layerConfig.getDouble("origin.x"),
                layerConfig.getDouble("origin.y"));
    }


    private void setRoot() {
        builder.root(layerConfig.getString("root"));
    }

    private void setName() {
        builder.name(name);
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

    private List<? extends Config> tileSetConfigs() {
        return layerConfig.getConfigList("tilesets");
    }

    private void setEnvelope() {
        builder.envelope(layerConfig.getDoubleList("envelope"));
    }

    private void setCrs() {
        int code = CrsId.parse(layerConfig.getString("crs")).getCode();
        builder.crs(CrsRegistry.getProjectedCoordinateReferenceSystemForEPSG(code));
    }
}
