package com.github.jtail.utils.xfn;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Class to support functional style of writing conditions,
 * chaining after {@link XOptional#ifPresent(Consumer)} call.
 */
public class Condition {
    private final static Condition TRUE = new Condition();

    private final static Condition ELSE = new Condition() {
        @SuppressWarnings("unchecked")
        @Override
        public <X extends Exception> void orElse(XRunnable runnable) throws X {
            try {
                runnable.run();
            } catch (Exception e) {
                throw (X)e;
            }
        }

        @Override
        public <X extends Exception> void orElseThrow(Supplier<X> supplier) throws X {
            throw supplier.get();
        }
    };

    public static Condition present() {
        return TRUE;
    }

    public static Condition absent() {
        return ELSE;
    }

    public <X extends Exception> void orElse(XRunnable runnable) throws X {
    }

    public <X extends Exception> void orElseThrow(Supplier<X> supplier) throws X {
    }


}
