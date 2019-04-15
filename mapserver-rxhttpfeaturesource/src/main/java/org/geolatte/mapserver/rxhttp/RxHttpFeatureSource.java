package org.geolatte.mapserver.rxhttp;

import java.nio.charset.Charset;
import java.util.Locale;

import be.wegenenverkeer.rxhttp.ClientRequest;
import be.wegenenverkeer.rxhttp.ClientRequestBuilder;
import be.wegenenverkeer.rxhttp.RxHttpClient;
import org.geolatte.geom.C2D;
import org.geolatte.geom.Envelope;
import org.geolatte.geom.Feature;
import org.geolatte.geom.Position;
import org.geolatte.geom.crs.CrsId;
import org.geolatte.maprenderer.map.PlanarFeature;
import org.geolatte.mapserver.features.FeatureDeserializer;
import org.geolatte.mapserver.features.FeatureSource;
import org.geolatte.mapserver.transform.Transform;
import org.geolatte.mapserver.transform.TransformFactory;
import org.stringtemplate.v4.ST;
import rx.Observable;

/**
 * Created by Karel Maesen, Geovise BVBA on 19/07/2018.
 */
public class RxHttpFeatureSource implements FeatureSource {


	final private static Charset UTF8 = Charset.forName( "UTF-8" );
	final private String template;
	final private boolean gzip;
	final private RxHttpClient client;
	final private FeatureDeserializerFactory featureDeserializerFactory;
	final private CrsId sourceCrsId;
	final private TransformFactory transformFactory;

	public RxHttpFeatureSource(
			RxHttpFeatureSourceConfig config,
			FeatureDeserializerFactory deserFactory,
			TransformFactory transformFactory) {
		this.template = config.getTemplate();
		String host = config.getHost();
		this.gzip = config.getGzip() == null ? true : config.getGzip();
		this.featureDeserializerFactory = deserFactory;
		this.sourceCrsId = CrsId.parse( config.getCrs() );
		this.transformFactory = transformFactory;
		this.client = new RxHttpClient.Builder()
				.setAccept( "application/json" )
				.setBaseUrl( host )
				.build();
	}

	@Override
	public Observable<PlanarFeature> query(Envelope<C2D> bbox, String query) {
		CrsId targetCrsId = bbox.getCoordinateReferenceSystem().getCrsId();
		Transform<Position, C2D> transform = buildTransform( targetCrsId );
		String queryUrl = render( transform, bbox, query );
		ClientRequestBuilder builder = client.requestBuilder().setUrlRelativetoBase( queryUrl );
		if ( this.gzip ) {
			builder = builder.addHeader( "Accept-Encoding", "gzip" );
		}

		ClientRequest request = builder.build();

		ChunkSplitter chunkSplitter = new ChunkSplitter();
		final FeatureDeserializer deserializer = featureDeserializerFactory.featureDeserializer();
		Observable<Feature> featureObservable = client
				.executeObservably( request, bytes -> new String( bytes, UTF8 ) )
				.flatMapIterable( chunkSplitter::split )
				.flatMapIterable( deserializer::deserialize );

		if ( transform != null ) {
			Observable<Feature> transformed = featureObservable.map( f -> (Feature) (transform.forwardFeature( f )) );
			return transformed.map( PlanarFeature::from );
		} else {
			return featureObservable.map( PlanarFeature::from );
		}


	}

	@SuppressWarnings("unchecked")
	private Transform<Position, C2D> buildTransform(CrsId targetCrsId) {
		Transform<Position, C2D> transform = null;
		if ( !targetCrsId.equals( sourceCrsId ) ) {
			transform = (Transform<Position, C2D>) this.transformFactory.getTransform( sourceCrsId, targetCrsId );

		}
		return transform;
	}


	// for testing
	protected FeatureDeserializerFactory getFeatureDeserializerFactory() {
		return this.featureDeserializerFactory;
	}

	//for testing
	protected CrsId getSourceCrsId() {
		return this.sourceCrsId;
	}

	private String render(Transform<Position, C2D> transform, Envelope<C2D> bbox, String query) {
		Envelope env = transform == null ? bbox : transform.reverse( bbox );
		ST st = fillInTemplateParams( env, query );
		return st.render();
	}

	private ST fillInTemplateParams(Envelope<C2D> bbox, String query) {
		ST st = new ST( template );
		st.add( "bbox", asString( bbox ) );
		if ( query != null ) {
			st.add( query, query );
		}
		return st;
	}

	private <P extends Position> String asString(Envelope<P> bbox) {
		double[] cos = bbox.toArray();
		return String.format( Locale.ROOT, "%f,%f,%f,%f",  cos[0], cos[1], cos[2], cos[3] );
	}

	@Override
	public void close() {
		if ( client != null ) {
			client.close();
		}
	}
}
