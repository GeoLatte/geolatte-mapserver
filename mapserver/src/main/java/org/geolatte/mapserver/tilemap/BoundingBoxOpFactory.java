package org.geolatte.mapserver.tilemap;

import org.geolatte.mapserver.core.MapRequest;
import org.geolatte.mapserver.spi.Imaging;

/**
 * <p/>
 * <p>
 * <i>Creation-Date</i>: 16/09/11<br>
 * <i>Creation-Time</i>:  16:43<br>
 * </p>
 *
 * @author Jeroen
 * @author <a href="http://www.qmino.com">Qmino bvba</a>
 */
public interface BoundingBoxOpFactory {

    public TileMapOperation<TileImage> create(MapRequest request, TileMap tileMap, Imaging imaging);

}
