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

package org.geolatte.mapserver.protocols.wms_1_3_0;


import net.opengis.wms.v_1_3_0.ServiceExceptionReport;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Karel Maesen, Geovise BVBA
 * creation-date: Jul 17, 2010
 */
public class WmsServiceException extends Exception {

    protected WmsServiceExceptionList exceptionItems;

    public WmsServiceException(String message) {
        super(message);
    }

    public WmsServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public WmsServiceException(WmsServiceExceptionList exceptionItems) {
        this.exceptionItems = exceptionItems;
    }

    public void writeToOutputStream(OutputStream outputStream) {
        ServiceExceptionReport report = WmsJaxb.instance().createServiceExceptionReport(exceptionItems);
        WmsJaxb.instance().marshal(report, outputStream);
    }

    public List<String> getCodes() {
        List<String> list = new ArrayList<String>();
        if (this.exceptionItems == null) return list;
        for (WmsServiceExceptionList.Item item : this.exceptionItems) {
            if (item.getCode() != null)
                list.add(item.getCode().toString());
        }
        return list;
    }

    public WmsServiceExceptionList getExceptionItems() {
        return exceptionItems;
    }

    public enum Code {
        InvalidFormat,
        InvalidSRS,
        LayerNotDefined,
        StyleNotDefined,
        LayerNotQueryable,
        CurrentUpdateSequence,
        InvalidUpdateSequence,
        MissingDimensionValue,
        InvalidDimensionValue
    }

}
