package com.ankerl.math;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Adds up numbers in an array with perfect precision, and in O(n).
 * 
 * @see http://code.activestate.com/recipes/393090/
 */
public class Summarizer {

    /**
     * Perfectly sums up numbers, without rounding errors (if at all possible).
     * 
     * @param values
     *            The values to sum up.
     * @return The sum.
     */
    public static double msum(double... values) {
        List<Double> partials = new ArrayList<Double>();
        for (double x : values) {
            int i = 0;
            for (double y : partials) {
                if (Math.abs(x) < Math.abs(y)) {
                    double tmp = x;
                    x = y;
                    y = tmp;
                }
                double hi = x + y;
                double lo = y - (hi - x);
                if (lo != 0.0) {
                    partials.set(i, lo);
                    ++i;
                }
                x = hi;
            }
            if (i < partials.size()) {
                partials.set(i, x);
                partials.subList(i + 1, partials.size()).clear();
            } else {
                partials.add(x);
            }
        }
        return sum(partials);
    }

    /**
     * Sums up the rest of the partial numbers which cannot be summed up without
     * loss of precision.
     */
    public static double sum(Collection<Double> values) {
        double s = 0.0;
        for (Double d : values) {
            s += d;
        }
        return s;
    }
}
