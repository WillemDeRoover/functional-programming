package be.desorted.functional.fcase;

@FunctionalInterface
interface Effect<T> {
    void handle(T t);
}
