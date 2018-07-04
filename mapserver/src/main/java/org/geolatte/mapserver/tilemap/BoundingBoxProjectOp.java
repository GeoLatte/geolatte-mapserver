package org.geolatte.mapserver.tilemap;


import org.geolatte.geom.C2D;
import org.geolatte.geom.Envelope;
import org.geolatte.geom.crs.CoordinateReferenceSystem;
import org.geolatte.mapserver.image.Image;
import org.geolatte.mapserver.transform.CoordinateTransforms;
import org.geolatte.mapserver.image.Imaging;
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
public class BoundingBoxProjectOp implements TileMapOperation<Image> {

    private final static Logger logger = LoggerFactory.getLogger(BoundingBoxProjectOp.class);

    private final TileMap tileMap;
    private final Envelope<C2D> requestedBBox;
    private final Envelope<C2D> sourceBBox;
    private final CoordinateReferenceSystem<C2D> requestedSRS;

    private final Imaging imaging;

    private Dimension sourceDimension;
    private TileSet tileSet;
    private Set<Tile> tiles;
    private Set<Image> images;
    private double requestXUnitsPerPixel;
    private double requestYUnitsPerPixel;

    //needs to be injected
    private CoordinateTransforms coordinateTransforms;

    public BoundingBoxProjectOp(TileMap tileMap, Envelope<C2D> bbox, CoordinateReferenceSystem<C2D> srs, Dimension dimension, Imaging imaging) {
        this.tileMap = tileMap;
        this.requestedBBox = bbox;
        this.requestedSRS = srs;
        Dimension requestDimension = dimension;
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
        this.sourceDimension = requestDimension; //temporary fix.

    }

    @Override
    public Image execute() {
        Chrono chrono = new Chrono();

        BoundingBoxOp bboxOp = new BoundingBoxOp(tileMap, sourceBBox, sourceDimension, imaging);
        Image srcImg = bboxOp.execute();


        //reproject using warp
        chrono.reset();
        //before warping, set the output image to encompass the total boundingbox
        Envelope<C2D> srcBBoxInTargetSRS = coordinateTransforms.transform(sourceBBox, this.requestedSRS);
        PixelRange srcPixelRange = new PixelRange(srcImg.getMinX(), srcImg.getMinY(), srcImg.getWidth(), srcImg.getHeight());
        MapUnitToPixelTransform mupTransform = new MapUnitToPixelTransform(sourceBBox, srcPixelRange);
        //TODO -- targetMupTransform works only when target and source SRS use the same units.
        MapUnitToPixelTransform targetMupTransform = new MapUnitToPixelTransform(
                srcBBoxInTargetSRS, new PixelRange(0, 0,
                (int) (width(srcBBoxInTargetSRS) / requestXUnitsPerPixel),
                (int) (height(srcBBoxInTargetSRS) / requestYUnitsPerPixel))
        );
        Image projectedImage = imaging.reprojectByWarping(srcImg, mupTransform, tileMap.getSRS(), requestedSRS, targetMupTransform, 0.333);
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
            Image result = imaging.crop(projectedImage, srcPixelRange);
            logger.debug("Warped image clipping took " + chrono.stop() + " ms.");
            return result;
        } catch (Exception e) {
            logger.warn("Error on image crop.");
            throw new RuntimeException(e);
        }

    }

}
