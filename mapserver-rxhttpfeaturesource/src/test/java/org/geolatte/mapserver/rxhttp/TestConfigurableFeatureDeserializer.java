package org.geolatte.mapserver.rxhttp;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigBeanFactory;
import com.typesafe.config.ConfigFactory;
import org.geolatte.mapserver.features.FeatureSource;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

/**
 * Created by Karel Maesen, Geovise BVBA on 07/08/2018.
 */
public class TestConfigurableFeatureDeserializer extends AbstractFeatureSourceTest {


    @Test
    public void testConfiguredFDFactoryIsInstantiated(){
        Config config = ConfigFactory.load("deserializerfactory-configured.conf").getConfig("source");
        RxHttpFeatureSourceConfig rxConfig = ConfigBeanFactory.create(config, RxHttpFeatureSourceConfig.class);
        RxHttpFeatureSource featureSource = featureSourceFactory.mkFeatureSource(rxConfig);
        assertThat(featureSource.getFeatureDeserializerFactory(), instanceOf(FeatureDeserializerFactoryDouble.class));
    }


    @Test
    public void testDefaultFDFactoryIsInstantiated(){
        Config config = ConfigFactory.load("default-configured.conf").getConfig("source");
        RxHttpFeatureSourceConfig rxConfig = ConfigBeanFactory.create(config, RxHttpFeatureSourceConfig.class);
        RxHttpFeatureSource featureSource = featureSourceFactory.mkFeatureSource(rxConfig);
        assertThat(featureSource.getFeatureDeserializerFactory(), instanceOf(GeoJsonFeatureDeserializerFactory.class));
    }


}
