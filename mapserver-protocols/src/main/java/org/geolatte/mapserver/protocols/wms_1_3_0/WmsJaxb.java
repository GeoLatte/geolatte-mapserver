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

import net.opengis.wms.v_1_3_0.*;
import org.geolatte.mapserver.image.ImageFormat;
import org.geolatte.mapserver.ServiceMetadata;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

/**
 * @author Karel Maesen, Geovise BVBA
 * creation-date: Jul 22, 2010
 */
public class WmsJaxb {

    private static WmsJaxb instance = new WmsJaxb();

    private JAXBContext ctxt;
    private ObjectFactory objectFactory;

    private WmsJaxb() {
        try {
            ctxt = JAXBContext.newInstance("net.opengis.wms.v_1_3_0");
            objectFactory = new ObjectFactory();
        } catch (JAXBException e) {
            throw new IllegalStateException("Can't instantiate JAXB static factory", e);
        }
    }

    public static WmsJaxb instance() {
        return instance;
    }

    public Object unmarshal(InputStream is) {
        Unmarshaller unm = null;
        try {
            unm = ctxt.createUnmarshaller();
            return unm.unmarshal(is);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }


    public void marshal(Object element, OutputStream outputStream) {
        Marshaller marshaller = null;
        try {
            //JAXB specification doesn't specify Marshaller thread-safety, so we create a new
            //object for each ows.
            marshaller = ctxt.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(element, outputStream);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    Marshaller createMarshaller() {
        try {
            Marshaller marshaller = ctxt.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            return marshaller;
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }


    public ServiceExceptionReport createServiceExceptionReport(WmsServiceExceptionList exceptions){
        ServiceExceptionReport serviceExceptionReport = objectFactory.createServiceExceptionReport();
        List<ServiceExceptionType> exceptionTypes = exceptions.items.stream().map(se -> {
            ServiceExceptionType st = objectFactory.createServiceExceptionType();
            st.setCode(se.getCode().name());
            st.setValue(se.getMessage());
            return st;
        }).collect(Collectors.toList());
        serviceExceptionReport.getServiceException().addAll(exceptionTypes);
        return serviceExceptionReport;
    }

    public WMSCapabilities createWMSCapabilities(ServiceMetadata serviceMetadata) {
        WMSCapabilities capabilities = objectFactory.createWMSCapabilities();
        capabilities.setService(buildService(serviceMetadata));
        capabilities.setCapability(buildCapability(serviceMetadata));
        return capabilities;
    }

    private Capability buildCapability(ServiceMetadata serviceMetadata) {
        Capability capability = objectFactory.createCapability();
        capability.setRequest(buildRequest(serviceMetadata));
        return capability;
    }

    private Request buildRequest(ServiceMetadata serviceMetadata) {
        Request request = objectFactory.createRequest();
        List<ServiceMetadata.Operation> operations = serviceMetadata.getOperations();
        operations.forEach(op -> addOperation(op, request));
        return request;
    }

    private void addOperation(ServiceMetadata.Operation operation, Request request) {
        switch (operation.getName()) {
            case ServiceMetadata.GET_CAPABILITIES_OP:
                request.setGetCapabilities(buildOperation(operation.getGetURL()));
                break;
            case ServiceMetadata.GET_MAP_OP:
                OperationType operationType = buildOperation(operation.getGetURL());
                ServiceMetadata.GetMapOperation gmo = (ServiceMetadata.GetMapOperation)operation;
                List<String> formats = gmo.getSupportedFormats()
                        .stream()
                        .map(ImageFormat::getMimeType)
                        .collect(Collectors.toList());
                operationType.setFormat(formats);
                request.setGetMap(operationType);
                break;
            default:
                throw new UnsupportedOperationException(
                        format("Operation %s not supported in Capabilieties document", operation.getName()))
                        ;
        }
    }


    private OperationType buildOperation(String url) {
        OperationType type = objectFactory.createOperationType();
        DCPType dcpType = objectFactory.createDCPType();
        OnlineResource olr = objectFactory.createOnlineResource();
        olr.setHref(url);
        HTTP http = objectFactory.createHTTP();
        Get get = objectFactory.createGet();
        get.setOnlineResource(olr);
        http.setGet(get);
        dcpType.setHTTP(http);
        type.setDCPType(Collections.singletonList(dcpType));
        return type;
    }


    // Build service

    private Service buildService(ServiceMetadata serviceMetadata) {
        Service service = objectFactory.createService();
        service.setName("WMS");
        service.setTitle(serviceMetadata.getServiceIdentification().getTitle());
        service.setAbstract(serviceMetadata.getServiceIdentification().getAbstractText());
        KeywordList keywords = objectFactory.createKeywordList();
        keywords.getKeyword().addAll(
                serviceMetadata.getServiceIdentification().getKeywords().stream()
                        .map(this::toKeyWord).collect(Collectors.toList())
        );
        service.setKeywordList(keywords);
        service.setOnlineResource(buildOnlineResource(serviceMetadata.getOnlineResource()));
        return service;
    }

    private OnlineResource buildOnlineResource(String url) {
        OnlineResource onlineResource = objectFactory.createOnlineResource();
        onlineResource.setHref(url);
        return onlineResource;
    }

    private Keyword toKeyWord(String value) {
        Keyword kw = objectFactory.createKeyword();
        kw.setValue(value);
        return kw;
    }

    // end of build service


}
