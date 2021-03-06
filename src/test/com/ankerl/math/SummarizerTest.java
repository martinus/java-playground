package com.ankerl.math;

import java.util.Random;

import junit.framework.Assert;

import org.junit.Test;

import com.ankerl.math.Summarizer;

public class SummarizerTest {

    public double[] times(double[] origin, int x) {
        double[] v = new double[origin.length * x];
        for (int i = 0; i < x; ++i) {
            for (int j = 0; j < origin.length; ++j) {
                v[i * origin.length + j] = origin[j];
            }
        }
        return v;
    }

    @Test
    public void simple() {
        Summarizer s = new Summarizer();

        double[] vals = new double[] { 1, 1e100, 1, -1e100 };
        for (int i = 0; i < 10000; ++i) {
            s.add(vals);
        }
        Assert.assertEquals(20000.0, s.sum(), 0.0);
    }

    @Test
    public void sum() {
        // create array with random numbers (large and small)
        Random r = new Random();

        Summarizer s = new Summarizer();
        for (int times = 0; times < 1000; ++times) {
            double[] randVals = new double[500];
            for (int i = 0; i < randVals.length; i += 2) {
                double val;
                do {
                    val = Double.longBitsToDouble(r.nextLong());
                } while (Double.isNaN(val));
//                val = Math.pow(r.nextGaussian(), 100.0);
                randVals[i] = val;
                randVals[i + 1] = -randVals[i];
            }

            // shuffle
            for (int i = randVals.length; i > 1; --i) {
                int pos = r.nextInt(i);
                double tmp = randVals[i - 1];
                randVals[i - 1] = randVals[pos];
                randVals[pos] = tmp;
            }

            s.add(randVals);
        }

        Assert.assertEquals(0.0, s.sum(), 0.0);
    }

    @Test
    public void rand() {
        Random r = new Random(5);
        Summarizer s = new Summarizer();
        for (int i = 0; i < 10000; ++i) {
            double val;
            do {
                val = Double.longBitsToDouble(r.nextLong());
            } while (Double.isNaN(val));
            
            s.add(val);
        }
//        System.out.print("[");
//        for (double d : s.getPartialSums()) {
//            System.out.print(d + " ");
//        }
//        System.out.println("]");
//        System.out.println(s.sum());
    }
}
