package org.geolatte.mapserver.spi;

import org.geolatte.mapserver.image.Imaging;

/**
 * Created by Karel Maesen, Geovise BVBA on 06/07/2018.
 */
public interface ImagingProvider {
    Imaging imaging();
}
