package org.geolatte.mapserver.rxhttp;

import be.wegenenverkeer.rxhttp.ClientRequest;
import be.wegenenverkeer.rxhttp.ClientRequestBuilder;
import be.wegenenverkeer.rxhttp.RxHttpClient;
import org.geolatte.geom.C2D;
import org.geolatte.geom.Envelope;
import org.geolatte.maprenderer.map.PlanarFeature;
import org.geolatte.mapserver.features.FeatureDeserializer;
import org.geolatte.mapserver.features.FeatureSource;
import org.stringtemplate.v4.ST;
import rx.Observable;

import java.nio.charset.Charset;
import java.util.Locale;

/**
 * Created by Karel Maesen, Geovise BVBA on 19/07/2018.
 */
public class RxHttpFeatureSource implements FeatureSource {


    final private static Charset UTF8 = Charset.forName("UTF-8");
    final private String template;
    final private boolean gzip;
    final private RxHttpClient client;
    final private FeatureDeserializerFactory featureDeserializerFactory;

    public RxHttpFeatureSource(RxHttpFeatureSourceConfig config, FeatureDeserializerFactory deserFactory) {
        this.template = config.getTemplate();
        String host = config.getHost();
        this.gzip = config.getGzip() == null ? true : config.getGzip();
        this.featureDeserializerFactory = deserFactory;

        this.client = new RxHttpClient.Builder()
                .setAccept("application/json")
                .setBaseUrl(host)
                .build();
    }

    @Override
    public Observable<PlanarFeature> query(Envelope<C2D> bbox, String query) {
        String queryUrl = render(bbox, query);
        ClientRequestBuilder builder = client.requestBuilder().setUrlRelativetoBase(queryUrl);
        if (this.gzip) {
            builder = builder.addHeader("Accept-Encoding", "gzip");
        }
        ClientRequest request = builder.build();

        ChunkSplitter chunkSplitter = new ChunkSplitter();
        final FeatureDeserializer deserializer = featureDeserializerFactory.featureDeserializer();
        return client
                .executeObservably(request, bytes -> new String(bytes, UTF8))
                .flatMapIterable(chunkSplitter::split)
                .flatMapIterable(deserializer::deserialize);
    }

    // for testing
    protected FeatureDeserializerFactory getFeatureDeserializerFactory(){
        return this.featureDeserializerFactory;
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
        return String.format(Locale.ROOT, "%f,%f,%f,%f", ll.getX(), ll.getY(), ur.getX(), ur.getY());
    }

    @Override
    public void close() {
        if (client != null) {
            client.close();
        }
    }
}
