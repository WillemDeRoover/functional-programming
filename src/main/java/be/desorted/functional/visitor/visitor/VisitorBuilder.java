package be.desorted.functional.visitor.visitor;

import be.desorted.functional.visitor.model.Car;

import java.util.function.BiConsumer;
import java.util.function.Function;

public interface VisitorBuilder<R> extends BiConsumer<Class<?>, Function<Object, R>> {

    default void register(Class<?> type, Function<Object, R> function) {
        this.accept(type, function);
    };

}
