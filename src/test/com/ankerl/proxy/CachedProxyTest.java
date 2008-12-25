package com.ankerl.proxy;

import junit.framework.Assert;

import org.junit.Test;

public class CachedProxyTest {

    public static interface A<X> {
        X a(X x);
    }

    /**
     * Counts the number of calls.
     */
    public static class AImpl implements A<Integer> {
        public int callCount = 0;
        @Override
        public Integer a(Integer b) {
            ++callCount;
            return b;
        }
    }

    @Test
    public void simple() {
        AImpl original = new AImpl();
        A a = CachedProxy.create(A.class, original);
        Assert.assertEquals(5, a.a(5));
        Assert.assertEquals(13, a.a(13));
        Assert.assertEquals(5, a.a(5));
        Assert.assertEquals(2, original.callCount);
        
        
    }
    


    public static class AThrowBounds implements A<Integer> {
        @Override
        public Integer a(Integer b) {
            int[] x = new int[] {b};
            return x[123];
        }
    }

    
    @Test(expected=ArrayIndexOutOfBoundsException.class)
    public void th() {
        A<Integer> a = CachedProxy.create(A.class, new AThrowBounds());
        a.a(2);
    }
    
    @Test
    public void nullTest() {
        AImpl original = new AImpl();
        A<Integer> a = CachedProxy.create(A.class, original);
        Assert.assertEquals(null, a.a(null));
        Assert.assertEquals(null, a.a(null));
        Assert.assertEquals(1, original.callCount);
    }
    
    
}
