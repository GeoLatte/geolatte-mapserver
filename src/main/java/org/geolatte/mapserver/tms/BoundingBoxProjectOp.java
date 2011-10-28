package org.geolatte.mapserver.tms;

import org.apache.log4j.Logger;
import org.geolatte.geom.Envelope;
import org.geolatte.geom.crs.CrsId;
import org.geolatte.mapserver.img.Imaging;
import org.geolatte.mapserver.referencing.Referencing;
import org.geolatte.mapserver.util.Chrono;
import org.geolatte.mapserver.util.PixelRange;

import java.awt.*;
import java.util.Set;

/**
 * @author Karel Maesen, Geovise BVBA
 *         creation-date: 7/1/11
 */
public class BoundingBoxProjectOp implements TileMapOperation{

    private final static Logger LOGGER = Logger.getLogger(BoundingBoxProjectOp.class);

    private final TileMap tileMap;
    private final Envelope requestedBBox;
    private final Envelope sourceBBox;
    private final CrsId requestedSRS;

    private final Dimension requestDimension;
    private final Imaging imaging;

    private Chrono chrono;
    private Dimension sourceDimension;
    private TileSet tileSet;
    private Set<Tile> tiles;
    private Set<TileImage> images;
    private double requestXUnitsPerPixel;
    private double requestYUnitsPerPixel;


    public BoundingBoxProjectOp(TileMap tileMap, Envelope bbox, CrsId srs, Dimension dimension, Imaging imaging) {
        this.tileMap = tileMap;
        this.requestedBBox = bbox;
        this.requestedSRS = srs;
        this.requestDimension = dimension;
        this.imaging = imaging;
        this.sourceBBox = Referencing.transform(this.requestedBBox, this.requestedSRS, this.tileMap.getSRS());
        this.requestXUnitsPerPixel = requestedBBox.getWidth() / requestDimension.getWidth();
        this.requestYUnitsPerPixel = requestedBBox.getHeight()/ requestDimension.getHeight();
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
        Envelope srcBBoxInTargetSRS = Referencing.transform(sourceBBox, this.tileMap.getSRS(), this.requestedSRS);
        PixelRange srcPixelRange = new PixelRange(srcImg.getMinX(), srcImg.getMinY(), srcImg.getWidth(), srcImg.getHeight());
        MapUnitToPixelTransform mupTransform = new MapUnitToPixelTransform(sourceBBox,srcPixelRange);
        //TODO -- targetMupTransform works only when target and source SRS use the same units.
        MapUnitToPixelTransform targetMupTransform = new MapUnitToPixelTransform(srcBBoxInTargetSRS, new PixelRange(0,0,(int)(srcBBoxInTargetSRS.getWidth()/requestXUnitsPerPixel), (int)(srcBBoxInTargetSRS.getHeight()/requestYUnitsPerPixel)));
        TileImage projectedImage = imaging.reprojectByWarping(srcImg, mupTransform, tileMap.getSRS(), requestedSRS, targetMupTransform, 0.333);
        LOGGER.debug("Image warping took " + chrono.stop() + " ms.");

        //crop to clipped bbox
        chrono.reset();
        srcPixelRange = targetMupTransform.toPixelRange(this.requestedBBox);
        //TODO -- clean up error-handling code
        try {
            LOGGER.debug("Source BBox in Target SRS: " + srcBBoxInTargetSRS);
            LOGGER.debug("Original BBOx:" + this.requestedBBox);
            LOGGER.debug("Image pixelRange:" + projectedImage.getMinX() + "," + projectedImage.getMinY() + "," + projectedImage.getMinX() + projectedImage.getWidth() + "," + projectedImage.getMinY() + projectedImage.getHeight());
            LOGGER.debug("Image request src PixelRange: " + srcPixelRange);
            TileImage result = imaging.crop(projectedImage, srcPixelRange);
            LOGGER.debug("Warped image clipping took " + chrono.stop() + " ms.");
            return result;
        }catch(Exception e){
            LOGGER.warn("Error on image crop.");
            throw new RuntimeException(e);
        }

    }

}
