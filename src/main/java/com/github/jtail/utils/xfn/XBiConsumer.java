/*
 * Copyright (c) 2010, 2013, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */
package com.github.jtail.utils.xfn;

/**
 * Version of {@link java.util.function.Consumer} with exception support.
 *
 * @param <T> the type of the first argument to the function
 * @param <U> the type of the second argument to the function
 * @param <X> the type of the exception thrown by the function
 */
@FunctionalInterface
public interface XBiConsumer<T, U, X extends Exception> {

    /**
     * Performs this operation on the given arguments.
     *
     * @param t the first function argument
     * @param u the second function argument
     */
    void accept(T t, U u) throws X;

}
