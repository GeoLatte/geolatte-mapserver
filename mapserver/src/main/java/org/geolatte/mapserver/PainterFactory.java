package org.geolatte.mapserver;

import org.geolatte.maprenderer.map.MapGraphics;
import org.geolatte.maprenderer.map.Painter;

/**
 * Created by Karel Maesen, Geovise BVBA on 27/07/2018.
 */
public interface PainterFactory {

    boolean canCreate(String ref);

    Painter mkPainter(String ref, MapGraphics mapGraphics);


}
