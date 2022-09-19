package be.desorted.functional.stream;

import be.desorted.functional.list.MyList;
import be.desorted.functional.list.Tuple;
import be.desorted.functional.option.Option;
import be.desorted.functional.tailcall.TailCall;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface Stream<A> {

	Stream EMPTY = new Empty<>();

	static <X> Stream<X> empty() {
		return EMPTY;
	}

	A head();

	Option<A> findHead();

	default Option<A> findHeadRightFold() {
		return foldRight(Option::none, h -> __ -> Option.some(h));
	}

	Stream<A> tail();

	boolean isEmpty();

	default MyList<A> toList() {
		return toListTail(MyList.empty(), this).getResult().reverse();
	}

	private static <X> TailCall<MyList<X>> toListTail(MyList<X> acc, Stream<X> curr) {
		return curr.isEmpty()
			? TailCall.result(acc)
			: () -> toListTail(acc.cons(curr.head()), curr.tail());
	}

	Stream<A> take(int n);

	Stream<A> takeWhile(Predicate<A> predicate);

	default Stream<A> takeWhileRightFold(Predicate<A> predicate) {
		return foldRight(Stream::empty, h -> a -> predicate.test(h) ? new Cons<A>(h, a) : empty());
	}

	default Stream<A> drop(int n) {
		return dropTail(n, this).getResult();
	}

	private static <X> TailCall<Stream<X>> dropTail(int n, Stream<X> curr) {
		return curr.isEmpty() || n == 0
			? TailCall.result(empty())
			: () -> dropTail(n - 1, curr.tail());
	}

	default Stream<A> dropWhile(Predicate<A> predicate) {
		return dropWhileTail(predicate, this).getResult();
	}

	private static <X> TailCall<Stream<X>> dropWhileTail(Predicate<X> predicate, Stream<X> curr) {
		return curr.isEmpty() || !predicate.test(curr.head())
			? TailCall.result(empty())
			: () -> dropWhileTail(predicate, curr.tail());
	}

	default boolean exists(Predicate<A> predicate) {
		return existsTail(predicate, this).getResult();
	}

	private static <X> TailCall<Boolean> existsTail(Predicate<X> predicate, Stream<X> curr) {
		return curr.isEmpty()
			? TailCall.result(false)
			: predicate.test(curr.head())
			? TailCall.result(true)
			: () -> existsTail(predicate, curr.tail());
	}

	//optimization
	private static <X> TailCall<Boolean> existsTail2(Predicate<X> predicate, Stream<X> curr) {
		return curr.isEmpty() || predicate.test(curr.head())
			? TailCall.result(!curr.isEmpty())
			: () -> existsTail(predicate, curr.tail());
	}

	<B> B foldRight(Supplier<B> identitySupplier, Function<A, Function<Supplier<B>, B>> f);

	default <B> Stream<B> map(Function<A, B> f) {
		return foldRight(Stream::empty, h -> a -> new Cons<>(f.apply(h), a));
	}

	default Stream<A> filter(Predicate<A> predicate) {
		return foldRight(Stream::empty, h -> a -> predicate.test(h) ? new Cons<>(h, a) : a.get());
	}

	default Stream<A> append(Supplier<Stream<A>> s) {
		return foldRight(s, h -> a -> new Cons<>(h, a));
	}

	default <B> Stream<B> flatMap(Function<A, Stream<B>> f) {
		return foldRight(Stream::empty, h -> a -> f.apply(h).append(a));
	}

	default Option<A> find(Predicate<A> predicate) {
		return filter(predicate).findHead();
	}

	static Stream<Integer> from(int i) {
//		return new Cons<>(i, () -> from(i + 1));
		return iterate(i, j -> j + 1);
//		return unfold(i, j -> Option.some(new Tuple<>(j, j+1)));
	}

	static <X> Stream<X> repeat(X x) {
//		return new Cons<>(x, () -> repeat(x));
		return iterate(x, Function.identity());
	}
	static <X> Stream<X> iterate(X seed, Function<X, X> f) {
		return new Cons<>(seed, () -> iterate(f.apply(seed), f));
	}

	static Stream<Integer> fibonacci() {
		return unfold(new Tuple<>(0, 1), s -> Option.some(new Tuple<>(s.a(), new Tuple<>(s.b(), s.a() + s.b()))));
//		return iterate(new Tuple<>(0, 1), t -> new Tuple<>(t.b(), t.a() + t.b()))
//			.map(Tuple::a);

	}

	private static <X, S> Stream<X> unfold(S s, Function<S, Option<Tuple<X, S>>> f) {
		return f.apply(s)
			.map(t -> (Stream<X>) new Cons<>(t.a(), () -> unfold(t.b(), f))).orElse(empty());
	}
}

class Cons<A> implements Stream<A> {

	private final Supplier<A> headSupplier;
	private A head = null;
	private final Supplier<Stream<A>> tailSupplier;
	private Stream<A> tail = null;

	Cons(A head, Supplier<Stream<A>> tailSupplier) {
		this(() -> head, tailSupplier);
		this.head = head;
	}

	Cons(Supplier<A> headSupplier, Supplier<Stream<A>> tailSupplier) {
		this.headSupplier = headSupplier;
		this.tailSupplier = tailSupplier;
	}

	@Override
	public A head() {
		if (head == null) {
			head = headSupplier.get();
		}
		return head;
	}

	@Override
	public Option<A> findHead() {
		return Option.some(head());
	}

	@Override
	public Stream<A> tail() {
		if (tail == null) {
			tail = tailSupplier.get();
		}
		return tail;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public Stream<A> take(int n) {
		return n == 0
			? EMPTY
			: new Cons<A>(headSupplier, () -> tail().take(n - 1));
	}

	@Override
	public Stream<A> takeWhile(Predicate<A> predicate) {
		return predicate.test(head())
			? new Cons<>(head(), () -> tail().takeWhile(predicate))
			: EMPTY;
	}

	@Override
	public <B> B foldRight(Supplier<B> identitySupplier, Function<A, Function<Supplier<B>, B>> f) {
		return f.apply(head()).apply(() -> tail().foldRight(identitySupplier, f));
	}


}

class Empty<A> implements Stream<A> {

	@Override
	public A head() {
		throw new IllegalArgumentException("Stream is empty");
	}

	@Override
	public Option<A> findHead() {
		return Option.none();
	}

	@Override
	public Stream<A> tail() {
		throw new IllegalArgumentException("Stream is empty");
	}

	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	public Stream<A> take(int n) {
		return this;
	}

	@Override
	public Stream<A> takeWhile(Predicate<A> predicate) {
		return this;
	}

	@Override
	public <B> B foldRight(Supplier<B> identitySupplier, Function<A, Function<Supplier<B>, B>> f) {
		return identitySupplier.get();
	}
}
