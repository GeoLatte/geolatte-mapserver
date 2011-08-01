package org.geolatte.mapserver.referencing;

import org.opengis.referencing.operation.NoninvertibleTransformException;

/**
 *
 * @author Karel Maesen, Geovise BVBA
 *         creation-date: 7/1/11
 */
public class ReferencingException extends Exception {

    public ReferencingException(String msg){
        super(msg);
    }

    public ReferencingException(Exception e) {
        super(e);
    }

    public ReferencingException(String msg, Exception e) {
        super(msg,e);
    }

}
