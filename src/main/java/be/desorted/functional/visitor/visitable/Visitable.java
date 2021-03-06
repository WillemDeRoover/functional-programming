package be.desorted.functional.visitor.visitable;

import be.desorted.functional.visitor.visitor.Visitor;

import java.util.stream.Collector;
import java.util.stream.Stream;

public interface Visitable<T> {

    Stream<Object> get();

    default <R, RR> RR accept(Visitor<R> visitor, Collector<? super R, ?, RR> collector) {
        return get().map(visitor::visit).collect(collector);
    }
}
