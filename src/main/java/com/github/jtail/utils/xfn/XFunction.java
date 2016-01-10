package com.github.jtail.utils.xfn;

/**
 * Version of {@link java.util.function.Function} with exception support.
 * @param <T> the type of the input to the function
 * @param <R> the type of the result of the function
 * @param <X> the type of the exception thrown by the function
 */
@FunctionalInterface
public interface XFunction<T, R, X extends Exception> {
    /**
     * Applies this function to the given argument.
     *
     * @param t the function argument
     * @return the function result
     * @throws X if exception occurs
     */
    R apply(T t) throws X;
}
