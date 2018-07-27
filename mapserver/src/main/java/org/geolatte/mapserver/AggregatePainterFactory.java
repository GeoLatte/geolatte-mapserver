package org.geolatte.mapserver;

import org.geolatte.maprenderer.map.MapGraphics;
import org.geolatte.maprenderer.map.Painter;

import java.util.List;
import java.util.NoSuchElementException;

import static java.lang.String.format;

/**
 * Created by Karel Maesen, Geovise BVBA on 27/07/2018.
 */
public class AggregatePainterFactory implements PainterFactory {

    final private List<PainterFactory> factories;

    public AggregatePainterFactory(List<PainterFactory> factories) {
        this.factories = factories;
    }

    @Override
    public boolean canCreate(String ref) {
        for( PainterFactory factory : factories) {
            if(factory.canCreate(ref)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Painter mkPainter(String ref, MapGraphics mapGraphics) {
        for( PainterFactory factory : factories) {
            if(factory.canCreate(ref)) {
                return factory.mkPainter(ref, mapGraphics);
            }
        }
        throw new NoSuchElementException(format("Can't find a PainterFactory for painterref %s", ref));
    }

}
