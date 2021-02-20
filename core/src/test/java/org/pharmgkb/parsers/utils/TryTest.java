package org.pharmgkb.parsers.utils;

import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TryTest {


    @Test
    public void testSimple() {
        assertEquals(Try.succeed(1).get(), Optional.of(1));
        assertEquals(Try.succeed(1).getException(), Optional.empty());
        assertEquals(Try.fail(new Exception("")).get(), Optional.empty());
        assertEquals(Try.fail(new Exception("")).getException().get().getMessage(), "");
    }

    @Test
    public void testAttempt() {
        Supplier<String> supplier = () -> "abc";
        Supplier<String> failing = () -> {
            throw new IllegalArgumentException("");
        };
        assertEquals(Try.attempt(supplier).get(), Optional.of("abc"));
        assertEquals(Try.attempt(supplier).getException(), Optional.empty());
        assertEquals(Try.attempt(failing).getException().get().getMessage(), "");
        assertEquals(Try.attempt(failing).get(), Optional.empty());
    }

    @Test
    public void testMap() {
        assertEquals(Try.succeed("").map(x -> x + "abc").get(), Optional.of("abc"));
        Try<String,Exception> failed = Try.fail(new IllegalArgumentException(""));
        assertEquals(failed.map(x -> x + "abc").get(), Optional.empty());
    }
    @Test
    public void testRecover() {
        assertEquals(Try.succeed("").recover(() -> "abc").get(), Optional.of(""));
        Try<String,Exception> failed = Try.fail(new IllegalArgumentException(""));
        assertEquals(failed.recover(() -> "abc").get(), Optional.of("abc"));
    }
    @Test
    public void testCompose() {
        assertEquals(Try.succeed("").compose(x -> x + "abc").get(), Optional.of("abc"));
        Try<String,Exception> failed = Try.fail(new IllegalArgumentException(""));
        assertEquals(failed.compose(x -> x + "abc").get(), Optional.empty());
        Function<Integer, Double> succeeding = x -> x/2.0;
        Function<Integer, Double> failing = x -> {
            throw new IllegalArgumentException("");
        };
        assertEquals(Try.succeed(1).compose(succeeding).get(), Optional.of(0.5));
        assertEquals(Try.succeed(1).compose(succeeding).get(), Optional.of(0.5));
    }
}
