package org.geolatte.mapserver.rxhttp;

import org.geolatte.mapserver.features.FeatureSourceConfig;

import java.util.Objects;

/**
 * Created by Karel Maesen, Geovise BVBA on 20/07/2018.
 */
public class RxHttpFeatureSourceConfig implements FeatureSourceConfig {

    private String template;

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
