package com.aaronbedra.chronometrophobia;

import com.jnape.palatable.lambda.adt.Either;
import com.jnape.palatable.shoki.impl.StrictQueue;
import com.jnape.palatable.shoki.impl.StrictStack;
import org.junit.Test;

import static com.aaronbedra.chronometrophobia.Counter.counter;
import static com.aaronbedra.chronometrophobia.HMac.*;
import static com.aaronbedra.chronometrophobia.OTP.otp6;
import static com.aaronbedra.chronometrophobia.OTP.otp8;
import static com.aaronbedra.chronometrophobia.Seed.seed;
import static com.aaronbedra.chronometrophobia.TimeStamp.timeStamp;
import static com.aaronbedra.chronometrophobia.TimeStep.timeStep30;
import static com.aaronbedra.chronometrophobia.Totp.generateInstance;
import static com.aaronbedra.chronometrophobia.Totp.totp;
import static com.jnape.palatable.lambda.adt.Either.right;
import static com.jnape.palatable.lambda.functions.builtin.fn3.FoldLeft.foldLeft;
import static com.jnape.palatable.shoki.impl.StrictQueue.strictQueue;
import static com.jnape.palatable.shoki.impl.StrictStack.strictStack;
import static org.junit.Assert.assertEquals;

public class TotpTest {
    @Test
    public void rfc6238() {
        Seed seed = seed("3132333435363738393031323334353637383930");
        Seed seed32 = seed("3132333435363738393031323334353637383930313233343536373839303132");
        Seed seed64 = seed("31323334353637383930313233343536373839303132333435363738393031323334353637383930313233343536373839303132333435363738393031323334");

        StrictStack<Counter> counters = strictStack(
                counter(timeStamp(59L), timeStep30()),
                counter(timeStamp(1111111109L), timeStep30()),
                counter(timeStamp(1111111111L), timeStep30()),
                counter(timeStamp(1234567890L), timeStep30()),
                counter(timeStamp(2000000000L), timeStep30()),
                counter(timeStamp(20000000000L), timeStep30()));


        StrictQueue<Either<Failure, Totp>> expectedSha1 = strictQueue(
                right(totp("94287082")),
                right(totp("07081804")),
                right(totp("14050471")),
                right(totp("89005924")),
                right(totp("69279037")),
                right(totp("65353130")));

        StrictQueue<Either<Failure, Totp>> expectedSha256 = strictQueue(
                right(totp("46119246")),
                right(totp("68084774")),
                right(totp("67062674")),
                right(totp("91819424")),
                right(totp("90698825")),
                right(totp("77737706")));

        StrictQueue<Either<Failure, Totp>> expectedSha512 = strictQueue(
                right(totp("90693936")),
                right(totp("25091201")),
                right(totp("99943326")),
                right(totp("93441116")),
                right(totp("38618901")),
                right(totp("47863826")));

        StrictQueue<Either<Failure, Totp>> actualSha1 = foldLeft(
                (acc, value) -> acc.snoc(generateInstance(otp8(), hMacSHA1(), seed, value).unsafePerformIO()),
                strictQueue(),
                counters);

        StrictQueue<Either<Failure, Totp>> actualSha256 = foldLeft(
                (acc, value) -> acc.snoc(generateInstance(otp8(), hMacSHA256(), seed32, value).unsafePerformIO()),
                strictQueue(),
                counters);

        StrictQueue<Either<Failure, Totp>> actualSha512 = foldLeft(
                (acc, value) -> acc.snoc(generateInstance(otp8(), hMacSHA512(), seed64, value).unsafePerformIO()),
                strictQueue(),
                counters);

        assertEquals(expectedSha1, actualSha1);
        assertEquals(expectedSha256, actualSha256);
        assertEquals(expectedSha512, actualSha512);

    }

    @Test
    public void googleAuthenticator() {
        Seed seed = seed("3132333435363738393031323334353637383930");
        StrictStack<Counter> counters = strictStack(
                counter(timeStamp(59L), timeStep30()),
                counter(timeStamp(1111111109L), timeStep30()),
                counter(timeStamp(1111111111L), timeStep30()),
                counter(timeStamp(1234567890L), timeStep30()),
                counter(timeStamp(2000000000L), timeStep30()),
                counter(timeStamp(20000000000L), timeStep30()));

        StrictQueue<Either<Failure, Totp>> expected = strictQueue(
                right(totp("287082")),
                right(totp("081804")),
                right(totp("050471")),
                right(totp("005924")),
                right(totp("279037")),
                right(totp("353130")));

        StrictQueue<Either<Failure, Totp>> actual = foldLeft(
                (acc, value) -> acc.snoc(generateInstance(otp6(), hMacSHA1(), seed, value).unsafePerformIO()),
                strictQueue(),
                counters);

        assertEquals(expected, actual);
    }
}
