package org.geolatte.mapserver.rxhttp;

import be.wegenenverkeer.rxhttp.ClientRequest;
import be.wegenenverkeer.rxhttp.RxHttpClient;
import org.geolatte.geom.C2D;
import org.geolatte.geom.Envelope;
import org.geolatte.geom.Feature;
import org.geolatte.maprenderer.map.PlanarFeature;
import org.geolatte.mapserver.features.FeatureSource;
import org.stringtemplate.v4.ST;
import rx.Observable;

import java.nio.charset.Charset;
import java.util.Optional;

import static java.lang.String.format;

/**
 * Created by Karel Maesen, Geovise BVBA on 19/07/2018.
 */
public class RxHttpFeatureSource implements FeatureSource {


    final private static Charset UTF8 = Charset.forName("UTF-8");
    final private String template;
    final private RxHttpClient client;


    public RxHttpFeatureSource(RxHttpFeatureSourceConfig config) {
        this.template = config.getTemplate();
        String host = config.getHost();

        this.client = new RxHttpClient.Builder()
                .setAccept("application/json")
                .setBaseUrl(host)
                .build();
    }

    @Override
    public Observable<PlanarFeature> query(Envelope<C2D> bbox, String query) {
        String queryUrl = render(bbox, query);
        ClientRequest request = client.requestBuilder()
                .setUrlRelativetoBase(queryUrl)
                .build();
       GeoJsonDeserializer deserializer = new GeoJsonDeserializer();
       return client.executeObservably(request, bytes -> new String(bytes, UTF8))
               .flatMap( deserializer::deserialize);
    }

    private String render(Envelope<C2D> bbox, String query) {
        ST st = fillInTemplateParams(bbox, query);
        return st.render();
    }

    private ST fillInTemplateParams(Envelope<C2D> bbox, String query) {
        ST st = new ST(template);
        st.add("bbox", asString(bbox));
        if (query != null) {
            st.add(query, query);
        }
        return st;
    }

    private String asString(Envelope<C2D> bbox) {
        C2D ll = bbox.lowerLeft();
        C2D ur = bbox.upperRight();
        return format("%f,%f,%f,%f", ll.getX(), ll.getY(), ur.getX(), ur.getY());
    }

    @Override
    public void close() {
        if(client != null) {
            client.close();
        }
    }
}
