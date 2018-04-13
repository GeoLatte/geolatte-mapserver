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

package org.geolatte.mapserver.protocols.wms;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides access to global WMSGetCapabilities of this
 * server
 *
 * @author Karel Maesen, Geovise BVBA
 * creation-date: Jul 14, 2010
 */
public class WMSCapabilities {

    final public static String WMS = "WMS";
    final public static String TMS = "TMS";
    final public static String WMS_GETCAPABILITIES_REQUEST = "GetCapabilities";
    final public static String WMS_GETMAP_REQUEST = "GetMap";
    final public static String WMS_GETFEATURE_INFO = "GetFeatureInfo";


    static Map<String, String[]> serviceVersions = new HashMap<String, String[]>();
    static Map<String, String[]> serviceRequests = new HashMap<String, String[]>();

    static {
        serviceVersions.put(WMS, new String[]{"1.1.1"});
        serviceRequests.put(WMS, new String[]{WMS_GETCAPABILITIES_REQUEST, WMS_GETMAP_REQUEST});
    }

    public static String[] getSupportedVersions(String service) {
        return serviceVersions.get(service);
    }

    public static String[] getSupportedServices() {
        return serviceVersions.keySet().toArray(new String[0]);
    }


    public static String[] getSupportedRequests(String service) {
        return serviceRequests.get(WMS);
    }


    public static String[] getSupportedFormat(String service, String request) {

        if (service.equals(WMS) && request.equals(WMS_GETCAPABILITIES_REQUEST))
            return new String[]{OGCMIMETypes.CAPABILITIES};

        if (service.equals(WMS) && request.equals(WMS_GETMAP_REQUEST))
            return new String[]{OGCMIMETypes.PNG, OGCMIMETypes.JPEG};

        return new String[0];
    }

    public static String[] getSupportedExceptionFormat(String service) {
        if (service.equals(WMS))
            return new String[]{OGCMIMETypes.SERVICE_EXCEPTION_XML};
        return new String[0];
    }


    public static void check(WMSRequest request) throws WMSServiceException {
        WMSServiceExceptionList exceptionList = new WMSServiceExceptionList();
        checkFormat(request, exceptionList);
        checkService(request, exceptionList);
        if (!exceptionList.isEmpty()) {
            throw new WMSServiceException(exceptionList);
        }
    }

    public static void checkService(WMSRequest request, WMSServiceExceptionList list) throws WMSServiceException {
        Object value = request.get(WMSParam.SERVICE);
        if (value == null) return;
        checkType(request, value, String.class);
        String requestedService = (String) value;
        for (String service : WMSCapabilities.getSupportedServices()) {
            if (service.equalsIgnoreCase(requestedService)) {
                return;
            }
        }
        list.add("Service not supported");
    }

    private static void checkFormat(WMSRequest request, WMSServiceExceptionList list) {
        Object val = request.get(WMSParam.FORMAT);
        if (val == null) return;
        String[] formats = WMSCapabilities.getSupportedFormat(WMSCapabilities.WMS, (String) request.get(WMSParam.REQUEST));
        checkType(request, val, String.class);
        String reqFormat = (String) val;
        for (String format : formats) {
            if (format.equalsIgnoreCase(reqFormat)) return;
        }
        list.add(String.format("Image format %s is not supported.", reqFormat), WMSServiceException.CODE.InvalidFormat);

    }

    private static void checkType(WMSRequest request, Object val, Class<?> classObj) {
        if (!(classObj.isAssignableFrom(val.getClass()))) {
            throw new RuntimeException(
                    String.format("Programming Error: request-class %s has mapped Format parameter to type %s, rather than String"
                            , request.getClass().getCanonicalName(), val.getClass().getName()));
        }
    }


}

