package org.geolatte.mapserver.boot;

import org.geolatte.mapserver.spi.Imaging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ServiceLoader;

import static java.lang.String.format;

/**
 * Created by Karel Maesen, Geovise BVBA on 01/05/2018.
 */
public class BootServiceRegistry implements ServiceRegistry{

    private static final Logger logger = LoggerFactory.getLogger(BootServiceRegistry.class);

    static Imaging imagingInstance;

    static {

        ServiceLoader<Imaging> imagingLoader = ServiceLoader.load(Imaging.class);
        for (Imaging img : imagingLoader) {
            logger.info(format("Loading %s for service Imaging", img.getClass().getCanonicalName()));
            imagingInstance = img;
            break;
        }

    }

    public Imaging getImaging() {
        return imagingInstance;
    }

}
