package org.geolatte.mapserver.render;

import java.util.TreeMap;

/**
 * Determines by which factor to scale the BBOX query, depending on the resolution.
 *
 * Created by Karel Maesen, Geovise BVBA on 27/08/2018.
 */
public class BboxFactors {

    private final static double STANDARD_FACTOR = 3;

    final private double defaultFactor;
    final private TreeMap<Double,Double> factors = new TreeMap<>();

    /**
     * Constructs an instance
     * @param defaultFactor the default BBox scaling factor
     */
    public BboxFactors(double defaultFactor) {
        this.defaultFactor = defaultFactor;
    }

    public BboxFactors(){
        this(STANDARD_FACTOR);
    }

    /**
     * Constructs a UnitsPerPixel value
     * @param value the value of upp
     * @return
     */
    static public Upp upp(Double value){
        return new Upp(value);
    }

    /**
     * Constructs a scale factor
     * @param value the scale factor value
     * @return
     */
    static public Factor factor(Double value) {
        return new Factor(value);
    }

    /**
     *  Adds a break so that from the specified upp to the next highest upp break the specified factor will be used.
     * @param upp the Upp above which this scale factor will be use
     * @param factor
     */
    public void put(Upp upp, Factor factor) {
        factors.put(upp.value, factor.value);
    }

    /**
     * Returns the BBox scaling factor corresponding to the specified upp
     * @param upp the resolution
     * @return
     */
    public Double getFactor(Upp upp) {
        return factors.ceilingKey(upp.value) != null ? factors.ceilingEntry(upp.value).getValue() : defaultFactor;
    }


    static class Upp {
        final Double value;

        public Upp(Double upp){
            value = upp;
        }
    }

    static class Factor {
        final Double value;

        public Factor(Double factor){
            value = factor;
        }
    }

}

