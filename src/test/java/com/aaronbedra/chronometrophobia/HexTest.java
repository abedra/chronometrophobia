package com.aaronbedra.chronometrophobia;

import org.junit.Test;

import static com.aaronbedra.chronometrophobia.Hex.bytesToHex;
import static org.junit.Assert.assertEquals;

public class HexTest {
    @Test
    public void encode() {
        assertEquals(bytesToHex("hello world".getBytes()), "68656C6C6F20776F726C64");
    }
}