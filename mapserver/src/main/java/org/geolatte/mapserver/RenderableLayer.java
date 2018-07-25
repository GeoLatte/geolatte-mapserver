package org.geolatte.mapserver;

/**
 * A {@code Layer} That can be rendered
 *
 * Created by Karel Maesen, Geovise BVBA on 25/07/2018.
 */
public interface RenderableLayer extends Layer {

    public AsyncOperation renderLayer();

}
