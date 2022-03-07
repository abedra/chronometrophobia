package com.aaronbedra.chronometrophobia;

import com.jnape.palatable.lambda.adt.Either;
import com.jnape.palatable.lambda.adt.coproduct.CoProduct3;
import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.io.IO;
import lombok.AllArgsConstructor;
import lombok.Value;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;

import static com.aaronbedra.chronometrophobia.Failure.failure;
import static com.jnape.palatable.lambda.adt.Either.left;
import static com.jnape.palatable.lambda.functions.builtin.fn1.Constantly.constantly;
import static com.jnape.palatable.lambda.io.IO.io;
import static lombok.AccessLevel.PRIVATE;

public abstract class HMac implements CoProduct3<HMac.HMacSHA1, HMac.HMacSHA256, HMac.HMacSHA512, HMac> {
    public abstract IO<Mac> getInstance();

    private HmacKey generateKey(Seed seed) {
        byte[] bArray = new BigInteger("10" + seed.getValue(), 16).toByteArray();
        byte[] ret = new byte[bArray.length - 1];
        System.arraycopy(bArray, 1, ret, 0, ret.length);
        return new HmacKey(ret);
    }

    public IO<Either<Failure, HmacResult>> hash(Seed seed, Counter counter) {
        HmacKey key = generateKey(seed);

        return getInstance()
                .flatMap(hmac -> io(() -> new SecretKeySpec(key.getValue(), "RAW"))
                        .flatMap(secretKeySpec -> io(() -> hmac.init(secretKeySpec)))
                        .flatMap(constantly(io(() -> Either.<Failure, HmacResult>right(new HmacResult(hmac.doFinal(counter.getValue())))))))
                .catchError(throwable -> io(left(failure(throwable))));
    }

    public static HMacSHA1 hMacSHA1() {
        return HMacSHA1.INSTANCE;
    }

    public static HMacSHA256 hMacSHA256() {
        return HMacSHA256.INSTANCE;
    }

    public static HMacSHA512 hMacSHA512() {
        return HMacSHA512.INSTANCE;
    }

    public static final class HMacSHA1 extends HMac {
        public static final HMacSHA1 INSTANCE = new HMacSHA1();

        private HMacSHA1() {
        }

        @Override
        public <R> R match(Fn1<? super HMacSHA1, ? extends R> aFn,
                           Fn1<? super HMacSHA256, ? extends R> bFn,
                           Fn1<? super HMacSHA512, ? extends R> cFn) {
            return aFn.apply(this);
        }

        @Override
        public IO<Mac> getInstance() {
            return io(() -> Mac.getInstance("HmacSHA1"));
        }
    }

    public static final class HMacSHA256 extends HMac {
        public static final HMacSHA256 INSTANCE = new HMacSHA256();

        private HMacSHA256() {
        }

        @Override
        public <R> R match(Fn1<? super HMacSHA1, ? extends R> aFn,
                           Fn1<? super HMacSHA256, ? extends R> bFn,
                           Fn1<? super HMacSHA512, ? extends R> cFn) {
            return bFn.apply(this);
        }

        @Override
        public IO<Mac> getInstance() {
            return io(() -> Mac.getInstance("HmacSHA256"));
        }
    }

    public static final class HMacSHA512 extends HMac {
        public static final HMacSHA512 INSTANCE = new HMacSHA512();

        private HMacSHA512() {
        }

        @Override
        public <R> R match(Fn1<? super HMacSHA1, ? extends R> aFn,
                           Fn1<? super HMacSHA256, ? extends R> bFn,
                           Fn1<? super HMacSHA512, ? extends R> cFn) {
            return cFn.apply(this);
        }

        @Override
        public IO<Mac> getInstance() {
            return io(() -> Mac.getInstance("HmacSHA512"));
        }
    }

    @Value
    @AllArgsConstructor(access = PRIVATE)
    public static class HmacResult {
        byte[] value;

        public static HmacResult hmacResult(byte[] value) {
            return new HmacResult(value);
        }
    }

    @Value
    @AllArgsConstructor(access = PRIVATE)
    public static class HmacKey {
        byte[] value;

        public static HmacKey hmacKey(byte[] value) {
            return new HmacKey(value);
        }
    }
}
