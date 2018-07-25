package org.geolatte.mapserver.tilemap;

import org.geolatte.mapserver.image.Image;
import org.geolatte.mapserver.ows.GetMapRequest;
import org.geolatte.mapserver.image.Imaging;

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

    public TileMapOperation<Image> create(GetMapRequest request, TileMap tileMap, Imaging imaging);



}
