package org.geolatte.mapserver;

import org.geolatte.mapserver.instrumentation.Timer;

public interface Instrumentation {

    Timer getCreateMapImageTimer(double mapUnitsPerPixel);

    Timer getLoadFeaturesTimer(double mapUnitsPerPixel);

}
