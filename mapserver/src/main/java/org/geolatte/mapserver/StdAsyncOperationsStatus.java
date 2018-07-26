package org.geolatte.mapserver;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by Karel Maesen, Geovise BVBA on 25/07/2018.
 */
class StdAsyncOperationsStatus implements AsyncOperationsStatus {

    final private AtomicReference<Timer> cleaner = new AtomicReference<>(mkTimer(6, TimeUnit.HOURS));
    final private ConcurrentMap<UUID, AsyncOperation> operations = new ConcurrentHashMap<>();

    @Override
    public void scheduleCleanupEvery(int n, TimeUnit unit) {
        this.cleaner.updateAndGet(timer -> updateTimer(timer, n, unit));
    }

    @Override
    public void register(UUID id, AsyncOperation operation) {
        operations.putIfAbsent(id, operation);
    }

    @Override
    public Optional<AsyncOperation> get(UUID id) {
        return Optional.ofNullable(operations.get(id));
    }

    private Timer updateTimer(Timer oldTimer, int n, TimeUnit unit) {
        oldTimer.cancel();
        return mkTimer(n, unit);
    }

    private Timer mkTimer(int n, TimeUnit unit) {

        TimerTask cleanup = new TimerTask() {

            @Override
            public void run() {
                operations.values().removeIf(AsyncOperation::isDone);
            }
        };
        Timer timer = new Timer(true);
        long period = unit.toMillis(n);
        timer.scheduleAtFixedRate(cleanup, period, period);
        return timer;
    }

}
