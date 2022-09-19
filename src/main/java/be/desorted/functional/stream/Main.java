package be.desorted.functional.stream;

public class Main {

	public static void main(String[] args) {
		System.out.println(Stream.from(1).take(10).toList().toString2());
		System.out.println(Stream.from(1).take(10).filter(i -> i > 5).toList().toString2());
		System.out.println(Stream.fibonacci().take(20).toList().toString2());

	}
}
