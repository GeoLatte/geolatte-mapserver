package org.geolatte.mapserver.image;

/**
 * Created by Karel Maesen, Geovise BVBA on 12/04/2018.
 */
public enum ImageFormat {


    JPEG("image/jpeg", "jpg"),
    PNG("image/png", "png");

    private String mimeType;
    private String ext;

    ImageFormat(String mimeType, String ext) {
        this.mimeType = mimeType;
        this.ext = ext;
    }

    public String getMimeType(){
        return mimeType;
    }

    public String getExt() {
        return ext;
    }

    /**
     * Returns the <code>ImageFormat</code> associated with the
     * specified MIME-type.
     *
     * @param mime MIME-type of the <code>ImageFormat</code> as string
     * @return the <code>ImageFormat</code> having the MIME-type specified by the <code>String</code> argument.
     */
    public static ImageFormat forMimeType(String mime) {
        for (ImageFormat f : values()) {
            if (f.getMimeType().equalsIgnoreCase(mime)) return f;
        }
        throw new IllegalArgumentException(String.format("Can't map %s to MIME type of a known image format", mime));
    }
}
