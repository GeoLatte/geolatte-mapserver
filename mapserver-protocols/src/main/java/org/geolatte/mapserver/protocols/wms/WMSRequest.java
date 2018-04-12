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

import org.geolatte.geom.Envelope;
import org.geolatte.geom.crs.CrsId;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public abstract class WMSRequest {

    static final Map<WMSParam, Field> paramFieldMap = new HashMap<WMSParam, Field>();

    private String requestURL;

    public static WMSRequest adapt(HttpServletRequest request) throws InvalidWMSRequestException {

        WMSRequest adapted = makeWMSRequest(request);
        Enumeration parameterNames = request.getParameterNames();
        for (; parameterNames.hasMoreElements(); ) {
            String pname = (String) parameterNames.nextElement();
            WMSParam param = matchRequestParameter(pname);
            adapted.set(param, request.getParameter(pname));
        }
        adapted.setRequestURL(request.getRequestURL().toString());
        adapted.verify();
        return adapted;
    }

    protected static WMSRequest makeWMSRequest(HttpServletRequest request) {
        String requestParamValue = getRequestParamValue(request);

        //Note that this is more relaxed than the specification,
        //which states that request parameter values shall be
        //case sensitive (cfr. p. 13)
        if (WMSCapabilities.WMS_GETMAP_REQUEST.equalsIgnoreCase(requestParamValue)) {
            return new WMSGetMapRequest();
        }

        if (WMSCapabilities.WMS_GETCAPABILITIES_REQUEST.equalsIgnoreCase(requestParamValue) ||
                "capabilities".equalsIgnoreCase(requestParamValue)) {
            return new WMSGetCapabilitiesRequest();
        }
        throw new IllegalArgumentException("Can't find the request parameter, or request not supported."); //OK, so which?
    }

    private static String getRequestParamValue(HttpServletRequest request) {

        for (Enumeration params = request.getParameterNames(); params.hasMoreElements(); ) {
            String pname = (String) params.nextElement();
            if (pname.equalsIgnoreCase("request")) {
                return request.getParameter(pname);
            }
        }
        return "";
    }

    /**
     * Maps the request parameter to a <code>WMSParam</code>
     *
     * @param pName HTTP Request parameter
     * @return the corresponding <code>WMSParam</code> instance
     */
    protected static WMSParam matchRequestParameter(String pName) {
        for (WMSParam param : WMSParam.values()) {
            for (String candidateName : param.getNames()) {
                if (pName.equalsIgnoreCase(candidateName.toString())) {
                    return param;
                }
            }
        }
        return null;
    }

    private static Envelope convertToEnvelope(String strVal) throws InvalidWMSRequestException {
        Scanner scanner = new Scanner(strVal);
        scanner.useDelimiter(",");
        double[] xyvals = new double[4];
        int i = 0;
        try {
            while (scanner.hasNext()) {
                xyvals[i++] = Double.valueOf(scanner.next());
            }
        } catch (NumberFormatException e) {
            throw new InvalidWMSRequestException(String.format("Invalid Boundingbox: %s", strVal));
        } catch (IndexOutOfBoundsException e) {
            throw new InvalidWMSRequestException(String.format("Invalid Boundingbox: %s", strVal));
        }
        Envelope result = new Envelope(xyvals[0], xyvals[1], xyvals[2], xyvals[3], null);
        if (result.isEmpty()) {
            throw new InvalidWMSRequestException("Empty or invalid bounding box.");
        }
        return result;
    }

    private static Integer convertToInteger(String strVal) {
        return Integer.valueOf(strVal);
    }

    private static CrsId convertToCrsId(String strVal) throws InvalidWMSRequestException {
        try {
            CrsId crsId = CrsId.parse(strVal);
            return crsId;
        } catch (IllegalArgumentException e) {
            throw new InvalidWMSRequestException(String.format("Can't interpret specified SRS: %s", strVal), e);
        }
    }

    public abstract String getResponseContentType();

    public Object get(WMSParam param) {
        try {
            Field f = getFieldForParameter(param);
            if (f == null) return null;
            f.setAccessible(true);
            return f.get(this);
        } catch (SecurityException e) {
            throw new RuntimeException("Programming Error", e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Programming Error", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Programming Error", e);
        }
    }

    public void set(WMSParam param, String value) throws InvalidWMSRequestException {
        if (param == null) return;
        try {
            Field field = getFieldForParameter(param);
            if (field == null) return;
            Object convertedValue = convertToFieldType(value, field);
            field.setAccessible(true);
            field.set(this, convertedValue);
        } catch (InvocationTargetException e) {
            throw new InvalidWMSRequestException(String.format("Can't set parameter %s to value %s", param, value), e.getCause());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Programming Error", e); //this shouldn't happen.
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Programming Error", e); //this shouldn't happen.
        }
    }

    public String getRequestURL() {
        return this.requestURL;
    }

    public void setRequestURL(String url) {
        this.requestURL = url;
    }

    private Field getFieldForParameter(WMSParam param) {
        assert param != null : "Null paramater is not allowed!";
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field f : fields) {
            WMSParameter paramAnnotation = getWMSParamAnnotation(f);
            if (paramAnnotation != null &&
                    param.equals(paramAnnotation.param())) return f;
        }
        return null;
    }

    private WMSParameter getWMSParamAnnotation(Field field) {
        if (field == null) return null;
        Annotation annotation = field.getAnnotation(WMSParameter.class);
        if (annotation != null) {
            return (WMSParameter) annotation;
        }
        return null;
    }

    protected Object convertToFieldType(String value, Field f) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Object convertedValue = null;

        Class<?> type = f.getType();
        if (type.isArray()) {
            Class<?> baseType = type.getComponentType();
            String[] components = value.split(",");
            convertedValue = Array.newInstance(baseType, components.length);
            int i = 0;
            for (String component : components) {
                ((Object[]) convertedValue)[i++] = convertToType(component, baseType);
            }
        } else {
            convertedValue = convertToType(value, type);
        }

        return convertedValue;
    }

    private Object convertToType(String value, Class<?> type) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if (type == String.class) return value;
        Method m = WMSRequest.class.getDeclaredMethod("convertTo" + type.getSimpleName(), String.class);
        m.setAccessible(true);
        return m.invoke(this, value);
    }

    public void verify() throws InvalidWMSRequestException {
        for (Field f : getClass().getDeclaredFields()) {
            WMSParameter pa = getWMSParamAnnotation(f);
            if (pa != null && pa.required()) {
                Object value = get(pa.param());
                if (value == null ||
                        value.toString().isEmpty()) {
                    throw new InvalidWMSRequestException("Required parameter: " + pa.param().toString() + " not found in GetMap request");
                }
            }

        }
    }

}
