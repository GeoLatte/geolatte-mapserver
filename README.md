# Geolatte Mapserver

Geolatte Mapserver is a mapserver-as-a-library that provides support for Web Mapping Server (WMS) operations. It makes
minimal assumptions with respect to its environment, and can be easily extended or adapted due to its modular architecture.

## Getting Started

TODO

## Architecture

Mapserver provides a small number of general abstractions that model how mapping requests
are turned into map image responses. The model is based on the [OGC WMS specifications](http://www.opengeospatial.org/standards/wms) and can best be explained by describing how Mapserver responds to a request for a 
map. 

 1. The `OwsHttpService` receives an WMS Http Request. (The "Ows" in the class name refers to the 
OGC Web services")

 2. The `OwsHttpService` uses its `ProtocolAdapter` to turn the protocol and version specific 
 request (WMS v1.3 or WMTS 1.0) into a protocol-independent `GetMapRequest`, and create a
 `RequestHandler` for it, which it invokes asynchronously.
 
 3. The `GetMapRequestHandler` will interpret the `GetMapRequest` and retrieve the map `Layer` 
 specified in the request from the `LayerRegistry`. It will ask the Layer for a map using the specified
 bounding box, styles, image dimensions  and other mapping parameters.
 
 4. the `Layer` creates the image based on these specifications. 
 
 5. The `OwsHttpService` turns this image into a `HttpResponse`.

## Layers

Currently Mapserver supports three types of `Layer`. The first, `TileMapLayer`, is backed by a pre-rendered 
[TileMap](https://wiki.osgeo.org/wiki/Tile_Map_Service_Specification). When handling map requests,
the best fitting tilemap level is selected and the requested image bbox is created by mosaicing, 
cropping and stretching the image as required.  

The second type is the `DynamicLayer` which will render the requested map directly using the 
[Geolatte MapRenderer](https://github.com/GeoLatte/geolatte-maprenderer) as the rendering backend, `FeatureSource` as a
 source of geographic objects (features), and a `Painter` that specifies how the feature should be rendered 
 on the rendering backend. 
 
 The third type, `RenderableTileMapLayer` combines the behavior of the two. It uses a `TileMap` like the first, but will 
 generate tiles dynamically using the same resources as the `DynamicLayer`. 

### Service Provider Interfaces

Mapserver requires a number of Services that are injected through the [SPI mechanism](https://docs.oracle.com/javase/8/docs/api/java/util/ServiceLoader.html).
The package `org.geolatte.mapserver.spi` contains the Provider interfaces for required
services. These are:

  - `ImagingProvider`: to provide an implementation of the `Imaging` interface, which models a set of
 image manipulation operations required for mapserver
 
  - `PainterFactoryProvider`: to provide factories to create Maprenderer `Painter`s to be used when
 rendering features from a `FeatureSource`. 
 
  - `ProtocolAdapterProvider`: to provide adapters that can translate protocol-specific requests into generalised 
 protocol-independent requests.
 
  - `LayerRegistryProvider`: to provide the `LayerRegistry` for the Mapserver
  
  - `FeatureSourceFactoryProvider`: to provide factories for `FeatureSource`s.
   
  - `ServiceMetadataProvider` provides general service metadata (required by the `GetCapabilitiesRequest`)
 
On system startup (boot) the classpath will be examined to find implementations for these
providers, and use them to register the provided services in the static `ServiceLocator` instance. 
The first implementation found will be used as `Provider`, except for the `FeatoureSourceFactoryProvider` 
and the `PainterFactoryProvider` where all provided services will be registered. 

This repository contains default implementations for most of these SPI interfaces. 

- [mapserver-imageops](/mapserver-imageops)  provides a `Imaging` implementation based on ImageIO and AWT Graphics.

- [mapserver-protocols](/mapserver-protocols) provides a `ProtocolAdapter` for the WMS v1.3.0 specification

- [mapserver-rxhttpfeaturesource](/mapserver-rxhttpfeaturesource) provides a `FeatureSourceFactory` using reactive HTTP against a 
[Geolatte Featureserver](https://github.com/geolatte/geolatte-featureserver)

- [mapserverconfig](/mapserverconfig) provides both `ServiceMetadata` and a `LayerRegistry` from configuration files
using [Lightbend's Config](https://github.com/lightbend/config)

Users of the library are required to provide their own `PainterFactory` implementations (although
later we plan to incorporate an [SLD](http://www.opengeospatial.org/standards/sld)-based implemntation
in this project)

## Integrating Mapserver into your application 

You need at least to add the [mapserver](/mapserver) artifact as a dependency, and then
packages with all the required service providers. Use the provided implementations, or roll your own.

Then create an adapter that turns the HTTP request (response) instances of your framework or 
 application into `org.geolatte.mapserver.http.HttpRequest (Response)` instances.
 
 
Finally plug in the  `OwsHttpService` instance into your server application. Alternatively, you could implement your
own `HttpService` or use the `RequestHandlerFactories` directly. 

For a example of how everything fits together, have a look at the [map-server](/map-server) integration
test classes.

