package be.desorted.functional.result;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface Result<V> {

    static <V> Result<V> success(V value) {
        return new Success<>(value);
    }

    static <V> Result<V> failure(RuntimeException exception) {
        return new Failure<>(exception);
    }

    static <V> Result<V> failure(Exception exception) {
        return new Failure<>(exception);
    }

    static <V> Result<V> failure(String message) {
        return new Failure<>(message);
    }

    static <V> Result<V> of(V value) {
        return of(value, "value cannot be null");
    }

    static <V> Result<V> of(V value, String message) {
        return value != null
                ? success(value)
                : failure("message");
    }

    static <V> Result<V> of(Predicate<V> predicate, V value) {
        return of(predicate, value, "failure when evaluating predicate");
    }

    static <V> Result<V> of(Predicate<V> predicate, V value, String message) {
        return predicate.test(value)
                ? success(value)
                : failure(message);
    }

    V getOrElse(V defaultValue);

    V getOrElse(Supplier<V> defaultValue);

    <U> Result<U> map(Function<V, U> f);

    <U> Result<U> flatMap(Function<V, Result<U>> f);

    Result<V> mapFailure(String s);

    default Result<V> orElse(Supplier<Result<V>> defaultValue) {
        return map(__ -> this).getOrElse(defaultValue);
    }

    default Result<V> filter(Predicate<V> predicate) {
        return flatMap(x -> predicate.test(x)
                ? this
                : failure("condition failed"));
    }

    default Result<V> filter(Predicate<V> predicate, String message) {
        return flatMap(x -> predicate.test(x)
                ? this
                : failure(message));
    }

    void forEach(Consumer<V> consumer);

    void forEachOrThrow(Consumer<V> consumer);

    default boolean exists() {
        return map(__ -> true).getOrElse(false);
    }

    static <A, B> Function<Result<A>, Result<B>> lift(Function<A, B> f) {
        return a -> a.map(f);
    }

    static <A, B, C> Function<Result<A>, Function<Result<B>, Result<C>>> lift2(Function<A, Function<B, C>> f) {
        return a -> b -> a.map(f).flatMap(b::map);
    }

    static <A, B, C, D> Function<Result<A>, Function<Result<B>, Function<Result<C>, Result<D>>>> lift3(Function<A, Function<B, Function<C, D>>> f) {
        return a -> b -> c -> a.map(f).flatMap(b::map).flatMap(c::map);
    }

    static <A, B, C> Result<C> map2(Result<A> ra, Result<B> rb, Function<A, Function<B, C>> f) {
        return lift2(f).apply(ra).apply(rb);
//        return ra.map(f).flatMap(rb::map);

    }

}

record Success<V>(V value) implements Result<V> {


    @Override
    public V getOrElse(V defaultValue) {
        return value;
    }

    @Override
    public V getOrElse(Supplier<V> defaultValue) {
        return value;
    }

    @Override
    public <U> Result<U> map(Function<V, U> f) {
        try {
            return new Success<>(f.apply(value));
        } catch (Exception e) {
            return new Failure<>(e);
        }

    }

    @Override
    public <U> Result<U> flatMap(Function<V, Result<U>> f) {
        try {
            return f.apply(value);
        } catch (Exception e) {
            return new Failure<>(e);
        }
    }

    @Override
    public Result<V> mapFailure(String s) {
        return this;
    }

    @Override
    public void forEach(Consumer<V> consumer) {
        consumer.accept(value);
    }

    @Override
    public void forEachOrThrow(Consumer<V> consumer) {
        forEach(consumer);
    }
}

record Failure<V>(RuntimeException exception) implements Result<V> {

    public Failure(Exception exception) {
        this(new RuntimeException(exception.getMessage(), exception));
    }

    public Failure(String message) {
        this(new IllegalStateException(message));
    }

    @Override
    public V getOrElse(V defaultValue) {
        return defaultValue;
    }

    @Override
    public V getOrElse(Supplier<V> defaultValue) {
        return defaultValue.get();
    }

    @Override
    public <U> Result<U> map(Function<V, U> f) {
        return new Failure<>(exception);
    }

    @Override
    public <U> Result<U> flatMap(Function<V, Result<U>> f) {
        return new Failure<>(exception);
    }

    @Override
    public Result<V> mapFailure(String message) {
        return new Failure<>(new IllegalStateException(message, exception));
    }

    @Override
    public void forEach(Consumer<V> consumer) {
    }

    @Override
    public void forEachOrThrow(Consumer<V> consumer) {
        throw exception;
    }
}
