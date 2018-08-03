package org.geolatte.mapserver.http;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Created by Karel Maesen, Geovise BVBA on 04/07/2018.
 */
public interface HttpQueryParams {

    List<String> allValues(String name);

    Optional<String> firstValue(String name);

    Set<String> allParams();

}
