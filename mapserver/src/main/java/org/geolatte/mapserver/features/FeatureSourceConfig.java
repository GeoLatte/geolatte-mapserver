package org.geolatte.mapserver.features;

/**
 * A marker interface for configuration of a @{code FeatureSource}.
 *
 * The intent is that implementations conform to the Java Bean configuration so that they
 * can be automatically instantiated, For instance, using typesafe Config we can do this:
 *
 * {@code (ConfigBeanFactory.create(config.getConfig("subtree-that-matches-bean"), MyBeanFeatureSourceConfig.class)}
 *
 * Created by Karel Maesen, Geovise BVBA on 20/07/2018.
 */
public interface FeatureSourceConfig {

}
