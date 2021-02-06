package com.aaronbedra.chronometrophobia;

import com.jnape.palatable.lambda.adt.Either;
import com.jnape.palatable.lambda.io.IO;
import lombok.AllArgsConstructor;
import lombok.Value;

import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor(access = PRIVATE)
public class Totp {
    String value;

    public static Totp totp(String value) {
        return new Totp(value);
    }

    public static IO<Either<Failure, Totp>> generateInstance(OTP otp, HMac hMac, Seed seed, Counter counter) {
        return hMac.hash(seed, counter)
                .fmap(eitherFailureHmacResult -> eitherFailureHmacResult
                        .biMapR(hmacResult -> totp(calculate(hmacResult), otp)));
    }

    private static int calculate(HMac.HmacResult hmacResult) {
        byte[] result = hmacResult.getValue();
        int offset = result[result.length - 1] & 0xf;
        return ((result[offset] & 0x7f) << 24) |
                ((result[offset + 1] & 0xff) << 16) |
                ((result[offset + 2] & 0xff) << 8) |
                ((result[offset + 3] & 0xff));
    }

    private static Totp totp(int totpBinary, OTP otp) {
        String code = Integer.toString(totpBinary % otp.power().getValue());
        int length = otp.digits().getValue() - code.length();

        return length > 0
                ? new Totp("0".repeat(length) + code)
                : new Totp(code);
    }
}