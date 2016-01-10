package com.github.jtail.utils.xfn;

import java.util.concurrent.Callable;

/**
 * Version of {@link Callable} with generic exception support.
 */
@FunctionalInterface
public interface XCallable<T, X extends Exception> extends Callable<T> {
    @Override
    T call() throws X;
}
