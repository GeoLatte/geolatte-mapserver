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

import net.opengis.wms.v_1_1_1.*;
import net.opengis.wms.v_1_1_1.Exception;
import org.geolatte.geom.Envelope;
import org.geolatte.geom.crs.CrsId;
import org.geolatte.mapserver.config.Configuration;
import org.geolatte.mapserver.coordinatetransforms.Referencing;
import org.geolatte.mapserver.tms.TileMap;
import org.geolatte.mapserver.tms.TileMapRegistry;

import java.io.IOException;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class WMSGetCapabilitiesResponse {

    private final WMTMSCapabilities responseTemplate;

    private WMSGetCapabilitiesResponse(WMTMSCapabilities responseTemplate) {
        this.responseTemplate = responseTemplate;
    }

    //TODO -- automate mapping between Configuration and WMSGetCapabilities.xml elements
    public static WMSGetCapabilitiesResponse build(Configuration config, TileMapRegistry registry) {

        WMTMSCapabilities capabilities = JAXB.instance().createWMTMSCapabilities();
        addServiceName(capabilities);
        addServiceTitle(capabilities, config.getWMSServiceTitle());
        addServiceAbstract(capabilities, config.getWMSServiceAbstract());
        addServiceKeywords(capabilities, config.getWMSServiceKeywords());
        addServiceOnlineResource(capabilities, config.getWMSServiceOnlineResource());
        addLayers(capabilities, registry, config.getWMSServiceTitle());
        return new WMSGetCapabilitiesResponse(capabilities);
    }

    private static void addLayers(WMTMSCapabilities capabilities, TileMapRegistry registry, String title) {
        Capability capability = JAXB.instance().createCapability();
        Layer layerRoot = JAXB.instance().createLayer();
        //Title is always required
        layerRoot.setTitle(title);
        for (String tileMapName : registry.getTileMapNames()) {
            TileMap tileMap = registry.getTileMap(tileMapName);
            List<CrsId> supportedSRS = registry.getSupportedSRS(tileMapName);
            addLayer(layerRoot, tileMap, supportedSRS);
        }
        capability.setLayer(layerRoot);
        capabilities.setCapability(capability);
    }

    private static void addLayer(Layer root, TileMap tileMap, List<CrsId> supportedSRS) {
        Layer layer = JAXB.instance().createLayer();
        //Currently title and name are the same because
        //TMS only specifies a title
        layer.setTitle(tileMap.getTitle());
        layer.setName(tileMap.getTitle());
        addLayerSRS(layer, tileMap.getSRS().toString());
        for (CrsId srs : supportedSRS) {
            addLayerSRS(layer, srs.toString());
        }
        Envelope bbox = tileMap.getBoundingBox();
        addLatLonBoundingBox(tileMap.getSRS(), layer, bbox);
        addBoundingBox(tileMap.getSRS(), layer, bbox);
        root.getLayer().add(layer);
    }

    private static void addLayerSRS(Layer layer, String srsStr) {
        SRS srs = JAXB.instance().createSRS();
        srs.setvalue(srsStr);
        layer.getSRS().add(srs);
    }

    private static void addLatLonBoundingBox(CrsId srs, Layer layer, Envelope bbox) {
        Envelope llbox = Referencing.transformToLatLong(bbox, srs);
        LatLonBoundingBox latLongBoundingBox = JAXB.instance().createLatLonBoundingBox();
        latLongBoundingBox.setMinx(toString(llbox.getMinX()));
        latLongBoundingBox.setMaxx(toString(llbox.getMaxX()));
        latLongBoundingBox.setMiny(toString(llbox.getMinY()));
        latLongBoundingBox.setMaxy(toString(llbox.getMaxY()));
        layer.setLatLonBoundingBox(latLongBoundingBox);
    }

    private static void addBoundingBox(CrsId srs, Layer layer, Envelope bbox) {
        BoundingBox boundingBox = JAXB.instance().createBoundingBox();
        boundingBox.setSRS(srs.toString());
        boundingBox.setMaxx(toString(bbox.getMaxX()));
        boundingBox.setMiny(toString(bbox.getMinY()));
        boundingBox.setMaxy(toString(bbox.getMaxY()));
        boundingBox.setMinx(toString(bbox.getMinX()));
        layer.getBoundingBox().add(boundingBox);
    }

    private static void addServiceTitle(WMTMSCapabilities capabilities, String title) {
        capabilities.getService().setTitle(title);
    }

    private static void addServiceAbstract(WMTMSCapabilities capabilities, String abstractText) {
        capabilities.getService().setAbstract(abstractText);
    }

    private static void addServiceName(WMTMSCapabilities capabilities) {
        Service service = JAXB.instance().createService();
        service.setName("OGC:WMS");
        capabilities.setService(service);
    }

    private static void addServiceKeywords(WMTMSCapabilities capabilities, String[] keywords) {
        if (keywords.length == 0) {
            return; //omit keyword list, since it is optional
        }
        KeywordList keywordList = JAXB.instance().createKeywordList();
        List<Keyword> keywordNodes = keywordList.getKeyword();
        for (String value : keywords) {
            Keyword keyword = JAXB.instance().createKeyword();
            keyword.setvalue(value);
            keywordNodes.add(keyword);
        }
        capabilities.getService().setKeywordList(keywordList);
    }

    private static void addServiceOnlineResource(WMTMSCapabilities capabilities, String wmsServiceOnlineResource) {
        OnlineResource onlineResource = createOnlineResourceElement(wmsServiceOnlineResource);
        capabilities.getService().setOnlineResource(onlineResource);
    }

    private static OnlineResource createOnlineResourceElement(String href) {
        OnlineResource onlineResource = JAXB.instance().createOnlineResource();
        onlineResource.setXmlnsXlink("http://www.w3.org/1999/xlink");
        onlineResource.setXlinkType("simple");
        onlineResource.setXlinkHref(href);
        return onlineResource;
    }

    private static String toString(double value) {
        NumberFormat nf = NumberFormat.getInstance(Locale.US);
        nf.setGroupingUsed(false);
        return nf.format(value);
    }

    public void write(OutputStream os, String requestURL) throws IOException {
        WMTMSCapabilities capabilitiesCopy = createCopyFromTemplateForResource(requestURL);
        JAXB.instance().marshal(capabilitiesCopy, os);
    }

    private WMTMSCapabilities createCopyFromTemplateForResource(String requestURL) {
        OnlineResource resource = createOnlineResourceElement(requestURL);
        WMTMSCapabilities copy = JAXB.instance().createWMTMSCapabilities();
        copy.setService(this.responseTemplate.getService());
        addCapabilities(copy, resource);
        copy.getCapability().setLayer(this.responseTemplate.getCapability().getLayer());
        return copy;
    }

    private void addCapabilities(WMTMSCapabilities capabilities, OnlineResource resource) {
        Capability capability = JAXB.instance().createCapability();
        addRequest(capability, resource);
        addException(capability);
        capabilities.setCapability(capability);
    }

    private void addException(Capability capability) {
        Exception exceptionEl = JAXB.instance().createException();
        addFormats(exceptionEl.getFormat(), WMSCapabilities.getSupportedExceptionFormat(WMSCapabilities.WMS));
        capability.setException(exceptionEl);
    }

    private void addRequest(Capability capability, OnlineResource resource) {
        Request request = JAXB.instance().createRequest();
        //TODO -- this should be responsive to WMSCapabilities.getSupportedRequests("WMS")
        addGetCapabilities(request, resource);
        addGetMap(request, resource);
        capability.setRequest(request);
    }

    private void addGetCapabilities(Request request, OnlineResource resource) {
        GetCapabilities getCapabilities = JAXB.instance().createGetCapabilities();
        addFormats(getCapabilities.getFormat(), WMSCapabilities.getSupportedFormat(WMSCapabilities.WMS, WMSCapabilities.WMS_GETCAPABILITIES_REQUEST));
        addHTTPGetOrPostDCPType(getCapabilities.getDCPType(), resource, true);
        request.setGetCapabilities(getCapabilities);
    }

    private void addGetMap(Request request, OnlineResource resource) {
        GetMap getMap = JAXB.instance().createGetMap();
        addFormats(getMap.getFormat(), WMSCapabilities.getSupportedFormat(WMSCapabilities.WMS, WMSCapabilities.WMS_GETMAP_REQUEST));
        addHTTPGetOrPostDCPType(getMap.getDCPType(), resource, false);
        request.setGetMap(getMap);
    }

    private void addFormats(List list, String[] formatValues) {
        for (String formatValue : formatValues) {
            Format format = JAXB.instance().createFormat();
            format.setvalue(formatValue);
            list.add(format);
        }
    }

    private void addHTTPGetOrPostDCPType(List list, OnlineResource resource, boolean includePost) {
        DCPType dcptype = JAXB.instance().createDCPType();
        HTTP http = JAXB.instance().createHTTP();
        Get get = JAXB.instance().createGet();
        get.setOnlineResource(resource);
        http.getGetOrPost().add(get);
        if (includePost) {
            Post post = JAXB.instance().createPost();
            post.setOnlineResource(resource);
            http.getGetOrPost().add(post);
        }
        dcptype.setHTTP(http);
        list.add(dcptype);
    }
}
