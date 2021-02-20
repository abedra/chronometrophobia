package com.aaronbedra.chronometrophobia;

import com.jnape.palatable.lambda.io.IO;
import com.jnape.palatable.lambda.monad.transformer.builtin.ReaderT;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.security.SecureRandom;

import static com.aaronbedra.chronometrophobia.Hex.bytesToHex;
import static com.jnape.palatable.lambda.io.IO.io;
import static com.jnape.palatable.lambda.monad.transformer.builtin.ReaderT.readerT;
import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor(access = PRIVATE)
public class Seed {
    String value;

    public static Seed seed(String value) {
        return new Seed(value);
    }

    public static ReaderT<SecureRandom, IO<?>, Seed> generateSeed(int length) {
        return readerT(secureRandom -> io(() -> {
            byte[] randomBytes = new byte[length];
            secureRandom.nextBytes(randomBytes);
            return seed(bytesToHex(randomBytes));
        }));
    }
}
