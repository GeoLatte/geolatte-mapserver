package org.geolatte.mapserver.instrumentation;

import java.util.concurrent.atomic.AtomicBoolean;

public class StopOnceTimerWrapper implements Timer {

    private AtomicBoolean stopped = new AtomicBoolean(false);
    private final Timer delegate;

    public StopOnceTimerWrapper(Timer delegate) {
        this.delegate = delegate;
    }

    public void stopOnce() {
        if (stopped.compareAndSet(false, true)) {
            delegate.stop();
        }
    }

    @Override
    public void stop() {
        stopOnce();
    }
}
