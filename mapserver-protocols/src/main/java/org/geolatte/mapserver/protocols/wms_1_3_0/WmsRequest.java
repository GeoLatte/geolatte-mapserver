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

import org.geolatte.geom.C2D;
import org.geolatte.geom.Envelope;
import org.geolatte.geom.crs.CrsId;
import org.geolatte.mapserver.core.ServiceMetadata;

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

public abstract class WmsRequest {

    static final Map<WmsParam, Field> paramFieldMap = new HashMap<WmsParam, Field>();

    private String requestURL;

    public static WmsRequest adapt(HttpServletRequest request) throws InvalidWmsRequestException {
        WmsRequest adapted = makeWMSRequest(request);
        Enumeration parameterNames = request.getParameterNames();
        for (; parameterNames.hasMoreElements(); ) {
            String pname = (String) parameterNames.nextElement();
            WmsParam param = matchRequestParameter(pname);
            adapted.set(param, request.getParameter(pname));
        }
        adapted.setRequestURL(request.getRequestURL().toString());
        adapted.verify();
        return adapted;
    }

    protected static WmsRequest makeWMSRequest(HttpServletRequest request) {
        String requestParamValue = getRequestParamValue(request);

        //Note that this is more relaxed than the specification,
        //which states that request parameter values shall be
        //case sensitive (cfr. p. 13)
        if (ServiceMetadata.GET_MAP_OP.equalsIgnoreCase(requestParamValue)) {
            return new WmsGetMapRequest();
        }

        if (ServiceMetadata.GET_CAPABILITIES_OP.equalsIgnoreCase(requestParamValue) ||
                "capabilities".equalsIgnoreCase(requestParamValue)) {
            return new WmsGetCapabilitiesRequest();
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
    protected static WmsParam matchRequestParameter(String pName) {
        for (WmsParam param : WmsParam.values()) {
            for (String candidateName : param.getNames()) {
                if (pName.equalsIgnoreCase(candidateName.toString())) {
                    return param;
                }
            }
        }
        return null;
    }

    private static WmsBbox convertToWmsBbox(String strVal) throws InvalidWmsRequestException {
        try(Scanner scanner = new Scanner(strVal);) {
            scanner.useDelimiter(",");
            double[] xyvals = new double[4];
            int i = 0;
            while (scanner.hasNext()) {
                xyvals[i++] = Double.valueOf(scanner.next());
            }
            return new WmsBbox(xyvals[0], xyvals[1], xyvals[2], xyvals[3]);
        } catch (NumberFormatException e) {
            throw new InvalidWmsRequestException(String.format("Invalid Boundingbox: %s", strVal));
        }
    }

    private static Integer convertToInteger(String strVal) {
        return Integer.valueOf(strVal);
    }

//    private static CrsId convertToCrsId(String strVal) throws InvalidWmsRequestException {
//        try {
//            CrsId crsId = CrsId.parse(strVal);
//            return crsId;
//        } catch (IllegalArgumentException e) {
//            throw new InvalidWmsRequestException(String.format("Can't interpret specified SRS: %s", strVal), e);
//        }
//    }

    public abstract String getResponseContentType();

    public Object get(WmsParam param) {
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

    public void set(WmsParam param, String value) throws InvalidWmsRequestException {
        if (param == null) return;
        try {
            Field field = getFieldForParameter(param);
            if (field == null) return;
            Object convertedValue = convertToFieldType(value, field);
            field.setAccessible(true);
            field.set(this, convertedValue);
        } catch (InvocationTargetException e) {
            throw new InvalidWmsRequestException(String.format("Can't set parameter %s to value %s", param, value), e.getCause());
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

    private Field getFieldForParameter(WmsParam param) {
        assert param != null : "Null paramater is not allowed!";
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field f : fields) {
            WmsParameter paramAnnotation = getWMSParamAnnotation(f);
            if (paramAnnotation != null &&
                    param.equals(paramAnnotation.param())) return f;
        }
        return null;
    }

    private WmsParameter getWMSParamAnnotation(Field field) {
        if (field == null) return null;
        Annotation annotation = field.getAnnotation(WmsParameter.class);
        if (annotation != null) {
            return (WmsParameter) annotation;
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
        Method m = WmsRequest.class.getDeclaredMethod("convertTo" + type.getSimpleName(), String.class);
        m.setAccessible(true);
        return m.invoke(this, value);
    }

    public void verify() throws InvalidWmsRequestException {
        for (Field f : getClass().getDeclaredFields()) {
            WmsParameter pa = getWMSParamAnnotation(f);
            if (pa != null && pa.required()) {
                Object value = get(pa.param());
                if (value == null ||
                        value.toString().isEmpty()) {
                    throw new InvalidWmsRequestException("Required parameter: " + pa.param().toString() + " not found in GetMap request");
                }
            }

        }
    }

}
