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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Karel Maesen, Geovise BVBA
 * creation-date: Jul 22, 2010
 */
public class WmsServiceExceptionList implements Iterable<WmsServiceExceptionList.Item> {


    List<Item> items = new ArrayList<Item>();

    public WmsServiceExceptionList add(String message, WmsServiceException.Code... code) {
        items.add(new Item(message, code));
        return this;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    @Override
    public Iterator<Item> iterator() {
        return items.iterator();
    }

    public static class Item {

        private final String message;
        private WmsServiceException.Code code = null;

        Item(String message, WmsServiceException.Code... code) {
            this.message = message;
            if (code.length > 0)
                this.code = code[0];
        }

        public WmsServiceException.Code getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

    }
}
