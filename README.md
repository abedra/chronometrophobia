# chronometrophobia

A Java TOTP library.

## Installation

TODO: publish to central and add descriptor

## Usage

```java
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
```