package be.desorted.functional.option;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class Option<T> {

    public abstract T getOrThrow();

    public abstract T orElse(T value);

    public abstract T orElse(Supplier<T> supplier);

    public Option<T> or(Supplier<Option<T>> supplier) {
        return map(x -> this).orElse(supplier);
    }

    public Option<T> filter(Predicate<T> predicate) {
        return flatMap(t -> predicate.test(t) ? this : none());
    }

    public abstract <S> Option<S> map(Function<T, S> f);

    <S> Option<S> flatMap(Function<T, Option<S>> f) {
        return map(f).orElse(none());
    }

    private static Option none = new None();

    public static <S> Option<S> some(S s) {
        return new Some<>(s);
    }

    public static <S> Option<S> none() {
        return none;
    }

    public static <A, B> Function<Option<A>, Option<B>> lift(Function<A, B> f) {
        try {
            return a -> a.map(f);
        } catch(Exception e) {
            return __ -> none();
        }
    }

    public static <A, B> Function<A, Option<B>> hlift(Function<A, B> f) {
        try {
            return a -> some(a).map(f);
        } catch(Exception e) {
            return __ -> none();
        }
    }

    private static class None<T> extends Option<T> {

        private None() {
        }

        @Override
        public T getOrThrow() {
            throw new IllegalStateException();
        }

        @Override
        public T orElse(T value) {
            return value;
        }

        @Override
        public T orElse(Supplier<T> supplier) {
            return supplier.get();
        }

        @Override
        public <S> Option<S> map(Function<T, S> f) {
            return none();
        }

    }

    private static class Some<T> extends Option<T> {

        private T t;

        private Some(T t) {
            this.t = t;
        }

        @Override
        public T getOrThrow() {
            return t;
        }

        @Override
        public T orElse(T value) {
            return t;
        }

        @Override
        public T orElse(Supplier<T> supplier) {
            return t;
        }

        @Override
        public <S> Option<S> map(Function<T, S> f) {
            return new Some(f.apply(t));
        }

    }

}



