package com.aaronbedra.chronometrophobia;

import com.jnape.palatable.lambda.io.IO;

import java.security.SecureRandom;

import static com.aaronbedra.chronometrophobia.Counter.counter;
import static com.aaronbedra.chronometrophobia.HMac.hMacSHA1;
import static com.aaronbedra.chronometrophobia.OTP.otp6;
import static com.aaronbedra.chronometrophobia.Seed.generateSeed;
import static com.aaronbedra.chronometrophobia.TimeStamp.now;
import static com.aaronbedra.chronometrophobia.TimeStep.timeStep30;
import static com.aaronbedra.chronometrophobia.Totp.generateInstance;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Into.into;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Tupler2.tupler;
import static com.jnape.palatable.lambda.io.IO.io;

public class Main {
    public static void main(String[] args) {
        generateSeed(64)
                .<IO<Seed>>runReaderT(new SecureRandom())
                .zip(now().fmap(tupler()))
                .flatMap(into((timeStamp, seed) -> generateInstance(otp6(), hMacSHA1(), seed, counter(timeStamp, timeStep30()))))
                .flatMap(failureOrTotp -> failureOrTotp.match(
                        hmacFailure -> io(() -> System.out.println(hmacFailure.getValue().getMessage())),
                        totp -> io(() -> System.out.println(totp))))
                .unsafePerformIO();
    }
}
