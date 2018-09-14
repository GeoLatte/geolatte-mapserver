package org.geolatte.mapserver.instrumentation;

import org.geolatte.mapserver.Instrumentation;

public class NoOpInstrumentation implements Instrumentation {

    @Override
    public Timer getCreateMapImageTimer(double mapUnitsPerPixel) {
        return NoOpTimer.INSTANCE;
    }

    @Override
    public Timer getLoadFeaturesTimer(double mapUnitsPerPixel) {
        return NoOpTimer.INSTANCE;
    }
}
