package org.geolatte.mapserver.coordinatetransforms;

import org.geolatte.geom.ApproximateGeometryEquality;
import org.geolatte.geom.C2D;
import org.geolatte.geom.G2D;
import org.geolatte.geom.Geometry;
import org.geolatte.geom.GeometryEquality;
import org.geolatte.geom.LineString;
import org.geolatte.geom.Point;
import org.geolatte.geom.Polygon;
import org.geolatte.geom.Position;
import org.geolatte.mapserver.transform.Transform;

import org.junit.Test;

import static org.geolatte.geom.builder.DSL.c;
import static org.geolatte.geom.builder.DSL.g;
import static org.geolatte.geom.builder.DSL.linestring;
import static org.geolatte.geom.builder.DSL.point;
import static org.geolatte.geom.builder.DSL.polygon;
import static org.geolatte.geom.builder.DSL.ring;
import static org.geolatte.geom.crs.CoordinateReferenceSystems.WEB_MERCATOR;
import static org.geolatte.geom.crs.CoordinateReferenceSystems.WGS84;
import static org.junit.Assert.assertTrue;

/**
 * Test class for Coordinate transformation.
 *
 * Created by Karel Maesen, Geovise BVBA on 2019-03-24.
 */
public class TestCoordinateTransform {

	static private GeometryEquality eq = new ApproximateGeometryEquality( 0.05 );

	GeolatteTransformFactory transforms = new GeolatteTransformFactory();
	Transform<G2D, C2D> transformOp = transforms.getTransform( WGS84, WEB_MERCATOR );

	@Test
	public void testPointTransform() {
		Point<G2D> pnt = point( WGS84, g( 5, 50 ) );
		Geometry<C2D> projected = transformOp.forward( pnt );
		assertEquals( point( WEB_MERCATOR, c( 556597.453966367, 6446275.84101716 ) ), projected );
	}

	@Test
	public void testLineStringTransform() {
		LineString<G2D> line = linestring( WGS84, g( 5.32, 51.3 ), g( 4.89, 50.76 ) );
		Geometry<C2D> projected = transformOp.forward( line );
		assertEquals( linestring(
				WEB_MERCATOR,
				c( (592219.691020215), 6674532.79847308 ),
				c( 544352.309979108, 6578949.80039655 )
		), projected );
	}


	@Test
	public void testPolygonTransform() {
		Polygon<G2D> poly = polygon(
				WGS84,
				ring(
						g( 5.32, 51.3 ),
						g( 5.32, 51.4 ),
						g( 5.33, 51.4 ),
						g( 5.33, 51.3 ),
						g( 5.32, 51.3 )
				)
		);
		Geometry<C2D> projected = transformOp.forward( poly );
		assertEquals(
				polygon(
						WEB_MERCATOR,
						ring(
								c( 592219.691020215, 6674532.79847308 ),
								c( 592219.691020215, 6692356.43526254 ),
								c( 593332.885928148, 6692356.43526254 ),
								c( 593332.885928148, 6674532.79847308 ),
								c( 592219.691020215, 6674532.79847308 )
						)
				), projected
		);


	}

	private static <P extends Position> void assertEquals(Geometry<P> g1, Geometry<P> g2) {
		assertTrue( eq.equals( g1, g2 ) );
	}

}
