package org.geolatte.mapserver.styles;

import org.geolatte.maprenderer.map.Painter;

/**
 * A Style is a named factory for Painters
 */
public interface Style {

    String getName();

    Painter buildPainter();

}
