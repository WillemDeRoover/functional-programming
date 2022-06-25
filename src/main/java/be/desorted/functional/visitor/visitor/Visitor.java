package be.desorted.functional.visitor.visitor;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Visitor<R> {

    R visit(Object o);

    static <R> Visitor<R> of(VisitorInitializer<R> visitorInitializer) {
        Map<Class<?>, Function<Object, R>> registry = new HashMap<>();
        visitorInitializer.init(registry::put);
        return o -> registry.get(o.getClass()).apply(o);
    }

    static <T, R> X<T, R> forType(Class<T> type) {
        return () -> type;
    }

    interface X<T, R> {

        Class<T> type();

        default Y<R> execute(Function<T, R> function) {
            return builder -> builder.register(type(), function.compose(type()::cast));
        }
    }

    interface Y<R> extends VisitorInitializer<R>{

        default <T> Z<T, R> forType(Class<T> type) {
            return () -> new A<>(type, this);
        }

        default Y<R> andThen(Y<R> after) {
            return  builder -> {this.accept(builder); after.accept(builder);};
        }
    }

    interface Z<T, R> extends Supplier<A<T,R>> {

        default Class<T> type() {
            return get().type;
        }

        default Y<R> previous() {
            return get().y;
        }

        default Y<R> execute(Function<T, R> function) {
            return previous().andThen(builder -> builder.register(type(), function.compose(type()::cast)));
        }

    }

    record A<T, R>(Class<T> type, Y<R> y) {}
}