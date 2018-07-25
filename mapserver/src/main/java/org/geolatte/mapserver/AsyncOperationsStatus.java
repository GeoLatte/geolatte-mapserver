package org.geolatte.mapserver;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Allows clients to register AsyncOperations so the status can be lookup
 * at a later date
 *
 * Created by Karel Maesen, Geovise BVBA on 25/07/2018.
 */
public interface AsyncOperationsStatus  {

    void scheduleCleanupEvery(int n, TimeUnit unit);

    static AsyncOperationsStatus INSTANCE = new StdAsyncOperationsStatus();

    void register(UUID id, AsyncOperation operation);

    Optional<AsyncOperation> get(UUID id);

}
