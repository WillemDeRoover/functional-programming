package be.desorted.functional.fcase;

import java.util.function.Supplier;
import java.util.stream.Stream;

// ex 3.2
public record Case<T>(Supplier<Boolean> condition, Supplier<Result<T>> result) {

    public static <T> Case<T> mCase(Supplier<Boolean> condition, Supplier<Result<T>> result) {
        return new Case<>(condition, result);
    }

    public static <T> Case<T> mCase(Supplier<Result<T>> result) {
        return new Case<>(() -> true, result);
    }

    public static <T> Result<T> match(Case<T> defaultCase, Case<T>... matchers) {
        return Stream.of(matchers)
                .filter(mCase -> mCase.condition.get())
                .map(mCase -> mCase.result.get())
                .findFirst()
                .orElse(defaultCase.result.get());
    }


 }
