package org.geolatte.mapserver.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Karel Maesen, Geovise BVBA on 09/05/2018.
 */
public class ServiceMetadataBuilder {

    private ServiceMetadata.ServiceIdentification si;

    private ServiceMetadata.ServiceProvider provider;
    private List<ServiceMetadata.Operation> operations;
    private String onlineResource;

    public ServiceIdentificationBuilder serviceIdentifaction() {
        return new ServiceIdentificationBuilder(this);
    }

    public ServiceMetadataBuilder serviceProvider(String name) {
        this.provider = new ServiceMetadata.ServiceProvider(name);
        return this;
    }

    public OperationsBuilder operations() {
        return new OperationsBuilder(this);
    }

    //For compatibility with WMS Capabilities doc
    public ServiceMetadataBuilder onlineResource(String url) {
        this.onlineResource = url;
        return this;
    }

    public ServiceMetadata build(){
        ServiceMetadata.OperationsMetadata opMetadata = new ServiceMetadata.OperationsMetadata(operations);
        return new ServiceMetadata(si, provider, opMetadata, null, null, "1.3.0", onlineResource);
    }

    public static class ServiceIdentificationBuilder {
        private String serviceTypeURN;
        private String serviceTypeVersion;
        private String title;
        private String abstractText;
        private List<String> keywords = new ArrayList<>();
        private ServiceMetadataBuilder next;

        ServiceIdentificationBuilder(ServiceMetadataBuilder next) {
            this.next = next;
        }

        public ServiceIdentificationBuilder serviceTypeURN(String v) {
            this.serviceTypeURN = v;
            return this;
        }

        public ServiceIdentificationBuilder serviceTypeVersion(String v) {
            this.serviceTypeVersion = v;
            return this;
        }

        public ServiceIdentificationBuilder title(String v) {
            this.title = v;
            return this;
        }

        public ServiceIdentificationBuilder abstractText(String v) {
            this.abstractText = v;
            return this;
        }

        public ServiceIdentificationBuilder keywords(String... v) {
            this.keywords.addAll(Arrays.asList(v));
            return this;
        }

        public ServiceMetadataBuilder end() {
            next.si = new ServiceMetadata.ServiceIdentification(serviceTypeURN, serviceTypeVersion, title, abstractText, keywords);
            return next;
        }
    }

    public static class OperationsBuilder {
        private ServiceMetadataBuilder next;
        private List<ServiceMetadata.Operation> ops = new ArrayList<>();

        OperationsBuilder(ServiceMetadataBuilder next) {
            this.next = next;
        }

        public OperationsBuilder addGetCapabilitiesOperation(String getURL) {
            ops.add(new ServiceMetadata.GetCapabilitiesOperation(getURL));
            return this;
        }

        public OperationsBuilder addGetMapOperation(String getURL, ImageFormat ...formats) {
            ops.add(new ServiceMetadata.GetMapOperation(getURL, Arrays.asList(formats)));
            return this;
        }

        public ServiceMetadataBuilder end() {
            this.next.operations = ops;
            return this.next;
        }

    }


}
