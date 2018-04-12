package org.geolatte.mapserver.wms;

import org.geolatte.mapserver.tms.TileImage;
import org.geolatte.mapserver.tms.TileMap;
import org.geolatte.mapserver.tms.TileMapOperation;

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

    public TileMapOperation<TileImage> create(WMSGetMapRequest request, TileMap tileMap);

}
