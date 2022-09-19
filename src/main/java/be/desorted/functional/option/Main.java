package be.desorted.functional.option;

import be.desorted.functional.list.MyList;

import java.util.function.Function;

public class Main {

    public static void main(String[] args) {
        Function<Integer, Function<String, Integer>> parseWithRadix =
                radix -> string -> Integer.parseInt(string, radix);
        Function<String, Option<Integer>> parse16 =
                Option.hlift(parseWithRadix.apply(16));
        MyList<String> list = MyList.list("4", "5", "6", "7", "8", "9");
        MyList<Option<Integer>> map = list.map(parse16);
        MyList<Option<Integer>> map2 = map.cons(Option.none()).cons(Option.some(20));
        Option<MyList<Integer>> sequence = sequence(map2);
        System.out.println("test");

    }

    static Function<MyList<Double>, Double> sum = list -> list.foldLeft(0d, x -> y -> x + y);
    static Function<MyList<Double>, Option<Double>> mean =
            list ->
                    list.isEmpty()
                            ? Option.none()
                            : Option.some(sum.apply(list)/list.length());

    static Function<MyList<Double>, Option<Double>> variance =
            list ->
                mean.apply(list)
                        .flatMap(m -> mean.apply(list.map(x -> Math.pow(x - m, 2))));


    static <A, B, C> Option<C> map2(Option<A> a, Option<B> b, Function<A, Function<B ,C>> f) {
        return a.map(f).flatMap(b::map);
    }

    private static <A> Option<MyList<A>> sequence(MyList<Option<A>> list) {
        return list.foldRight(Option.some(MyList.empty()), x -> y ->  map2(x, y, a -> b -> b.cons(a)));
    }

    private static <A, B> Option<MyList<B>> traverse(MyList<A> list, Function<A, Option<B>> f) {
        return list.foldRight(Option.some(MyList.empty()), x -> y -> map2(f.apply(x), y, a -> b -> b.cons(a)));
    }

    private static <A> Option<MyList<A>> sequenceWithTraverse(MyList<Option<A>> list) {
        return traverse(list, x -> x);
    }



}
