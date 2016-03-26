package com.github.jtail.utils;


import com.github.jtail.utils.xfn.XOptional;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Part of the tests for extended behaviour of ifPresent
 */
public class XOptionalIfPresentTest {
    private XOptional<Boolean> present = XOptional.of(Boolean.TRUE);
    private XOptional<Boolean> absent = XOptional.empty();

    @Test
    public void present() throws Exception {
        AtomicBoolean marker = new AtomicBoolean(false);
        present.ifPresent(marker::set);
        assertTrue(marker.get());
    }

    @Test
    public void presentOrElse() throws Exception {
        present.ifPresent(b -> {}).orElse(this::fail);
    }

    @Test
    public void presentOrElseThrow() throws Exception {
        present.ifPresent(b -> {}).orElseThrow(CheckedException::new);
    }

    @Test
    public void absent() throws Exception {
        AtomicBoolean marker = new AtomicBoolean(false);
        absent.ifPresent(marker::set);
        assertFalse(marker.get());
    }

    @Test
    public void absentOrElse() throws Exception {
        AtomicBoolean marker = new AtomicBoolean(false);
        absent.ifPresent(b -> fail()).orElse(
                () -> marker.set(true)
        );
        assertTrue(marker.get());
    }

    @Test(expected = CheckedException.class)
    public void absentOrElseException() throws Exception {
        absent.ifPresent(b -> fail()).orElse(
                () -> {throw new CheckedException();}
        );
    }

    @Test(expected = CheckedException.class)
    public void absentOrElseThrow() throws Exception {
        absent.ifPresent(b -> {}).orElseThrow(CheckedException::new);
    }

    private void fail() {
        throw new ObscureException();
    }
}
