package com.aaronbedra.chronometrophobia;

import com.jnape.palatable.lambda.io.IO;
import lombok.AllArgsConstructor;
import lombok.Value;

import static com.jnape.palatable.lambda.io.IO.io;
import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor(access = PRIVATE)
public class TimeStamp {
    long value;

    public static TimeStamp timeStamp(long value) {
        return new TimeStamp(value);
    }

    public static IO<TimeStamp> now() {
        return io(() -> timeStamp(System.currentTimeMillis() / 1000));
    }
}
