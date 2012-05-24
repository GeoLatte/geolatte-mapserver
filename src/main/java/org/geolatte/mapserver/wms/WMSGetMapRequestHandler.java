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

package org.geolatte.mapserver.wms;

import org.apache.log4j.Logger;
import org.geolatte.geom.crs.CrsId;
import org.geolatte.mapserver.tms.TileImage;
import org.geolatte.mapserver.tms.TileMap;
import org.geolatte.mapserver.tms.TileMapRegistry;
import org.geolatte.mapserver.util.Chrono;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Karel Maesen, Geovise BVBA
 *         creation-date: Jul 22, 2010
 */
public class WMSGetMapRequestHandler implements WMSRequestHandler {

    final static Logger LOGGER = Logger.getLogger(WMSGetMapRequestHandler.class);
    private final TileMapRegistry tileMapRegistry;

    WMSGetMapRequestHandler(TileMapRegistry tileMapRegistry) {
        this.tileMapRegistry = tileMapRegistry;
    }

    @Override
    public void executeAndWriteTo(WMSRequest wmsRequest, OutputStream out) throws WMSServiceException {
        Chrono chrono = new Chrono();
        WMSGetMapRequest request = (WMSGetMapRequest) wmsRequest;
        WMSCapabilities.check(request);
        TileMap tileMap = getTileMapAndCheck(request);
        LOGGER.debug("GetMap Request received: " + request.toString());
        try {
            TileImage image = getMapImage(request, tileMap);
            chrono.reset();
            write(wmsRequest, out, image);
            LOGGER.debug("Wrote image to outputstream in " + chrono.stop() + " ms.");
        } catch (Exception e) { //TODO: don't catch on class 'Exception'
            throw new WMSServiceException(e.getMessage(),e);
        }
        LOGGER.debug("Response in " + chrono.total() + " ms.");
    }

    private TileImage getMapImage(WMSGetMapRequest request, TileMap tileMap) throws WMSServiceException {

        TileImage image = tileMap.getBoundingBoxOpFactory().create(request, tileMap).execute();

        if (image == null) {
            throw new WMSServiceException("Null-image received.");
        } else {
            LOGGER.debug("Created image.");
            return image;
        }


    }

    private TileMap getTileMapAndCheck(WMSGetMapRequest request) throws WMSServiceException {
        WMSServiceExceptionList exceptionList = new WMSServiceExceptionList();

        String[] layers = request.getLayers();
        if (layers.length > 1) {
            exceptionList.add("This WMS Service accepts only GetMap requests for one layer at  a time.",
                    WMSServiceException.CODE.LayerNotDefined);
        }
        String layer = layers[0];
        TileMap tileMap = tileMapRegistry.getTileMap(layer);
        if (tileMap == null) {
            exceptionList.add(String.format("Layer %s not defined", layer), WMSServiceException.CODE.LayerNotDefined);
        }
        checkSRS(request, exceptionList, tileMap);

        if (!exceptionList.isEmpty()) throw new WMSServiceException(exceptionList);
        return tileMap;
    }

    private void checkSRS(WMSGetMapRequest request, WMSServiceExceptionList exceptionList, TileMap tileMap) {
        if (tileMap == null) return;
        if (!isSupported(tileMap, request.getSrs())) {
            exceptionList.add(String.format("GetMap request specified unsupported SRS: %s",
                    request.getSrs()),
                    WMSServiceException.CODE.InvalidSRS);
        }
    }

    private void write(WMSRequest wmsRequest, OutputStream out, TileImage image) throws IOException {
        image.write(out, wmsRequest.getResponseContentType());
    }

    private boolean isSupported(TileMap tileMap, CrsId srs) {
        return this.tileMapRegistry.supportsSRS(tileMap.getTitle(), srs);
    }


}
