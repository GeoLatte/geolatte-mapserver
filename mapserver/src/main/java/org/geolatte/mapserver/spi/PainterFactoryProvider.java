package org.geolatte.mapserver.spi;

import org.geolatte.mapserver.PainterFactory;

/**
 * Created by Karel Maesen, Geovise BVBA on 27/07/2018.
 */
public interface PainterFactoryProvider {

    PainterFactory painterFactory();

}
