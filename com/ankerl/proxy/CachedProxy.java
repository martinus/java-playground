package com.ankerl.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * Creates an intermediate proxy object for a given interface. All calls are
 * cached.
 * 
 * @author Martin Ankerl (martin.ankerl@gmail.at)
 * @version $Rev$
 */
public class CachedProxy {

    /**
     * Query object to find out if the exact same query was already made.
     * 
     * @author Martin Ankerl (martin.ankerl@profactor.at)
     * @version $Rev$
     */
    private static final class Args {
        private final Method mMethod;

        private final Object[] mArgs;

        private final int mHash;

        public Args(final Method m, final Object[] args) {
            mMethod = m;
            mArgs = args;
            // precalculate hash
            mHash = calcHash();
        }

        /**
         * Method and all the arguments have to be equal. Assumes that obj is of
         * the same type.
         */
        @Override
        public boolean equals(final Object obj) {
            final Args other = (Args) obj;
            if (!mMethod.equals(other.mMethod)) {
                return false;
            }
            for (int i = 0; i < mArgs.length; ++i) {
                if (!mArgs[i].equals(other.mArgs[i])) {
                    return false;
                }
            }
            return true;
        }

        /**
         * Use the precalculated hash.
         */
        @Override
        public int hashCode() {
            return mHash;
        }

        /**
         * Try to use a good & fast hash function here.
         */
        public int calcHash() {
            int h = mMethod.hashCode();
            for (final Object o : mArgs) {
                h = h * 65599 + (o == null ? 0 : o.hashCode());
            }
            return h;
        }
    }

    /**
     * Creates an intermediate proxy object that uses cached results if
     * available, otherwise calls the given code.
     * 
     * @param <T>
     *            Type of the class.
     * @param cl
     *            The interface for which the proxy should be created.
     * @param code
     *            The actual calculation code that should be cached.
     * @return The proxy.
     */
    @SuppressWarnings("unchecked")
    public static <T> T create(final Class<T> cl, final T code) {
        // create the cache
        final Map<Args, Object> argsToOutput = new HashMap<Args, Object>();

        // proxy for the interface T
        return (T) Proxy.newProxyInstance(cl.getClassLoader(), new Class<?>[] { cl }, new InvocationHandler() {

            @Override
            public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
                final Args input = new Args(method, args);
                Object result = argsToOutput.get(input);
                if (result == null) {
                    result = method.invoke(code, args);
                    argsToOutput.put(input, result);
                }
                return result;
            }
        });
    }
}
