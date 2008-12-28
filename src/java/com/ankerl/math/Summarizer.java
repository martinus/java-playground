package com.ankerl.math;

import java.util.Arrays;

/**
 * Adds up numbers in an array with perfect precision, and in O(n).
 * 
 * @see http://code.activestate.com/recipes/393090/
 */
public class Summarizer {

    private double[] mPartials;

    private int mUsedPartialSize;

    /**
     * Creates the perfect summarizer.
     */
    public Summarizer() {
        mPartials = new double[16];
        mUsedPartialSize = 0;
    }

    /**
     * Perfectly sums up numbers, without rounding errors (if at all possible).
     * 
     * @param values
     *            The values to sum up.
     * @return The sum.
     */
    public void add(double... values) {
        for (double x : values) {
            int i = 0;
            for (int p = 0; p < mUsedPartialSize; ++p) {
                double y = mPartials[p];
                if (Math.abs(x) < Math.abs(y)) {
                    double tmp = x;
                    x = y;
                    y = tmp;
                }
                double hi = x + y;
                double lo = y - (hi - x);
                if (lo != 0.0) {
                    mPartials[i] = lo;
                    ++i;
                }
                x = hi;
            }
            if (i >= mPartials.length) {
                mPartials = Arrays.copyOf(mPartials, mPartials.length * 2);
            }
            mPartials[i] = x;
            mUsedPartialSize = i + 1;
        }
    }

    /**
     * Returns the most precise sum possible. This result might contain rounding
     * errors, but it will always be as exact as possible.
     */
    public double sum() {
        double s = 0.0;
        for (int p = 0; p < mUsedPartialSize; ++p) {
            s += mPartials[p];
        }
        return s;
    }
}
