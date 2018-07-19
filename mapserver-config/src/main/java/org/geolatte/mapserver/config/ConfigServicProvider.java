package org.geolatte.mapserver.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.geolatte.mapserver.FeatureSourceFactoryRegistry;
import org.geolatte.mapserver.LayerRegistry;
import org.geolatte.mapserver.ServiceMetadata;
import org.geolatte.mapserver.image.ImageFormat;
import org.geolatte.mapserver.spi.LayerRegistryProvider;
import org.geolatte.mapserver.spi.ServiceMetadataProvider;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Created by Karel Maesen, Geovise BVBA on 06/07/2018.
 */
public class ConfigServicProvider implements ServiceMetadataProvider, LayerRegistryProvider {


    final private static String PREFIX = "geolatte-mapserver";
    final private Config mainConfig;

    public ConfigServicProvider() {
        mainConfig = ConfigFactory.load();
    }

    // provided for unit testing
    ConfigServicProvider(String resourceBasename) {
        mainConfig = ConfigFactory.load(resourceBasename);
    }

    @Override
    public ServiceMetadata serviceMetadata() {
        return buildServiceMetadata();
    }

    @Override
    public LayerRegistry layerSourceRegistry(FeatureSourceFactoryRegistry featureSourceFactoryRegistry) {
        Config cf = mainConfig.getConfig(PREFIX);
        Config layersCf = cf.getConfig("layers");
        return new LayerRegistryBuilder(featureSourceFactoryRegistry, layersCf).build();
    }


    //TODO - refactor this to the MetadataBuilder class
    private ServiceMetadata buildServiceMetadata() {
        Config cf = mainConfig.getConfig(PREFIX);
        Config ident = cf.getConfig("service-identifcation");

        MetadataBuilder builder = new MetadataBuilder();
        builder.onlineResource(cf.getString("oneline-resource"))
                .serviceIdentifaction()
                .serviceTypeURN(ident.getString("service-type-URN"))
                .serviceTypeVersion(ident.getString("service-type-version"))
                .abstractText(ident.getString("abstract-text"))
                .keywords(ident.getStringList("keywords"))
                .title(ident.getString("title"))
                .end()
                .serviceProvider(cf.getString("service-provider.name"));

        MetadataBuilder.OperationsBuilder operationsBuilder = builder.operations();
        for (Config opCfg : cf.getConfigList("operations")) {
            if ("GetMap".equalsIgnoreCase(opCfg.getString("name"))) {
                List<ImageFormat> formats = opCfg.getStringList("formats")
                        .stream()
                        .map(ImageFormat::valueOf)
                        .collect(toList());
                operationsBuilder.addGetMapOperation(opCfg.getString("url"), formats);
            }

            if ("GetCapabilities".equalsIgnoreCase(opCfg.getString("name"))) {
                operationsBuilder.addGetCapabilitiesOperation(opCfg.getString("url"));
            }

        }
        operationsBuilder.end();

        return builder.build();
    }


}
