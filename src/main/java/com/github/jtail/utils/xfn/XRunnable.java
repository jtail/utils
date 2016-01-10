package com.github.jtail.utils.xfn;

/**
 * Version of {@link Runnable} that supports throwing a generic exception.
 *
 * @param <X> type of the exception that can be thrown.
 */
@FunctionalInterface
public interface XRunnable<X extends Exception> {
    /**
     * The general contract of this method is that it may take any action whatsoever.
     */
    void run() throws X;
}
