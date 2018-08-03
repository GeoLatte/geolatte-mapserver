package org.geolatte.mapserver.protocols.wms_1_3_0;

import net.opengis.wms.v_1_3_0.WMSCapabilities;
import org.geolatte.mapserver.Capabilities;
import org.geolatte.mapserver.ServiceMetadata;
import org.geolatte.mapserver.ows.GetCapabilitiesRequest;
import org.geolatte.mapserver.ows.MapServerRequest;
import org.geolatte.mapserver.protocols.ProtocolAdapter;
import org.geolatte.mapserver.http.HttpRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by Karel Maesen, Geovise BVBA on 05/07/2018.
 */
public class Wms130ProtocolAdapter implements ProtocolAdapter {

    final private WmsJaxb jaxb = WmsJaxb.instance();

    @Override
    public List<String> supportedProtocols() {
        return null;
    }

    @Override
    public MapServerRequest adapt(HttpRequest request) {
        try {
            WmsRequest wmsRequest =  WmsRequest.adapt(request);
            return wmsRequest.toMapServerRequest();
        } catch (InvalidWmsRequestException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] adaptGetCapabilities(GetCapabilitiesRequest request, Capabilities capabilities) {
        ServiceMetadata serviceMetadata = capabilities.getServiceMetadata();
        WMSCapabilities wmsCapabilities = jaxb.createWMSCapabilities(serviceMetadata);
        return toBytes(wmsCapabilities);
    }

    @Override
    public boolean canHandle(HttpRequest request) {
        return WmsRequest.canHandle(request);
    }

    @Override
    public boolean canFormat(GetCapabilitiesRequest request) {
        return request.getVersion().equalsIgnoreCase("1.3.0") &&
                request.getService().equalsIgnoreCase("WMS");
    }

    private byte[] toBytes(WMSCapabilities document){
        try(ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            jaxb.marshal(document, out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


}
