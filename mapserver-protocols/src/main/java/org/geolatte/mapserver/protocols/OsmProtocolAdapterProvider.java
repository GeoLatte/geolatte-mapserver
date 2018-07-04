package org.geolatte.mapserver.protocols;

import org.geolatte.mapserver.spi.ProtocolAdapterProvider;

/**
 * Created by Karel Maesen, Geovise BVBA on 06/07/2018.
 */
public class OsmProtocolAdapterProvider implements ProtocolAdapterProvider {
    @Override
    public ProtocolAdapter protocolAdapter() {
        return new RootProtocolAdapter();
    }

}
