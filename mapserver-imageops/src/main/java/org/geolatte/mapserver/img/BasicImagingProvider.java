package org.geolatte.mapserver.img;

import org.geolatte.mapserver.image.Imaging;
import org.geolatte.mapserver.spi.ImagingProvider;

import static java.lang.String.format;

/**
 * Created by Karel Maesen, Geovise BVBA on 13/04/2018.
 */

public class BasicImagingProvider implements ImagingProvider {


    @Override
    public Imaging imaging() {
        return new BasicImaging();
    }

}

