package org.geolatte.mapserver.instrumentation;

import org.geolatte.mapserver.Instrumentation;
import org.geolatte.mapserver.spi.InstrumentationProvider;

public class NoOpInstrumentationProvider implements InstrumentationProvider {

    @Override
    public Instrumentation instrumentation() {
        return new NoOpInstrumentation();
    }
}
