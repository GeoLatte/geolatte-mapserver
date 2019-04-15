package org.geolatte.mapserver.rxhttp;

import java.util.concurrent.ExecutorService;

import org.geolatte.mapserver.Instrumentation;
import org.geolatte.mapserver.LayerRegistry;
import org.geolatte.mapserver.PainterFactory;
import org.geolatte.mapserver.ServiceLocator;
import org.geolatte.mapserver.ServiceMetadata;
import org.geolatte.mapserver.image.Imaging;
import org.geolatte.mapserver.protocols.ProtocolAdapter;
import org.geolatte.mapserver.transform.TransformFactory;

import org.junit.Before;

/**
 * Created by Karel Maesen, Geovise BVBA on 2019-03-24.
 */
abstract public class AbstractFeatureSourceTest {

	RxHttpFeatureSourceFactory featureSourceFactory;

	@Before
	public void before(){
		featureSourceFactory= new RxHttpFeatureSourceFactory();
		featureSourceFactory.setServiceLocator( new ServiceLocatorDouble() );
	}


}

class ServiceLocatorDouble implements ServiceLocator {

	@Override
	public Imaging imaging() {
		return null;
	}

	@Override
	public ProtocolAdapter protocolAdapter() {
		return null;
	}

	@Override
	public LayerRegistry layerRegistry() {
		return null;
	}

	@Override
	public ServiceMetadata serviceMetadata() {
		return null;
	}

	@Override
	public ExecutorService executorService() {
		return null;
	}

	@Override
	public PainterFactory painterFactory() {
		return null;
	}

	@Override
	public TransformFactory coordinateTransforms() {
		return null;
	}

	@Override
	public Instrumentation instrumentation() {
		return null;
	}
}