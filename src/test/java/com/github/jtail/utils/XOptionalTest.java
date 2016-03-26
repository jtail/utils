package com.github.jtail.utils;


import com.github.jtail.utils.xfn.XOptional;
import org.junit.Test;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Shamelessly stolen from Oracle OpenJDK and adapted: <br/>
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 */
@SuppressWarnings({"ThrowableInstanceNeverThrown", "EqualsWithItself", "IndexOfReplaceableByContains", "unused"})
public class XOptionalTest {

    @Test
    public void cast() {
        XOptional<Boolean> empty = XOptional.from(Optional.of(Boolean.TRUE));
        Boolean got = empty.get();
    }

    @Test
    public void empty() {
        XOptional<Boolean> empty = XOptional.empty();
        XOptional<String> presentEmptyString = XOptional.of("");
        XOptional<Boolean> present = XOptional.of(Boolean.TRUE);

        // empty
        assertTrue(empty.equals(empty));
        assertTrue(empty.equals(XOptional.empty()));
        assertTrue(!empty.equals(present));
        assertTrue(0 == empty.hashCode());
        assertTrue(!empty.toString().isEmpty());
        assertTrue(!empty.toString().equals(presentEmptyString.toString()));
        assertTrue(!empty.isPresent());
        empty.ifPresent(v -> fail());
        assertSame(null, empty.orElse(null));
        RuntimeException orElse = new RuntimeException() {};
        assertSame(Boolean.FALSE, empty.orElse(Boolean.FALSE));
        assertSame(null, empty.orElseGet(() -> null));
        assertSame(Boolean.FALSE, empty.orElseGet(() -> Boolean.FALSE));
    }

    @Test(expected = NoSuchElementException.class)
    public void emptyGet() {
        XOptional<Boolean> empty = XOptional.empty();

        Boolean got = empty.get();
    }

    @Test(expected = NullPointerException.class)
    public void emptyOrElseGetNull() {
        XOptional<Boolean> empty = XOptional.empty();

        Boolean got = empty.orElseGet(null);
    }

    @Test(expected = NullPointerException.class)
    public void emptyOrElseThrowNull() throws Throwable {
        XOptional<Boolean> empty = XOptional.empty();

        Boolean got = empty.orElseThrow(null);
    }

    @Test(expected = ObscureException.class)
    public void emptyOrElseThrow() throws Exception {
        XOptional<Boolean> empty = XOptional.empty();

        Boolean got = empty.orElseThrow(ObscureException::new);
    }

    @Test
    public void present() {
        XOptional<Boolean> empty = XOptional.empty();
        XOptional<String> presentEmptyString = XOptional.of("");
        XOptional<Boolean> present = XOptional.of(Boolean.TRUE);

        assertTrue(present.equals(present));
        assertTrue(present.equals(XOptional.of(Boolean.TRUE)));
        assertTrue(!present.equals(empty));
        assertTrue(Boolean.TRUE.hashCode() == present.hashCode());
        assertTrue(!present.toString().isEmpty());
        assertTrue(!present.toString().equals(presentEmptyString.toString()));
        assertTrue(-1 != present.toString().indexOf(Boolean.TRUE.toString()));
        assertSame(Boolean.TRUE, present.get());
        assertSame(Boolean.TRUE, present.orElse(null));
        assertSame(Boolean.TRUE, present.orElse(Boolean.FALSE));
        assertSame(Boolean.TRUE, present.orElseGet(null));
        assertSame(Boolean.TRUE, present.orElseGet(() -> null));
        assertSame(Boolean.TRUE, present.orElseGet(() -> Boolean.FALSE));
        assertSame(Boolean.TRUE, present.<RuntimeException>orElseThrow(null));
        assertSame(Boolean.TRUE, present.<RuntimeException>orElseThrow(ObscureException::new));
    }

    @Test
    public void ofNullable() {
        XOptional<String> instance = XOptional.ofNullable(null);
        assertFalse(instance.isPresent());

        instance = XOptional.ofNullable("Duke");
        assertTrue(instance.isPresent());
        assertEquals(instance.get(), "Duke");
    }

    @Test
    public void filter() {
        // Null mapper function
        XOptional<String> empty = XOptional.empty();
        XOptional<String> duke = XOptional.of("Duke");

        try {
            XOptional<String> result = empty.filter(null);
            fail("Should throw NPE on null mapping function");
        } catch (NullPointerException npe) {
            // expected
        }

        XOptional<String> result = empty.filter(String::isEmpty);
        assertFalse(result.isPresent());

        result = duke.filter(String::isEmpty);
        assertFalse(result.isPresent());
        result = duke.filter(s -> s.startsWith("D"));
        assertTrue(result.isPresent());
        assertEquals(result.get(), "Duke");

        XOptional<String> emptyString = XOptional.of("");
        result = emptyString.filter(String::isEmpty);
        assertTrue(result.isPresent());
        assertEquals(result.get(), "");
    }

    @Test
    public void map() {
        XOptional<String> empty = XOptional.empty();
        XOptional<String> duke = XOptional.of("Duke");

        // Null mapper function
        try {
            XOptional<Boolean> b = empty.map(null);
            fail("Should throw NPE on null mapping function");
        } catch (NullPointerException npe) {
            // expected
        }

        // Map an empty value
        XOptional<Boolean> b = empty.map(String::isEmpty);
        assertFalse(b.isPresent());

        // Map into null
        b = empty.map(n -> null);
        assertFalse(b.isPresent());
        b = duke.map(s -> null);
        assertFalse(b.isPresent());

        // Map to value
        XOptional<Integer> l = duke.map(String::length);
        assertEquals(l.get().intValue(), 4);
    }

    @Test
    public void flatMap() {
        XOptional<String> empty = XOptional.empty();
        XOptional<String> duke = XOptional.of("Duke");

        // Null mapper function
        try {
            XOptional<Boolean> b = empty.flatMap(null);
            fail("Should throw NPE on null mapping function");
        } catch (NullPointerException npe) {
            // expected
        }

        // Map into null
        try {
            XOptional<Boolean> b = duke.flatMap(s -> null);
            fail("Should throw NPE when mapper return null");
        } catch (NullPointerException npe) {
            // expected
        }

        // Empty won't invoke mapper function
        try {
            XOptional<Boolean> b = empty.flatMap(s -> null);
            assertFalse(b.isPresent());
        } catch (NullPointerException npe) {
            fail("Mapper function should not be invoked");
        }

        // Map an empty value
        XOptional<Integer> l = empty.flatMap(s -> XOptional.of(s.length()));
        assertFalse(l.isPresent());

        // Map to value
        XOptional<Integer> fixture = XOptional.of(Integer.MAX_VALUE);
        l = duke.flatMap(s -> XOptional.of(s.length()));
        assertTrue(l.isPresent());
        assertEquals(l.get().intValue(), 4);

        // Verify same instance
        l = duke.flatMap(s -> fixture);
        assertSame(l, fixture);
    }

    private static class CheckedException extends Exception {
    }

    private static class ObscureException extends RuntimeException {
    }
}
