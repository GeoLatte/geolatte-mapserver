package org.geolatte.mapserver.rxhttp;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigBeanFactory;
import com.typesafe.config.ConfigFactory;
import org.geolatte.geom.crs.CrsId;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created by Karel Maesen, Geovise BVBA on 2019-03-24.
 */
public class TestCrsConfig  extends AbstractFeatureSourceTest {
	
	@Test
	public void testConfiguredCrs(){
		Config config = ConfigFactory.load( "crs.conf").getConfig( "source");
		RxHttpFeatureSourceConfig rxConfig = ConfigBeanFactory.create( config, RxHttpFeatureSourceConfig.class);
		RxHttpFeatureSource featureSource = featureSourceFactory.mkFeatureSource(rxConfig);
		assertThat( featureSource.getSourceCrsId(), equalTo( CrsId.valueOf( 31370)));
	}


	@Test
	public void testDefaultCrs(){
		Config config = ConfigFactory.load( "default-configured.conf").getConfig( "source");
		RxHttpFeatureSourceConfig rxConfig = ConfigBeanFactory.create( config, RxHttpFeatureSourceConfig.class);
		RxHttpFeatureSource featureSource = featureSourceFactory.mkFeatureSource(rxConfig);
		assertThat( featureSource.getSourceCrsId(), equalTo( CrsId.valueOf( 4326)));
	}


}
