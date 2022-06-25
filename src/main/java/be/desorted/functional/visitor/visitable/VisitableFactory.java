package be.desorted.functional.visitor.visitable;

import java.util.function.Function;
import java.util.stream.Stream;

public interface VisitableFactory<T> {

    Stream<Function<T, ? extends Object>> functions();

    static <T> X<T> visiting(Class<T> type) {
        return () -> type;
    }

    default Visitable<T> makeVisitable(T t) {
        return () -> functions().map(function -> function.apply(t));
    }

    interface X<T> {
        Class<T> type();

        default VisitableFactory<T> collectsFrom(Function<T, ? extends Object>... functions) {
            return () -> Stream.of(functions);
        }
    }
}
