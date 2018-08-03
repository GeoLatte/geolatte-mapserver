package org.geolatte.mapserver;

import org.geolatte.mapserver.image.ImageFormat;

import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.joining;

/**
 * Models the Service metadata for the MapServer.
 *
 * <P>This implementation is based on the OGC Web Service Common Implementation Specification, v 2.0.0 specification, and
 * the WMTS specification v. 1.0.0.
 * </P>
 * <p>
 * Created by Karel Maesen, Geovise BVBA on 08/05/2018.
 */
public class ServiceMetadata {

    final static public String GET_CAPABILITIES_OP = "GetCapabilities";
    final static public String GET_MAP_OP = "GetMap";

    private final ServiceIdentification serviceIdentification;
    private final ServiceProvider serviceProvider;
    private final OperationsMetadata operationsMetadata;
    private final String version;
    private final String onlineResource;

    public ServiceMetadata(ServiceIdentification serviceIdentification,
                           ServiceProvider serviceProvider,
                           OperationsMetadata operationsMetadata,
                           String version, String onlineResource) {
        this.serviceIdentification = serviceIdentification;
        this.serviceProvider = serviceProvider;
        this.operationsMetadata = operationsMetadata;
        this.version = version;
        this.onlineResource = onlineResource;
    }


    public ServiceIdentification getServiceIdentification() {
        return serviceIdentification;
    }

    public ServiceProvider getServiceProvider() {
        return serviceProvider;
    }

    public List<Operation> getOperations() {
        return operationsMetadata.operations;
    }

    public String getVersion() {
        return version;
    }

    public String getOnlineResource() {
        return onlineResource;
    }

    public static class ServiceIdentification {
        private final String serviceTypeURN;
        private final String serviceTypeVersion;
        private final String title;
        private final String abstractText;
        private final List<String> keywords;


        public ServiceIdentification(String serviceTypeURN, String serviceTypeVersion, String title, String abstractText, List<String> keywords) {
            this.serviceTypeURN = serviceTypeURN;
            this.serviceTypeVersion = serviceTypeVersion;
            this.title = title;
            this.abstractText = abstractText;
            this.keywords = keywords;
        }

        public String getServiceTypeURN() {
            return serviceTypeURN;
        }

        public String getServiceTypeVersion() {
            return serviceTypeVersion;
        }

        public String getTitle() {
            return title;
        }

        public String getAbstractText() {
            return abstractText;
        }

        public List<String> getKeywords() {
            return keywords;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ServiceIdentification that = (ServiceIdentification) o;
            return Objects.equals(serviceTypeURN, that.serviceTypeURN) &&
                    Objects.equals(serviceTypeVersion, that.serviceTypeVersion) &&
                    Objects.equals(title, that.title) &&
                    Objects.equals(abstractText, that.abstractText) &&
                    Objects.equals(keywords, that.keywords);
        }

        @Override
        public int hashCode() {

            return Objects.hash(serviceTypeURN, serviceTypeVersion, title, abstractText, keywords);
        }

        @Override
        public String toString() {
            return "ServiceIdentification{" +
                    "serviceTypeURN='" + serviceTypeURN + '\'' +
                    ", serviceTypeVersion='" + serviceTypeVersion + '\'' +
                    ", title='" + title + '\'' +
                    ", abstractText='" + abstractText + '\'' +
                    ", keywords=" + keywords +
                    '}';
        }
    }

    public static class ServiceProvider {
        private final String providerName;

        public ServiceProvider(String providerName) {
            this.providerName = providerName;
        }

        public String getProviderName() {
            return providerName;
        }
    }

    public static class OperationsMetadata {
        final private List<Operation> operations;

        public OperationsMetadata(List<Operation> operations) {
            this.operations = operations;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            OperationsMetadata that = (OperationsMetadata) o;
            return Objects.equals(operations, that.operations);
        }

        @Override
        public int hashCode() {
            return Objects.hash(operations);
        }

        @Override
        public String toString() {
            return "OperationsMetadata{" +
                    "operations=" + operations +
                    '}';
        }
    }
    
    public abstract static class Operation{


        final protected String name;
        final protected String getURL;

        public Operation(String name, String getURL) {
            this.name = name;
            this.getURL = getURL;
        }

        public String getName() {
            return name;
        }

        public String getGetURL() {
            return getURL;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Operation)) return false;
            Operation operation = (Operation) o;
            return Objects.equals(getName(), operation.getName()) &&
                    Objects.equals(getGetURL(), operation.getGetURL());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getName(), getGetURL());
        }
    }

    public static class GetCapabilitiesOperation extends Operation{

        public GetCapabilitiesOperation(String getURL) {
            super(GET_CAPABILITIES_OP, getURL);
        }


        @Override
        public String toString() {
            return "GetCapabilitiesOperation{" +
                    "name='" + name + '\'' +
                    ", getURL='" + getURL + '\'' +
                    '}';
        }


    }

    public static class GetMapOperation extends Operation {
        final private List<ImageFormat> supportedFormats;

        public GetMapOperation(String getURL, List<ImageFormat> formats) {
            super(GET_MAP_OP, getURL);
            this.supportedFormats = formats;
        }

        public List<ImageFormat> getSupportedFormats() {
            return supportedFormats;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof GetMapOperation)) return false;
            if (!super.equals(o)) return false;
            GetMapOperation that = (GetMapOperation) o;
            return Objects.equals(getSupportedFormats(), that.getSupportedFormats());
        }

        @Override
        public int hashCode() {

            return Objects.hash(super.hashCode(), getSupportedFormats());
        }

        @Override
        public String toString() {
            return "GetMapOperation{" +
                    "name='" + name + '\'' +
                    ", getURL='" + getURL + '\'' +
                    ", supportedFormats=" + supportedFormats +
                    '}';
        }
    }

}
