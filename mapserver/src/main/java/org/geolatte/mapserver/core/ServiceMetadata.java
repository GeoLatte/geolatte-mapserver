package org.geolatte.mapserver.core;

import java.util.List;

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
    private final Contents contents;
    private final Languages languages;
    private final String version;
    private final String onlineResource;

    public ServiceMetadata(ServiceIdentification serviceIdentification,
                           ServiceProvider serviceProvider,
                           OperationsMetadata operationsMetadata,
                           Contents contents,
                           Languages languages, String version, String onlineResource) {
        this.serviceIdentification = serviceIdentification;
        this.serviceProvider = serviceProvider;
        this.operationsMetadata = operationsMetadata;
        this.contents = contents;
        this.languages = languages;
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

    public Contents getContents() {
        return contents;
    }

    public Languages getLanguages() {
        return languages;
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
    }

    /**
     * The Contents element for the ServiceMetadata
     *
     * <p>This structure is modelled on the </p>
     */
    public static class Contents {
        final private List<LayerSource> layers;

        public Contents(List<LayerSource> layers) {
            this.layers = layers;
        }

        public List<LayerSource> getLayers() {
            return layers;
        }
    }

    public static class Languages {
        final private List<String> languages;


        public Languages(List<String> languages) {
            this.languages = languages;
        }

        public List<String> getLanguages() {
            return languages;
        }
    }

    public static class Operation{


        final private String name;
        final private String getURL;

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
    }

    public static class GetCapabilitiesOperation extends Operation{

        public GetCapabilitiesOperation(String getURL) {
            super(GET_CAPABILITIES_OP, getURL);
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
    }

}
