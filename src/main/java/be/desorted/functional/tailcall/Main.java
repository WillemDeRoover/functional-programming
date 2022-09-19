package be.desorted.functional.tailcall;

import be.desorted.functional.exercises.chapter4.CollectionsUtils;
import be.desorted.functional.tailcall.TailCall.Result;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static be.desorted.functional.exercises.chapter3.CollectionsUtils.append;


public class Main {

    public static void main(String[] args) {
        System.out.println(add(5, 10));
        System.out.println(IntStream.range(0, 50)
                .boxed()
                .map(Main::fibonacci)
                .map(Object::toString)
                .collect(Collectors.joining(", ")));

        System.out.println(CollectionsUtils.range(0, 5));
        System.out.println(fibonacciMemoized(25));
    }

    private static Integer add(int a, int b) {
        return addRec(a, b).getResult();
    }

    private static TailCall<Integer> addRec(int current, int result) {
        return current == 0 ?
                new Result<>(result) :
                () -> addRec(current - 1, result + 1);
    }

    private static BigInteger fibonacci(int number) {
        return fibonacciTail(BigInteger.ZERO, BigInteger.ONE, number).getResult();
    }

    private static TailCall<BigInteger> fibonacciTail(BigInteger acc1, BigInteger acc2, int x) {
        if (x==0) {
            return new Result<>(BigInteger.ONE);
        } else if ( x == 1) {
            return new Result<>(acc1.add(acc2));
        } else {
            return () -> fibonacciTail(acc2, acc1.add(acc2), x - 1);
        }
     }

     private static String fibonacciMemoized(int number) {
         List<BigInteger> result = fibonacciMemoizedTail(List.of(), BigInteger.ZERO, BigInteger.ONE, number).getResult();
         return result.stream()
                 .map(Object::toString)
                 .collect(Collectors.joining(", "));
     }

     private static TailCall<List<BigInteger>> fibonacciMemoizedTail(List<BigInteger> list, BigInteger acc1, BigInteger acc2, int x) {
        if(x==0) {
            return new Result<>(append(list, BigInteger.ONE));
        } else if(x==1) {
            return new Result<>(append(list, acc1.add(acc2)));
        } else {
            return () -> fibonacciMemoizedTail(append(list, acc1.add(acc2)), acc2, acc1.add(acc2), x - 1);
         }
     }
}


