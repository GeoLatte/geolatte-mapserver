package org.geolatte.mapserver.wms;

import org.geolatte.mapserver.img.JAIImaging;
import org.geolatte.mapserver.tms.*;

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
    public TileMapOperation<TileImage> create(WMSGetMapRequest request, TileMap tileMap) {
        if (tileMap.getSRS().equals(request.getSrs())) {
            return new BoundingBoxOp(tileMap, request.getBbox(), request.getDimension(), new JAIImaging());
        } else {
            return new BoundingBoxProjectOp(tileMap, request.getBbox(), request.getSrs(), request.getDimension(), new JAIImaging());
        }
    }

}
