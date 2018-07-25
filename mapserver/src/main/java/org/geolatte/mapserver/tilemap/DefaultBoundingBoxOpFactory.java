package org.geolatte.mapserver.tilemap;

import org.geolatte.mapserver.image.Image;
import org.geolatte.mapserver.ows.GetMapRequest;
import org.geolatte.mapserver.image.Imaging;

/**
 * <p/>
 * <p>
 * <i>Creation-Date</i>: 16/09/11<br>
 * <i>Creation-Time</i>:  16:50<br>
 * </p>
 *
 * @author Jeroen
 * @author <a href="http://www.qmino.com">Qmino bvba</a>
 */
public class DefaultBoundingBoxOpFactory implements BoundingBoxOpFactory {
    @Override
    public TileMapOperation<Image> create(GetMapRequest request, TileMap tileMap, Imaging imaging) {
        if (tileMap.getSRS().equals(request.getCrs())) {
            return new BoundingBoxOp(tileMap, request.getBbox(), request.getDimension(), imaging);
        } else {
            throw new UnsupportedOperationException("Reprojecting images is not supported ");
        }
    }


}
