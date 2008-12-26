package com.ankerl.proxy;

import java.security.GeneralSecurityException;
import java.util.Iterator;
import java.util.Random;

import org.junit.Ignore;
import org.junit.Test;
import org.uncommons.maths.random.CellularAutomatonRNG;

/**
 * Calculates the deviation of values when used in a HashMap.
 */
public class HashFunctionTest {

    public static abstract class ValueForHashIterator<T> implements Iterator<T> {
        @Override
        public boolean hasNext() {
            return true;
        }

        @Override
        public void remove() {
            throw new IllegalArgumentException("not implemented");
        }

        public abstract void reset();
    }

    public static interface Hash<T> {
        int hash(T o);
    }

    /**
     * @param minBits
     * @param maxBits
     * @return
     */
    public static <T> double deviation(int minBits, int maxBits, int times, ValueForHashIterator<T> valueIt,
            Hash<T> hash) {
        double devSum = 0;
        for (int b = minBits; b <= maxBits; ++b) {
            valueIt.reset();
            // create numbers and fill them into the buckets
            int size = 1 << b;
            int[] buckets = new int[size];
            for (int i = 0; i < times; ++i) {
                int h = hash.hash(valueIt.next());
                ++buckets[h & (size - 1)];
            }

            // calculate deviation of bucket counts
            double avg = ((double) times) / size;
            double var = 0.0;
            for (int bucket : buckets) {
                double diff = bucket - avg;
                var += diff * diff;
            }
            double dev = Math.sqrt(var / times);
            System.out.println(size + " " + dev);
            devSum += dev;
        }

        return devSum;
    }

    public static class Inc extends ValueForHashIterator<Integer> {
        private int mVal;

        public Inc() {
            mVal = 0;
        }

        @Override
        public Integer next() {
            return ++mVal;
        }

        @Override
        public void reset() {
            mVal = 0;
        }
    }

    public static class TwoIdx extends ValueForHashIterator<int[]> {
        private int[] mX;

        private Random mRand;

        public TwoIdx() {
            mX = null;
            //mRand = new CellularAutomatonRNG();
            mRand = new Random();
        }

        @Override
        public int[] next() {
            if (mX[0] == mX[1]) {
                ++mX[0];
                mX[1] = 0;
            } else if (mX[0] > mX[1]) {
                ++mX[1];
            }
            int tmp = mX[0];
            mX[0] = mX[1];
            mX[1] = tmp;

            //                        mX[0] = mRand.nextInt();
            //                        mX[1] = mRand.nextInt();
            return mX;
        }

        @Override
        public void reset() {
            mX = new int[] { -1, -1 };
        }
    }

    @Ignore
    public void twoTest() {
        Iterator<int[]> it = new TwoIdx();
        for (int i = 0; i < 20; ++i) {
            int[] x = it.next();
            System.out.println(x[0] + " " + x[1]);
        }
    }

    @Test
    public void run() throws GeneralSecurityException {
        //final Random r = new Random();
        //final Random r = new SecureRandom();
        //final Random r = new CellularAutomatonRNG();
        //final Random r = new AESCounterRNG();
        //final Random r = new MersenneTwisterRNG();
        double dev = deviation(4, 17, 20 * 1000 * 1000, new TwoIdx(), new Hash<int[]>() {
            @Override
            public int hash(int[] x) {
                long key = (long) x[0] << 32 | x[1];
                //return x[1];

                // 638
                // return r.nextInt();

                // 623
                //                long k = (~key) + (key << 30);
                //                k ^= k >>> 27;
                //                k *= k;
                //                k ^= k >>> 13;
                //                k *= 41;
                //                k ^= k >>> 26;
                //                return (int) k;

                // 19177
                //                                 return x[0] << 16 ^ x[1];

                // 463
                //                key = (~key) + (key << 18); // key = (key << 18) - key - 1;
                //                key = key ^ (key >>> 31);
                //                key = key * 21; // key = (key + (key << 2)) + (key << 4);
                //                key = key ^ (key >>> 11);
                //                key = key + (key << 6);
                //                key = key ^ (key >>> 22);
                //                return (int) key;

                // 249
                final long k = key * 2654435761L; // golden ratio
                return (int) ((k >>> 32) ^ k);
            }
        });
        System.out.println("-------\n" + dev);
    }
}
