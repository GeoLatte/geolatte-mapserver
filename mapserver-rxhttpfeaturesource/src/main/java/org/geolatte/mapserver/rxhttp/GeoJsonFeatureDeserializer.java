package org.geolatte.mapserver.rxhttp;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.geolatte.geom.Feature;
import org.geolatte.geom.json.GeolatteGeomModule;
import org.geolatte.maprenderer.map.PlanarFeature;
import org.geolatte.mapserver.features.FeatureDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

/**
 * Created by Karel Maesen, Geovise BVBA on 20/07/2018.
 */
class GeoJsonFeatureDeserializer implements FeatureDeserializer {

    final private static Logger logger = LoggerFactory.getLogger(GeoJsonFeatureDeserializer.class);
    final private static ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
        mapper.registerModule(new GeolatteGeomModule());
    }

    @Override
    public Iterable<PlanarFeature> deserialize(String jsonString) {
        try {
            Feature<?, ?> feature = mapper.readValue(jsonString, Feature.class);
            return Collections.singletonList(PlanarFeature.from(feature));
        } catch (Exception e) {
            logger.warn("Failure to parse String to GeoJson", e);
            return Collections.emptyList();
        }
    }
}
