package be.desorted.functional.memoization;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Memoized {

    public static <R, S> Function<R, S> memoize(Function<R, S> f) {
        Map<R, S> cache = new HashMap<>();
        return r -> cache.computeIfAbsent(r, f);
    }

}