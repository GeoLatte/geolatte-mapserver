package org.geolatte.mapserver.rxhttp;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.geolatte.geom.Feature;
import org.geolatte.geom.json.GeolatteGeomModule;
import org.geolatte.maprenderer.map.PlanarFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Karel Maesen, Geovise BVBA on 20/07/2018.
 */
class GeoJsonDeserializer {

    final private static Logger logger = LoggerFactory.getLogger(GeoJsonDeserializer.class);
    final private static ObjectMapper mapper;

    //mutable state
    private String[] jsons;
    private String partialJson = "";
    private List<PlanarFeature> features;

    static {
        mapper = new ObjectMapper();
        mapper.registerModule(new GeolatteGeomModule());
    }

    Observable<PlanarFeature> deserialize(String chunk) {
        features = new ArrayList<>();
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
        String chunkWithPartial = partialJson + chunk;

        String[] parts = chunkWithPartial.split("\n");

        if (chunk.endsWith("\n")) {
            // every part in the chunk is a complete json
            partialJson = "";
            jsons = parts;
        } else {
            // the last part is not a complete json, we keep it for the next chunk
            partialJson = parts[parts.length - 1];
            // all other parts are complete jsons
            jsons = Arrays.copyOfRange(parts, 0, parts.length - 1);
        }
    }
}
