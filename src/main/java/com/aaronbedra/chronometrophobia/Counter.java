package com.aaronbedra.chronometrophobia;

import lombok.AllArgsConstructor;
import lombok.Value;

import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor(access = PRIVATE)
public class Counter {
    byte[] value;

    public static Counter counter(byte[] value) {
        return new Counter(value);
    }

    public static Counter counter(TimeStamp timeStamp, TimeStep timeStep) {
        long counter = timeStamp.getValue() / timeStep.value();
        byte[] buffer = new byte[Long.SIZE / Byte.SIZE];
        for (int i = 7; i >= 0; i--) {
            buffer[i] = (byte) (counter & 0xff);
            counter = counter >> 8;
        }
        return counter(buffer);
    }
}