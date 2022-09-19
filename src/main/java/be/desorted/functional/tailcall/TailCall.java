package be.desorted.functional.tailcall;

import java.util.function.Supplier;
import java.util.stream.Stream;

public interface TailCall<T> extends Supplier<TailCall<T>> {

    default boolean hasResult() {
        return false;
    }

    default T getResult() {
        return Stream.iterate(this, TailCall::get)
                .filter(TailCall::hasResult)
                .map(TailCall::getResult)
                .findFirst().orElseThrow();
    }

    public static <X> Result<X> result(X t) {
        return new Result(t);
    }

    record Result<T>(T t) implements TailCall<T> {

        @Override
        public TailCall<T> get() {
            throw new IllegalStateException();
        }

        @Override
        public boolean hasResult() {
            return true;
        }

        @Override
        public T getResult() {
            return t;
        }
    }

}

