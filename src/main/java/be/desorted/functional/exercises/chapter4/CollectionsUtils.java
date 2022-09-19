package be.desorted.functional.exercises.chapter4;

import be.desorted.functional.tailcall.TailCall;
import be.desorted.functional.tailcall.TailCall.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

public class CollectionsUtils {

    public static <T> List<T> empty() {
        return List.of();
    }

    public static <T> List<T> singleton(T t) {
        return List.of(t);
    }

    public static <T> List<T> copyOf(List<T> list) {
        return List.copyOf(list);
    }

    public static <T> List<T> fromElements(T... elements) {
        return List.of(elements);
    }

    public static <T> List<T> unfold(T seed, Function<T, T> f, Function<T, Boolean> p) {
        ArrayList<T> list = new ArrayList<>();
        for (T i = seed; p.apply(i); i = f.apply(i)) {
            list.add(i);
        }
        return list;
    }

    public static List<Integer> range(int start, int end) {
        return rangeTail(List.of(), start, end).getResult();
    }

    private static TailCall<List<Integer>> rangeTail(List<Integer> acc, int start, int end) {
        return start == end
                ? new Result<>(acc)
                : () -> rangeTail(append(acc, start), start + 1, end);
    }

    public static <T> T head(List<T> list) {
        if (list.isEmpty()) {
            throw new IllegalArgumentException();
        }
        return list.get(0);
    }

    public static <T> List<T> tail(List<T> list) {
        if (list.isEmpty()) {
            return null;
        }
        return list.stream()
                .skip(1)
                .toList();
    }

    public static <T> List<T> append(List<T> list, T element) {
        ArrayList<T> newList = new ArrayList<>(list);
        newList.add(element);
        return copyOf(newList);
    }


    public static <T, U> U foldLeft(List<T> elements, U identity, Function<U, Function<T, U>> f) {
        return foldLeftTail(identity, elements, f).getResult();
    }

    private static <T, U> TailCall<U> foldLeftTail(U acc, List<T> elements, Function<U, Function<T, U>> f) {
        return elements.isEmpty()
                ? new Result<>(acc)
                : () -> foldLeftTail(f.apply(acc).apply(head(elements)), tail(elements), f);
    }

    public static <T, U> U foldRight(List<T> elements, U identity, Function<T, Function<U, U>> f) {
        System.out.println("foldRight");
        return foldRightTail(identity, reverse(elements), f).getResult();
    }

    private static <T, U> TailCall<U> foldRightTail(U acc, List<T> elements, Function<T, Function<U, U>> f) {
        System.out.println("foldRightTail");
        return elements.isEmpty()
                ? new Result<>(acc)
                : () -> foldRightTail(f.apply(head(elements)).apply(acc), tail(elements), f);
    }

    private static <T> Function<T, T> compose(List<Function<T, T>> functions) {
        return foldRight(functions, Function.identity(), x -> y -> x.compose(y));
    }

    public static <T> List<T> reverse(List<T> list) {
        return foldLeft(list, empty(), x -> y -> prepend(x, y));
    }

    public static <T> List<T> prepend(List<T> list, T element) {
        return foldLeft(list, singleton(element), x -> y -> append(x, y));
    }

    public static <T, U> List<U> mapLeft(List<T> list, Function<T, U> f) {
        return foldLeft(list, empty(), x -> y -> append(x, f.apply(y)));
    }

    public static <T, U> List<U> mapRight(List<T> list, Function<T, U> f) {
        return foldRight(list, empty(), x -> y -> prepend(y, f.apply(x)));
    }

    public static void main(String[] args) {
//        System.out.println(foldLeft(fromElements(1, 2, 3, 4, 5), "0", x -> y -> addSi(x, y)));
//        System.out.println(foldRight(fromElements(1, 2, 3, 4, 5), "0", x -> y -> addIs(x, y)));
//        System.out.println(reverse((List.of(1, 2, 3, 4, 5))));
//        System.out.println(mapLeft(List.of(1, 2, 3, 4, 5), x -> x + 2));
//        System.out.println(mapRight(List.of(1, 2, 3, 4, 5), x -> x + 2));


        List<Function<Integer, Integer>> list = IntStream.range(0, 3000)
                .mapToObj( __ -> (Function<Integer, Integer>) y -> y + 1)
                .toList();

        System.out.println("start composition");
        Function<Integer, Integer> composedFunction = compose(list);
        System.out.println("stop composition");
        System.out.println(composedFunction.apply(0));
    }

    public static String addSi(String s, Integer i) {
        return "(" + s + " + " + i + ")";
    }

    public static String addIs(Integer i, String s) {
        return "(" + i + " + " + s + ")";
    }
}
