package org.geolatte.mapserver.core;

import org.geolatte.maprenderer.map.Painter;

/**
 * A Style is a named factory for Painters
 */
public interface Style {

    String getName();

    Painter buildPainter();

}
