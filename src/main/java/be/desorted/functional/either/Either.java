package be.desorted.functional.either;

import java.util.function.Function;
import java.util.function.Supplier;

public interface Either<E, A> {

    static <E, A> Either<E, A> left(E value) {
        return new Left<>(value);
    }

    static <E, A> Either<E, A> right(A value) {
        return new Right<>(value);
    }

    <V> Either<E, V> map(Function<A, V> f);

    <V> Either<E, V> flatMap(Function<A, Either<E, V>> f);

    A getOrElse(Supplier<A> defaultValue);

    default Either<E, A> orElse(Supplier<Either<E, A>> defaultValue) {
        return map(__ -> this).getOrElse(defaultValue);
    }

    class Left<E, A> implements Either<E, A> {
        private final E value;

        public Left(E value) {
            this.value = value;
        }

        @Override
        public <V> Either<E, V> map(Function<A, V> f) {
            return new Left<>(value);
        }

        @Override
        public <V> Either<E, V> flatMap(Function<A, Either<E, V>> f) {
            return new Left<>(value);
        }

        @Override
        public A getOrElse(Supplier<A> defaultValue) {
            return defaultValue.get();
        }

    }

    class Right<T, U> implements Either<T, U> {
        private final U value;

        public Right(U value) {
            this.value = value;
        }

        @Override
        public <V> Either<T, V> map(Function<U, V> f) {
            return f.andThen(Right<T,V>::new).apply(value);
        }

        @Override
        public <V> Either<T, V> flatMap(Function<U, Either<T, V>> f) {
            return f.apply(value);
        }

        @Override
        public U getOrElse(Supplier<U> defaultValue) {
            return value;
        }

    }

}
