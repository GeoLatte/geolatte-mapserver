package org.geolatte.mapserver.util;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

/**
 * Created by Karel Maesen, Geovise BVBA on 05/07/2018.
 */
public class TestCaseInsensitiveMultiMap {

    CaseInsensitiveMultiMap ciMap;

    @Before
    public void setUp(){
        ciMap = new CaseInsensitiveMultiMap();
    }


    @Test
    public void testSetValuesOnEmptyMap(){
        ciMap.put("k", "a", "b", "c");
        assertThat(ciMap.allValues("k"), hasItems("a", "b", "c"));
    }

    @Test
    public void testDeepCopyCreatesIndependentMaps(){
        ciMap.put("k", "a", "b", "c");
        CaseInsensitiveMultiMap clone = ciMap.deepCopy();
        ciMap.put("k", "d");
        assertThat(ciMap.allValues("k"), hasItems("a", "b", "c", "d"));
        assertThat(clone.allValues("K"), hasItems("a", "b", "c"));
        assertThat(clone.allValues("K"), not(hasItem("d")));

    }

}
