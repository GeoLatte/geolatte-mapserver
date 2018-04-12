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

import net.opengis.wms.v_1_1_1.*;
import net.opengis.wms.v_1_1_1.Exception;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.OutputStream;

/**
 * @author Karel Maesen, Geovise BVBA
 * creation-date: Jul 22, 2010
 */
public class JAXB {

    private static JAXB instance = new JAXB();

    private JAXBContext ctxt;
    private ObjectFactory objectFactory;

    private JAXB() {
        try {
            ctxt = JAXBContext.newInstance("net.opengis.wms.v_1_1_1");
            objectFactory = new ObjectFactory();
        } catch (JAXBException e) {
            throw new IllegalStateException("Can't instantiate JAXB static factory", e);
        }
    }

    public static JAXB instance() {
        return instance;
    }

    public void marshal(Object element, OutputStream outputStream) {
        Marshaller marshaller = null;
        try {
            //JAXB specification doesn't specify Marshaller thread-safety, so we create a new
            //object for each request.                    
            marshaller = ctxt.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(element, outputStream);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    public OnlineResource createOnlineResource() {
        return objectFactory.createOnlineResource();
    }

    public DataURL createDataURL() {
        return objectFactory.createDataURL();
    }

    public UserDefinedSymbolization createUserDefinedSymbolization() {
        return objectFactory.createUserDefinedSymbolization();
    }

    public Service createService() {
        return objectFactory.createService();
    }

    public DescribeLayer createDescribeLayer() {
        return objectFactory.createDescribeLayer();
    }

    public Post createPost() {
        return objectFactory.createPost();
    }

    public GetLegendGraphic createGetLegendGraphic() {
        return objectFactory.createGetLegendGraphic();
    }

    public StyleURL createStyleURL() {
        return objectFactory.createStyleURL();
    }

    public Get createGet() {
        return objectFactory.createGet();
    }

    public Identifier createIdentifier() {
        return objectFactory.createIdentifier();
    }

    public ServiceExceptionReport createServiceExceptionReport() {
        return objectFactory.createServiceExceptionReport();
    }

    public AuthorityURL createAuthorityURL() {
        return objectFactory.createAuthorityURL();
    }

    public PutStyles createPutStyles() {
        return objectFactory.createPutStyles();
    }

    public WMSDescribeLayerResponse createWMSDescribeLayerResponse() {
        return objectFactory.createWMSDescribeLayerResponse();
    }

    public HTTP createHTTP() {
        return objectFactory.createHTTP();
    }

    public LatLonBoundingBox createLatLonBoundingBox() {
        return objectFactory.createLatLonBoundingBox();
    }

    public WMTMSCapabilities createWMTMSCapabilities() {
        return objectFactory.createWMTMSCapabilities();
    }

    public Keyword createKeyword() {
        return objectFactory.createKeyword();
    }

    public Format createFormat() {
        return objectFactory.createFormat();
    }

    public ContactInformation createContactInformation() {
        return objectFactory.createContactInformation();
    }

    public GetCapabilities createGetCapabilities() {
        return objectFactory.createGetCapabilities();
    }

    public GetMap createGetMap() {
        return objectFactory.createGetMap();
    }

    public Dimension createDimension() {
        return objectFactory.createDimension();
    }

    public BoundingBox createBoundingBox() {
        return objectFactory.createBoundingBox();
    }

    public ScaleHint createScaleHint() {
        return objectFactory.createScaleHint();
    }

    public ContactAddress createContactAddress() {
        return objectFactory.createContactAddress();
    }

    public Query createQuery() {
        return objectFactory.createQuery();
    }

    public Capability createCapability() {
        return objectFactory.createCapability();
    }

    public Style createStyle() {
        return objectFactory.createStyle();
    }

    public Layer createLayer() {
        return objectFactory.createLayer();
    }

    public Exception createException() {
        return objectFactory.createException();
    }

    public DCPType createDCPType() {
        return objectFactory.createDCPType();
    }

    public LayerDescription createLayerDescription() {
        return objectFactory.createLayerDescription();
    }

    public Extent createExtent() {
        return objectFactory.createExtent();
    }

    public Request createRequest() {
        return objectFactory.createRequest();
    }

    public GetFeatureInfo createGetFeatureInfo() {
        return objectFactory.createGetFeatureInfo();
    }

    public LogoURL createLogoURL() {
        return objectFactory.createLogoURL();
    }

    public MetadataURL createMetadataURL() {
        return objectFactory.createMetadataURL();
    }

    public VendorSpecificCapabilities createVendorSpecificCapabilities() {
        return objectFactory.createVendorSpecificCapabilities();
    }

    public Attribution createAttribution() {
        return objectFactory.createAttribution();
    }

    public KeywordList createKeywordList() {
        return objectFactory.createKeywordList();
    }

    public ServiceException createServiceException() {
        return objectFactory.createServiceException();
    }

    public LegendURL createLegendURL() {
        return objectFactory.createLegendURL();
    }

    public GetStyles createGetStyles() {
        return objectFactory.createGetStyles();
    }

    public FeatureListURL createFeatureListURL() {
        return objectFactory.createFeatureListURL();
    }

    public StyleSheetURL createStyleSheetURL() {
        return objectFactory.createStyleSheetURL();
    }

    public SRS createSRS() {
        return objectFactory.createSRS();
    }

    public ContactPersonPrimary createContactPersonPrimary() {
        return objectFactory.createContactPersonPrimary();
    }

}
