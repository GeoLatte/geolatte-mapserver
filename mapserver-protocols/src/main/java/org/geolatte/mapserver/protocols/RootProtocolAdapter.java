package org.geolatte.mapserver.protocols;

import org.geolatte.mapserver.Capabilities;
import org.geolatte.mapserver.http.HttpRequest;
import org.geolatte.mapserver.ows.GetCapabilitiesRequest;
import org.geolatte.mapserver.ows.MapServerRequest;
import org.geolatte.mapserver.protocols.wms_1_3_0.Wms130ProtocolAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

/**
 * Created by Karel Maesen, Geovise BVBA on 04/07/2018.
 */
public class RootProtocolAdapter implements ProtocolAdapter {

    private final List<ProtocolAdapter> adapters = new ArrayList<>();

    public RootProtocolAdapter(){
        adapters.add(new Wms130ProtocolAdapter());
    }


    @Override
    public List<String> supportedProtocols() {
        return adapters.stream()
                .flatMap(l -> l.supportedProtocols().stream())
                .collect(toList());
    }

    @Override
    public MapServerRequest adapt(HttpRequest request) {
        return getFirst( p -> p.canHandle(request) ).map(p -> p.adapt(request))
                .orElseThrow( () ->
                        new RuntimeException(format("No registered Adapter can handle this ows %s", request.uri().toString()))
                );
    }

    @Override
    public byte[] adaptGetCapabilities(GetCapabilitiesRequest request, Capabilities capabilities) {
        return getFirst(p -> p.canFormat(request)).map(p -> p.adaptGetCapabilities(request, capabilities))
                .orElseThrow(() -> new RuntimeException(
                        format("No registered Adapter can output Capabilities to version %s for sevice %s",
                                request.getVersion(),
                                request.getService())
                        )
                );

    }


    @Override
    public boolean canHandle(HttpRequest request) {
        return getFirst( p -> p.canHandle(request)).isPresent();
    }

    @Override
    public boolean canFormat(GetCapabilitiesRequest request) {
        return getFirst(p -> p.canFormat(request)).isPresent();
    }


    private Optional<ProtocolAdapter> getFirst(Predicate<ProtocolAdapter> pred) {
        return adapters.stream().filter(pred).findFirst();
    }

}
