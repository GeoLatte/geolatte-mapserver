package org.geolatte.mapserver.rxhttp;

import com.typesafe.config.Optional;
import org.geolatte.mapserver.features.FeatureSourceConfig;

import java.util.Objects;

/**
 * Created by Karel Maesen, Geovise BVBA on 20/07/2018.
 */
public class RxHttpFeatureSourceConfig implements FeatureSourceConfig {

    private String template;

    @Optional
    private Boolean gzip;

    @Optional
    private String featureDeserializerFactory;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    private String host;

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getFeatureDeserializerFactory( ) {
        return featureDeserializerFactory;
    }

    public void setFeatureDeserializerFactory(String featureDeserializerFactory) {
        this.featureDeserializerFactory = featureDeserializerFactory;
    }

    public Boolean getGzip() {
        return gzip;
    }

    public void setGzip(Boolean gzip) {
        this.gzip = gzip;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RxHttpFeatureSourceConfig that = (RxHttpFeatureSourceConfig) o;
        return Objects.equals(template, that.template);
    }

    @Override
    public int hashCode() {

        return Objects.hash(template);
    }
}
