# chronometrophobia ![Build](https://github.com/abedra/chronometrophobia/workflows/Java%20CI%20with%20Maven/badge.svg)

A Java TOTP library.

## Installation

```xml
<dependency>
  <groupId>com.aaronbedra</groupId>
  <artifactId>chronometrophobia</artifactId>
  <version>1.0</version>
</dependency>
```

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
