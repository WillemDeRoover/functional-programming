package be.desorted.functional.list;

import be.desorted.functional.option.Option;

import java.util.function.Function;

public class Main {

    public static void main(String[] args) {
        MyList<Integer> list = MyList.list(1, 2, 3, 4);
        System.out.println(list.toString2());

        MyList<Integer> droppedList = list.drop(2);
        System.out.println(droppedList.toString2());
        System.out.println(list.toString2());

        System.out.println(list.dropWhile(x -> x < 2).toString2());

        String result = list.foldRight("0", i -> s -> addIs(i, s));
        System.out.println(result);

        System.out.println(concatRightFold(MyList.list(1, 2), MyList.list(3, 4)).toString2());
        System.out.println(concatLeftFold(MyList.list(1, 2), MyList.list(3, 4)).toString2());

        System.out.println(times3(list).toString2());

        System.out.println(list.map(x -> x * 3).toString2());

        System.out.println(list.concat(MyList.list(5, 6, 7, 8)).toString2());
        System.out.println(list.flatMap(i -> MyList.list(i, -i)).toString2());

        System.out.println("range: " + range(5, 10).toString2());
    }


    private static int sumFoldRight(MyList<Integer> list) {
        return list.foldRight(0, x -> y -> x + y);
    }

    private static int sumFoldLeft(MyList<Integer> list) {
        return list.foldLeft(0, x -> y -> x + y);
    }

    private static int productFoldLeft(MyList<Integer> list) {
        return list.foldLeft(1, x -> y -> x * y);
    }

    private static int lengthFoldLeft(MyList<Integer> list) {
        return list.foldLeft(0, x -> __ -> x + 1);
    }

    private static <A> MyList<A> concatRightFold(MyList<A> list1, MyList<A> list2) {
        return list1.foldRight(list2, x -> y -> y.cons(x));
    }

    private static <A> MyList<A> concatLeftFold(MyList<A> list1, MyList<A> list2) {
        return list2.foldLeft(list1.reverse(), acc -> item -> acc.cons(item));
    }

    private static <T> MyList<T> reverseFoldLeft(MyList<T> list) {
        return list.foldLeft(MyList.empty(), x -> x::cons);
    }

    public static MyList<Integer> times3(MyList<Integer> list) {
        return list.foldRight(MyList.empty(), x -> y -> y.cons(x * 3));
    }

    public static MyList<String> toString(MyList<Double> list) {
        return list.foldRight(MyList.empty(), x -> y -> y.cons(Double.toString(x)));
    }

    public static <A, B, C> MyList<C> product(MyList<A> listA, MyList<B> listB, Function<A, Function<B, C>> f) {
        return listA.flatMap(a -> listB.map(f.apply(a)));
    }

    public static <A, B> Tuple<MyList<A>, MyList<B>> unzip(MyList<Tuple<A, B>> list) {
        return list.foldLeft(new Tuple<>(MyList.empty(), MyList.empty()), x -> y -> new Tuple<>(x.a().cons(y.a()), x.b().cons(y.b())));
    }

    public static String addIs(Integer i, String s) {
        return "(" + i + " + " + s + ")";
    }

    public static MyList<Integer> range(int start, int end) {
        return MyList.unfold(start, x -> x < end
            ? Option.some(new Tuple<>(x, x +1))
            : Option.none());
    }

}
