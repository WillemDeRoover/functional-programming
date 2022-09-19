package be.desorted.functional.exercises.chapter2;

import java.util.function.Function;

public class Functions {

    public static <A, B, C> Function<A, C> compose(Function<B, C> f, Function<A, B> g) {

//      ex 2.1: write a compose function
//      return new Function<A, C>() {
//          @Override
//          public C apply(A arg) {
//              return f.apply(g.apply(arg));
//          }
//      };

//      ex 2.2: write a compose function using lambda's
        return x -> f.apply(g.apply(x));
    }

    public static void main(String[] args) {

//      ex 2.3: write a function to add 2 arguments
        Function<Integer, Function<Integer, Integer>> add = x -> y -> x + y;
        Function<Integer, Integer> add3 = add.apply(3);
        System.out.println(add3.apply(5));

//      ex 2.4: write a function to compose two function
        Function<Function<Integer, Integer>, // x ->
                Function<Function<Integer, Integer>, // y ->
                        Function<Integer, Integer>>> compose = // z -> x.apply(y.apply)
                x -> y -> z -> x.apply(y.apply(z));

    }

    //  ex 2.5: write a polymorphic version of the compose function
    public static <A, B, C>
    Function<Function<B, C>, // x ->
            Function<Function<A, B>, // y ->
                    Function<A, C>>> polyCompose() {
        return x -> y -> z -> x.apply(y.apply(z));
    }

    //  ex 2.6: write the andThen function (polymorphic)
    public static <A, B, C> Function<Function<A, B>, Function<Function<B, C>, Function<A, C>>> polyAndThen() {
        return x -> y -> z -> y.apply(x.apply(z));
    }

    //  ex 2.7: write a method to partially apply a carried function of two arguments to its first argument
    public static <A, B, C> Function<B, C> partial(A a, Function<A, Function<B, C>> f) {
        return f.apply(a);
    }

    //  ex 2.8: write a method to partially apply a carried function of two arguments to its second argument
    public static <A, B, C> Function<A, C> partialB(B b, Function<A, Function<B, C>> f) {
        return a -> f.apply(a).apply(b);
    }

    //  ex 2.9
    public static <A, B, C, D> Function<A, Function<B, Function<C, Function<D, String>>>> format() {
        return a -> b -> c -> d -> String.format("%s, %s, %s, %s", a, b, c, d);
    }

    //  ex 2.10: write a method to curry a function of a tuple
    public static <A, B, C> Function<A, Function<B, C>> curry(Function<Tuple<A, B>, C> f) {
        return a -> b -> f.apply(new Tuple<>(a, b));
    }

    //  ex 2.11: swap the arguments of a curried function
    public static <A, B, C> Function<B, Function<A, C>> reverseArgA(Function<A, Function<B, C>> f) {
        return b -> a -> f.apply(a).apply(b);
    }

    // ex 2.12: write a recursive function
    public final static Function<Integer, Integer> factorial = i -> i == 0 ? 1 : i * Functions.factorial.apply(i - 1);


    private record Tuple<A, B>(A a, B b) {

    }
}

