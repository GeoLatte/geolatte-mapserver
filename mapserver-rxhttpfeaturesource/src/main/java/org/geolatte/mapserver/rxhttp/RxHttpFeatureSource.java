package org.geolatte.mapserver.rxhttp;

import java.awt.*;
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
import org.geolatte.mapserver.render.BboxFactors;
import org.geolatte.mapserver.transform.Transform;
import org.geolatte.mapserver.transform.TransformFactory;
import org.stringtemplate.v4.ST;
import rx.Observable;

import static org.geolatte.mapserver.render.BboxFactors.upp;
import static org.geolatte.mapserver.util.EnvelopUtils.bufferRounded;

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
  final private boolean convertFeaturesToRequestedCrs;
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
		this.convertFeaturesToRequestedCrs = config.convertFeaturesToRequestedCrs;

    this.transformFactory = transformFactory;
		this.client = new RxHttpClient.Builder()
				.setAccept( "application/json" )
				.setBaseUrl( host )
				.build();
	}

  @Override
  public Envelope<C2D> queryBoundingBox(Envelope<C2D> tileBoundingBox, BboxFactors dynamicFactors, Dimension size, double graphicsRes) {
    Transform<C2D, C2D> transform = buildTransform( tileBoundingBox );
    Envelope<C2D> env = transform == null ? tileBoundingBox : transform.reverse( tileBoundingBox );

    C2D ll = env.lowerLeft();
    C2D ur = env.upperRight();
    double sx = size.width / (ur.getX() - ll.getX());
    double sy = size.height / (ur.getY() - ll.getY());
    double res = Math.max(1 / sx, 1 / sy);

    return bufferRounded(
      env,
      dynamicFactors.getFactor(upp(res)));
  }

	@Override
	public Observable<PlanarFeature> query(Envelope<C2D> bbox, String query) {
		Transform<C2D, C2D> transform = buildTransform( bbox );
		String queryUrl = render( bbox, query );
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

		if ( convertFeaturesToRequestedCrs && (transform != null) ) {
			Observable<Feature> transformed = featureObservable.map( f -> (Feature) (transform.forwardFeature( f )) );
			return transformed.map( PlanarFeature::from );
		} else {
			return featureObservable.map( PlanarFeature::from );
		}


	}

	@SuppressWarnings("unchecked")
	private Transform<C2D, C2D> buildTransform(Envelope<C2D> bbox) {
    CrsId targetCrsId = bbox.getCoordinateReferenceSystem().getCrsId();
		Transform<C2D, C2D> transform = null;
		if ( !targetCrsId.equals( sourceCrsId ) ) {
			transform = (Transform<C2D, C2D>) this.transformFactory.getTransform( sourceCrsId, targetCrsId );

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

	private String render(Envelope<C2D> bbox, String query) {
		ST st = fillInTemplateParams( bbox, query );
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
