package org.geolatte.mapserver.config;

import org.geolatte.mapserver.ServiceMetadata;
import org.geolatte.mapserver.image.ImageFormat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Karel Maesen, Geovise BVBA on 19/07/2018.
 */
class MetadataBuilder {
    private ServiceMetadata.ServiceIdentification si;

    private ServiceMetadata.ServiceProvider provider;
    private List<ServiceMetadata.Operation> operations;
    private String onlineResource;

    ServiceIdentificationBuilder serviceIdentifaction() {
        return new ServiceIdentificationBuilder(this);
    }

    MetadataBuilder serviceProvider(String name) {
        this.provider = new ServiceMetadata.ServiceProvider(name);
        return this;
    }

    OperationsBuilder operations() {
        return new OperationsBuilder(this);
    }

    //For compatibility with WMS Capabilities doc
    MetadataBuilder onlineResource(String url) {
        this.onlineResource = url;
        return this;
    }

    ServiceMetadata build() {
        ServiceMetadata.OperationsMetadata opMetadata = new ServiceMetadata.OperationsMetadata(operations);
        return new ServiceMetadata(si, provider, opMetadata, "1.3.0", onlineResource);
    }

    static class ServiceIdentificationBuilder {
        private String serviceTypeURN;
        private String serviceTypeVersion;
        private String title;
        private String abstractText;
        private List<String> keywords = new ArrayList<>();
        private MetadataBuilder next;

        ServiceIdentificationBuilder(MetadataBuilder next) {
            this.next = next;
        }

        ServiceIdentificationBuilder serviceTypeURN(String v) {
            this.serviceTypeURN = v;
            return this;
        }

        ServiceIdentificationBuilder serviceTypeVersion(String v) {
            this.serviceTypeVersion = v;
            return this;
        }

        ServiceIdentificationBuilder title(String v) {
            this.title = v;
            return this;
        }

        ServiceIdentificationBuilder abstractText(String v) {
            this.abstractText = v;
            return this;
        }

        ServiceIdentificationBuilder keywords(List<String> v) {
            this.keywords.addAll(v);
            return this;
        }

        MetadataBuilder end() {
            next.si = new ServiceMetadata.ServiceIdentification(serviceTypeURN, serviceTypeVersion, title, abstractText, keywords);
            return next;
        }
    }

    static class OperationsBuilder {
        private MetadataBuilder next;
        private List<ServiceMetadata.Operation> ops = new ArrayList<>();

        OperationsBuilder(MetadataBuilder next) {
            this.next = next;
        }

        OperationsBuilder addGetCapabilitiesOperation(String getURL) {
            ops.add(new ServiceMetadata.GetCapabilitiesOperation(getURL));
            return this;
        }

        OperationsBuilder addGetMapOperation(String getURL, List<ImageFormat> formats) {
            ops.add(new ServiceMetadata.GetMapOperation(getURL, formats));
            return this;
        }

        MetadataBuilder end() {
            this.next.operations = ops;
            return this.next;
        }

    }
}
