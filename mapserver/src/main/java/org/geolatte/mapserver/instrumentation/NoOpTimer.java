package org.geolatte.mapserver.instrumentation;

public class NoOpTimer implements Timer {

    public static Timer INSTANCE = new NoOpTimer();

    @Override
    public void stop() {
    }
}
