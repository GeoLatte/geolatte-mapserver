package org.geolatte.mapserver.rxhttp;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.geolatte.geom.Feature;
import org.geolatte.geom.json.GeolatteGeomModule;
import org.geolatte.maprenderer.map.PlanarFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Karel Maesen, Geovise BVBA on 20/07/2018.
 */
class GeoJsonDeserializer {

    final private static Logger logger = LoggerFactory.getLogger(GeoJsonDeserializer.class);
    final static private ObjectMapper mapper;

    //mutable state
    private String[] jsons;
    private List<PlanarFeature> features = new ArrayList<>();

    static {
        mapper = new ObjectMapper();
        mapper.registerModule(new GeolatteGeomModule());
    }

    Observable<PlanarFeature> deserialize(String chunk) {
        splitChunk(chunk);
        deserializeChunks();
        return Observable.from(features);
    }

    private void deserializeChunks() {
        for (String json : jsons) {
            deserializeChunk(json);
        }
    }

    @SuppressWarnings("unchecked")
    private void deserializeChunk(String json) {
        try {
            Feature<?, ?> feature = mapper.readValue(json, Feature.class);
            features.add(PlanarFeature.from(feature));
        } catch (Exception e) {
            logger.warn("Failure to parse String to GeoJson", e);
        }

    }

    private void splitChunk(String chunk) {
        jsons = chunk.split("\n");
    }
}
