package com.aaronbedra.chronometrophobia;

import lombok.AllArgsConstructor;
import lombok.Value;

import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor(access = PRIVATE)
public class Failure {
    Throwable value;

    public static Failure failure(Throwable value) {
        return new Failure(value);
    }
}
