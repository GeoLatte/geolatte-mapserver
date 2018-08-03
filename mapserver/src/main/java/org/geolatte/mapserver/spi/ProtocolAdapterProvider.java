package org.geolatte.mapserver.spi;

import org.geolatte.mapserver.protocols.ProtocolAdapter;

/**
 * Created by Karel Maesen, Geovise BVBA on 06/07/2018.
 */
public interface ProtocolAdapterProvider {

    ProtocolAdapter protocolAdapter();
}
