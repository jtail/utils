package com.github.jtail.utils.xfn;

/**
 * A version of {@link java.util.function.Supplier} with exception support
 */
@FunctionalInterface
public interface XSupplier<T, X extends Exception> {

    /**
     * Gets a result.
     *
     * @return a result
     */
    T get() throws X;
}
