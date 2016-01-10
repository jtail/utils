package com.github.jtail.utils.xfn;

/**
 * Version of {@link java.util.function.Consumer} with exception support.
 */
@FunctionalInterface
public interface XConsumer<T, X extends Exception> {
    /**
     * Performs this operation on the given argument.
     *
     * @param t the input argument
     */
    void accept(T t) throws X;

}
