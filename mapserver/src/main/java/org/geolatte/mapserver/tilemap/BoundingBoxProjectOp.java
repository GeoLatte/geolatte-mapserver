package org.geolatte.mapserver.tilemap;


import org.geolatte.geom.C2D;
import org.geolatte.geom.Envelope;
import org.geolatte.geom.crs.CoordinateReferenceSystem;
import org.geolatte.mapserver.spi.CoordinateTransforms;
import org.geolatte.mapserver.spi.Imaging;
import org.geolatte.mapserver.util.Chrono;
import org.geolatte.mapserver.util.PixelRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.Set;

import static org.geolatte.mapserver.util.EnvelopUtils.height;
import static org.geolatte.mapserver.util.EnvelopUtils.width;

/**
 * @author Karel Maesen, Geovise BVBA
 * creation-date: 7/1/11
 */
public class BoundingBoxProjectOp implements TileMapOperation<TileImage> {

    private final static Logger logger = LoggerFactory.getLogger(BoundingBoxProjectOp.class);

    private final TileMap tileMap;
    private final Envelope requestedBBox;
    private final Envelope sourceBBox;
    private final CoordinateReferenceSystem<?> requestedSRS;

    private final Dimension requestDimension;
    private final Imaging imaging;

    private Chrono chrono;
    private Dimension sourceDimension;
    private TileSet tileSet;
    private Set<Tile> tiles;
    private Set<TileImage> images;
    private double requestXUnitsPerPixel;
    private double requestYUnitsPerPixel;

    //needs to be injected
    private CoordinateTransforms coordinateTransforms;

    public BoundingBoxProjectOp(TileMap tileMap, Envelope<C2D> bbox, CoordinateReferenceSystem<?> srs, Dimension dimension, Imaging imaging) {
        this.tileMap = tileMap;
        this.requestedBBox = bbox;
        this.requestedSRS = srs;
        this.requestDimension = dimension;
        this.imaging = imaging;
        this.sourceBBox = coordinateTransforms.transform(this.requestedBBox, this.requestedSRS);
        this.requestXUnitsPerPixel = width(requestedBBox) / requestDimension.getWidth();
        this.requestYUnitsPerPixel = height(requestedBBox) / requestDimension.getHeight();
        //TODO -- calculate the proper dimension for the source image.
        // the commented-out code only works when the units between source and target SRS are the same.
        // as is the case between LCC and UTM and as is not for LCC/WGS-84
//        int srcWidth =  (int)Math.round(sourceBBox.getWidth()/requestXUnitsPerPixel);
//        int srcHeight = (int)Math.round(sourceBBox.getHeight()/requestYUnitsPerPixel);
//        this.sourceDimension = new Dimension(srcWidth, srcHeight);
        this.sourceDimension = this.requestDimension; //temporary fix.

    }

    @Override
    public TileImage execute() {
        chrono = new Chrono();

        BoundingBoxOp bboxOp = new BoundingBoxOp(tileMap, sourceBBox, sourceDimension, imaging);
        TileImage srcImg = bboxOp.execute();


        //reproject using warp
        chrono.reset();
        //before warping, set the output image to encompass the total boundingbox
        Envelope srcBBoxInTargetSRS = coordinateTransforms.transform(sourceBBox, this.requestedSRS);
        PixelRange srcPixelRange = new PixelRange(srcImg.getMinX(), srcImg.getMinY(), srcImg.getWidth(), srcImg.getHeight());
        MapUnitToPixelTransform mupTransform = new MapUnitToPixelTransform(sourceBBox, srcPixelRange);
        //TODO -- targetMupTransform works only when target and source SRS use the same units.
        MapUnitToPixelTransform targetMupTransform = new MapUnitToPixelTransform(
                srcBBoxInTargetSRS, new PixelRange(0, 0,
                (int) (width(srcBBoxInTargetSRS) / requestXUnitsPerPixel),
                (int) (height(srcBBoxInTargetSRS) / requestYUnitsPerPixel))
        );
        TileImage projectedImage = imaging.reprojectByWarping(srcImg, mupTransform, tileMap.getSRS(), requestedSRS, targetMupTransform, 0.333);
        logger.debug("Image warping took " + chrono.stop() + " ms.");

        //crop to clipped bbox
        chrono.reset();
        srcPixelRange = targetMupTransform.toPixelRange(this.requestedBBox);
        //TODO -- clean up error-handling code
        try {
            logger.debug("Source BBox in Target SRS: " + srcBBoxInTargetSRS);
            logger.debug("Original BBOx:" + this.requestedBBox);
            logger.debug("Image pixelRange:" + projectedImage.getMinX() + "," + projectedImage.getMinY() + "," + projectedImage.getMinX() + projectedImage.getWidth() + "," + projectedImage.getMinY() + projectedImage.getHeight());
            logger.debug("Image request src PixelRange: " + srcPixelRange);
            TileImage result = imaging.crop(projectedImage, srcPixelRange);
            logger.debug("Warped image clipping took " + chrono.stop() + " ms.");
            return result;
        } catch (Exception e) {
            logger.warn("Error on image crop.");
            throw new RuntimeException(e);
        }

    }

}
