package org.geolatte.mapserver.transform;


/**
 * @author Karel Maesen, Geovise BVBA
 * creation-date: 7/1/11
 */
public class ReferencingException extends RuntimeException {

    public ReferencingException(String msg) {
        super(msg);
    }

    public ReferencingException(Exception e) {
        super(e);
    }

    public ReferencingException(String msg, Exception e) {
        super(msg, e);
    }

}
