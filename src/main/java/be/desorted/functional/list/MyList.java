package be.desorted.functional.list;

import be.desorted.functional.result.Result;
import be.desorted.functional.option.Option;
import be.desorted.functional.tailcall.TailCall;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public interface MyList<A> {

	static <T> MyList<T> empty() {
		return new Empty<>();
	}

	static <T> MyList<T> list(T... t) {
		return IntStream.range(0, t.length)
			.mapToObj(i -> t[t.length - (i + 1)])
			.reduce(empty(), (a, b) -> new ListImpl<>(b, a), (a, b) -> {
				throw new IllegalStateException();
			});
	}

	A head();

	Option<A> findHead();

	MyList<A> tail();

	default Option<A> findLast() {
		return foldLeft(Option.none(), x -> Option::some);
	}

	boolean isEmpty();

	MyList<A> setHead(A a);

	default MyList<A> cons(A a) {
		return new ListImpl<>(a, this);
	}

	default MyList<A> drop(int n) {
		return drop(this, n).getResult();
	}

	default MyList<A> dropWhile(Predicate<A> predicate) {
		return dropWhile(this, predicate).getResult();
	}

	private TailCall<MyList<A>> dropWhile(MyList<A> acc, Predicate<A> predicate) {
		return acc.isEmpty() || !predicate.test(acc.head())
			? new TailCall.Result<>(acc)
			: dropWhile(acc.tail(), predicate);
	}

	private TailCall<MyList<A>> drop(MyList<A> acc, int n) {
		return n == 0 || acc.isEmpty()
			? new TailCall.Result<>(acc)
			: () -> drop(acc.tail(), n - 1);
	}

	default MyList<A> reverse() {
		return reverse(empty(), this).getResult();
	}

	private static <T> TailCall<MyList<T>> reverse(MyList<T> acc, MyList<T> list) {
		return list.isEmpty()
			? new TailCall.Result<>(acc)
			: () -> reverse(acc.cons(list.head()), list.tail());
	}

	private MyList<A> init() {
		return reverse().tail().reverse();
	}

	default <B> B foldLeft(B identity, Function<B, Function<A, B>> f) {
		return foldLeftTail(this, identity, f).getResult();
	}

	private static <T, S> TailCall<S> foldLeftTail(MyList<T> list, S acc, Function<S, Function<T, S>> f) {
		return list.isEmpty()
			? new TailCall.Result<>(acc)
			: () -> foldLeftTail(list.tail(), f.apply(acc).apply(list.head()), f);
	}

	default <B> B foldLeft(B identity, B zero, Function<B, Function<A, B>> f) {
		return foldLeftTail(this, identity, zero, f).getResult();
	}

	private static <T, S> TailCall<S> foldLeftTail(MyList<T> list, S acc, S zero, Function<S, Function<T, S>> f) {
		return list.isEmpty() || acc.equals(zero)
			? new TailCall.Result<>(acc)
			: () -> foldLeftTail(list.tail(), f.apply(acc).apply(list.head()), zero, f);
	}

	default <B> B foldRight(B identity, Function<A, Function<B, B>> f) {
		return foldRightTail(this.reverse(), identity, f).getResult();

	}

	private static <T, S> TailCall<S> foldRightTail(MyList<T> list, S identity, Function<T, Function<S, S>> f) {
		return list.isEmpty()
			? new TailCall.Result<>(identity)
			: () -> foldRightTail(list.tail(), f.apply(list.head()).apply(identity), f);

	}


	default String toString2() {
		return toString("", this).getResult();
	}

	private TailCall<String> toString(String acc, MyList<A> myList) {
		return myList.isEmpty()
			? new TailCall.Result<>(String.format("[%sNIL]", acc))
			: () -> toString(acc + myList.head() + ", ", myList.tail());
	}

	default <B> MyList<B> map(Function<A, B> f) {
		return foldRight(empty(), h -> t -> t.cons(f.apply(h)));
	}

	default MyList<A> filter(Predicate<A> p) {
		return foldRight(empty(), h -> t -> p.test(h) ? t.cons(h) : t);
	}

	default <B> MyList<B> flatMap(Function<A, MyList<B>> f) {
		return foldRight(empty(), h -> t -> f.apply(h).concat(t));
	}

	default MyList<A> concat(MyList<A> list2) {
		return this.foldRight(list2, x -> y -> y.cons(x));
	}

	int length();

	default <X, Y> Tuple<MyList<X>, MyList<Y>> unzip(Function<A, Tuple<X, Y>> f) {
		return this.foldLeft(new Tuple<>(MyList.empty(), MyList.empty()), x -> y -> {
			Tuple<X, Y> result = f.apply(y);
			return new Tuple<>(x.a().cons(result.a()), x.b().cons(result.b()));
		});
	}

	default Option<A> get(int i) {
		return i < 0 || i > length()
			? Option.none()
			: Option.some(getTail(i, this).getResult());
	}

	private TailCall<A> getTail(int i, MyList<A> list) {
		return i == 0
			? TailCall.result(list.head())
			: () -> getTail(i - 1, list.tail());
	}

	default A getLeft(int i, MyList<A> list) {
		return list.foldLeft(new Tuple<>((A) null, i), x -> y ->
				x.b() < 0
					? x
					: new Tuple<>(y, x.b() - 1))
			.a();
	}

	default <B> Map<B, MyList<A>> groupBy(Function<A, B> f) {
		return this.foldLeft(new HashMap<>(), m -> a -> {
			B b = f.apply(a);
			m.put(b, m.getOrDefault(b, MyList.empty()).cons(a));
			return m;
		});
	}

	default boolean anyMatch(Predicate<A> predicate) {
		return foldLeft(false, true, x -> y -> x || predicate.test(y));
	}

	default boolean allMatch(Predicate<A> predicate) {
		return foldLeft(true, false, x -> y -> x && predicate.test(y));
	}

	default MyList<MyList<A>> divide(int depth) {
		return this.isEmpty()
			? list(this)
			: divide(depth, list(this));
	}

	default MyList<MyList<A>> divide(int depth, MyList<MyList<A>> list) {
		return depth == 0 || list.head().length() > 2
			? list
			: divide(depth - 1, list.flatMap(aList -> aList.splitAt(aList.length() / 2)));
	}

	default MyList<MyList<A>> splitAt(int i) {
		return splitAtTail(i, MyList.empty(), this).getResult();
	}

	private static <X> TailCall<MyList<MyList<X>>> splitAtTail(int i, MyList<X> acc, MyList<X> list) {
		return i == 0 || list.isEmpty()
			? TailCall.result(MyList.list(acc, list))
			: () -> splitAtTail(i - 1, acc.cons(list.head()), list.tail());
	}

	static <X> MyList<X> flatten(MyList<MyList<X>> list) {
		return list.foldRight(MyList.empty(), x -> y -> x.concat(y));
	}

	static <X> MyList<X> flattenResult(MyList<Result<X>> list) {
		return flatten(list.foldLeft(MyList.empty(), x -> y -> x.cons(y.map(MyList::list).getOrElse(MyList.empty()))));
	}

	static <X> Result<MyList<X>> sequence(MyList<Result<X>> list) {
		return list.foldLeft(Result.of(MyList.empty()), x -> y -> Result.map2(x, y, a -> b -> a.cons(b)));
	}

	static <X> Result<MyList<X>> sequence2(MyList<Result<X>> list) {
		return traverse(list, x -> x);
	}

	static <X, Y> Result<MyList<Y>> traverse(MyList<X> list, Function<X, Result<Y>> f) {
		return list.foldLeft(Result.of(MyList.empty()), x -> y -> Result.map2(x, f.apply(y), x2 -> y2 -> x2.cons(y2)));
	}

	static <X, Y, Z> MyList<Z> zip(MyList<X> listx, MyList<Y> listy, Function<X, Function<Y, Z>> f) {
		return zipTail(MyList.empty(), listx, listy, f).getResult();
	}

	static <X, Y, Z> TailCall<MyList<Z>> zipTail(MyList<Z> acc, MyList<X> listX, MyList<Y> listY, Function<X, Function<Y, Z>> f) {
		return (listX.isEmpty() || listY.isEmpty())
			? new TailCall.Result<>(acc)
			: () -> zipTail(acc.cons(f.apply(listX.head()).apply(listY.head())), listX.tail(), listY.tail(), f);
	}

	static <X> boolean hasSubList(MyList<X> list, MyList<X> subList) {
		return hasSubListTail(list, subList).getResult();
	}

	static <X> TailCall<Boolean> hasSubListTail(MyList<X> list, MyList<X> subList) {
		return list.isEmpty() ? TailCall.result(false)
			: startsWith(list, subList) ? TailCall.result(true)
			: () -> startsWithTail(list.tail(), subList);
	}

	static <X> boolean startsWith(MyList<X> list, MyList<X> subList) {
		return startsWithTail(list, subList).getResult();
	}

	static <X> TailCall<Boolean> startsWithTail(MyList<X> list, MyList<X> subList) {
		return subList.isEmpty() ? TailCall.result(true)
			: list.isEmpty() ? TailCall.result(false)
			: list.head().equals(subList.head()) ? () -> startsWithTail(list.tail(), subList.tail())
			: TailCall.result(false);
	}

	static <X, Y> MyList<Y> unfold(X x, Function<X, Option<Tuple<Y, X>>> f) {
		return unfoldTail(MyList.empty(), x, f).getResult().reverse();
	}

	private static <X, Y> TailCall<MyList<Y>> unfoldTail(MyList<Y> acc, X x, Function<X, Option<Tuple<Y, X>>> f) {
		Option<Tuple<Y, X>> tuple = f.apply(x);
		return () -> tuple.map(t -> unfoldTail(acc.cons(t.a()), t.b(), f)).orElse(() -> TailCall.result(acc));
	}

}

class ListImpl<A> implements MyList<A> {

	private A a;
	private MyList<A> tail;
	private int length;

	public ListImpl(A a, MyList<A> tail) {
		this(a, tail, tail.length() + 1);
	}

	private ListImpl(A a, MyList<A> tail, int length) {
		this.a = a;
		this.tail = tail;
		this.length = length;
	}

	@Override
	public A head() {
		return a;
	}

	@Override
	public Option<A> findHead() {
		return Option.some(a);
	}

	@Override
	public MyList<A> tail() {
		return tail;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public MyList<A> setHead(A a) {
		return new ListImpl<>(a, tail());
	}

	@Override
	public int length() {
		return tail().length();
	}

}

class Empty<A> implements MyList<A> {

	@Override
	public A head() {
		throw new IllegalStateException();
	}

	@Override
	public Option<A> findHead() {
		return Option.none();
	}

	@Override
	public MyList<A> tail() {
		throw new IllegalStateException();
	}

	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	public MyList<A> setHead(A a) {
		throw new IllegalStateException();
	}

	@Override
	public MyList<A> drop(int n) {
		return this;
	}

	@Override
	public String toString2() {
		return "[NIL]";
	}

	@Override
	public int length() {
		return 0;
	}

}

