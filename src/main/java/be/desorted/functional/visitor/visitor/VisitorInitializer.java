package be.desorted.functional.visitor.visitor;

import java.util.function.Consumer;

public interface VisitorInitializer<R> extends Consumer<VisitorBuilder<R>> {

    default void init(VisitorBuilder<R> visitorBuilder) {
        this.accept(visitorBuilder);
    }
}
