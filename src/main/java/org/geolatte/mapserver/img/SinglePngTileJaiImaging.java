/*
 * Copyright 2009-2010  Geovise BVBA, QMINO BVBA
 *
 * This file is part of GeoLatte Mapserver.
 *
 * GeoLatte Mapserver is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GeoLatte Mapserver is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GeoLatte Mapserver.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.geolatte.mapserver.img;

import com.sun.media.jai.codec.PNGDecodeParam;
import com.sun.media.jai.codec.SeekableStream;
import org.apache.log4j.Logger;
import org.geolatte.mapserver.tms.TileImage;
import org.geolatte.mapserver.util.PixelRange;

import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.PNGDescriptor;
import javax.media.jai.operator.TranslateDescriptor;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

/**
 * <p/>
 * JAIImaging subclass that is faster then the common JAIImaging,
 * because it reads only PNG's and does no mosaic, crop and scale operations.
 * <p>
 * <i>Creation-Date</i>: 28/12/11<br>
 * <i>Creation-Time</i>:  9:31<br>
 * </p>
 *
 * @author Jeroen
 * @author <a href="http://www.qmino.com">Qmino bvba</a>
 */
public class SinglePngTileJaiImaging extends JAIImaging {

    private final static Logger LOGGER = Logger.getLogger(SinglePngTileJaiImaging.class);

    @Override
    public TileImage read(InputStream is, int x, int y, boolean forceArgb) throws IOException {
        SeekableStream stream = SeekableStream.wrapInputStream(is, false);
        PNGDecodeParam decodeParam = new PNGDecodeParam();
        if(forceArgb) {
            decodeParam.setExpandPalette(true);
        }
        RenderedOp loadOp = PNGDescriptor.create(stream, decodeParam, null);
        RenderedOp translateOp = TranslateDescriptor.create(loadOp, (float) x, (float) y, null, null);
        PlanarImage image = translateOp.createInstance();

        return new JAITileImage(image);
    }

    @Override
    public TileImage mosaic(Set<TileImage> images, PixelRange bounds) {
        if(images.size() > 1)
            LOGGER.error("Loaded more than 1 tile for bounds " + bounds);
        return images.iterator().next();
    }

    @Override
    public TileImage crop(TileImage image, PixelRange cropBounds) {
        return image;
    }

    @Override
    public TileImage scale(TileImage image, Dimension dimension) {
        return image;
    }

    @Override
    public TileImage scale(TileImage image, float xScale, float yScale) {
        return image;
    }
}
