package org.geolatte.mapserver.rxhttp;

import java.util.Arrays;

class ChunkSplitter {

    private final String separator;
    
    private String partialJson = "";

    public ChunkSplitter() {
        this("\n");
    }

    public ChunkSplitter(String separator) {
        this.separator = separator;
    }

    public Iterable<String> split(String chunk) {
        String chunkWithPartial = partialJson + chunk;

        String[] parts = chunkWithPartial.split(separator);

        if (chunk.endsWith(separator)) {
            // every part in the chunk is a complete json
            partialJson = "";
            return Arrays.asList(parts);
        } else {
            // the last part is not a complete json, we keep it for the next chunk
            partialJson = parts[parts.length - 1];
            // all other parts are complete jsons
            return Arrays.asList(Arrays.copyOfRange(parts, 0, parts.length - 1));
        }
    }
}
