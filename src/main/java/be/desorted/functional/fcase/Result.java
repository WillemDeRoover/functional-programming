package be.desorted.functional.fcase;

// ex 3.1
public interface Result<T> {

    void handle(Effect<T> success, Effect<String> failure);

    static <T> Success<T> success(T t) {
        return new Success<>(t);
    }

    static <T> Failure<T> failure(String message) {
        return new Failure<>(message);
    }

    class Success<T> implements Result<T> {
        private final T t;

        public Success(T t) {
            this.t = t;
        }

        @Override
        public void handle(Effect<T> success, Effect<String> failure) {
            success.handle(t);
        }

    }

     class Failure<T> implements Result<T> {
        private final String message;

        public Failure(String message) {
            this.message = message;
        }

        public String message() {
            return message;
        }

        @Override
        public void handle(Effect<T> success, Effect<String> failure) {
            failure.handle(message);
        }
    }
}





