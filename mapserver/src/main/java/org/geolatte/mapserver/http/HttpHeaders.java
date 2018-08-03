package org.geolatte.mapserver.http;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Represents the headers fo an Http Request
 *
 * <p>We provide this interface to allow integration with as much Http Libraries as possible. This can later be
 * replaced by the JDK standard HttpClient interfaces (coming in JDK11).
 * </p>
 *
 * <p>implementation note: the interface is modeled on the incubating Http client package in JDK10. </p>
 *
 * Created by Karel Maesen, Geovise BVBA on 04/07/2018.
 */
public interface HttpHeaders {

    List<String> allValues(String name);

    Optional<String> firstValue(String name);

    Map<String, List<String>> map();

}
