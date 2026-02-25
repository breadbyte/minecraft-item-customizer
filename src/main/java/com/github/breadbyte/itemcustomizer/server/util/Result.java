package com.github.breadbyte.itemcustomizer.server.util;

// Can be either a Success<T> or a Failure<E>
public sealed class Result<T> {

    // Success Type of Result<T,E>
    public static final class Success<T> extends Result<T> {
        public final T value;
        public final String message;

        public Success(T value, String message) {
            this.value = value;
            this.message = message;
        }

        public Success(T value) {
            this.value = value;
            this.message = null;
        }
    }

    // Failure Type of Result<T,E>
    public static final class Failure<T> extends Result<T> {
        public final Reason reason;
        
        public Failure(Reason reason) {
            this.reason = reason;
        }
    }
    
    public static <T> Result<T> ok(T value, String message) {
        return new Success<>(value, message);
    }

    public static <T> Result<T> ok(T value) {
        return new Success<>(value);
    }

    public static <T> Result<T> ok() {
        return new Success<>(null);
    }
    
    public static <T> Result<T> err(Reason error) {
        return new Failure<>(error);
    }

    public String getMessage() {
        if (this instanceof Success) {
            return ((Success<T>) this).message;
        } else {
            return ((Failure<T>) this).reason.getMessage();
        }
    }

    public boolean isOk() {
        return this instanceof Success;
    }

    public boolean isErr() {
        return this instanceof Failure;
    }

    public T unwrap() throws IllegalStateException {
        if (this instanceof Success<T> s) {
            return s.value;
        } else if (this instanceof Failure<T> f) {
            throw new IllegalStateException("Attempted to unwrap a Failure: " + f.reason);
        } else {
            throw new IllegalStateException("Unexpected value: " + this);
        }
    }

    public Reason unwrapErr() throws IllegalStateException {
        if (this instanceof Failure<T> f) {
            return f.reason;
        } else if (this instanceof Success<T> s) {
            throw new IllegalStateException("Attempted to unwrapErr a Success: " + s.value);
        } else {
            throw new IllegalStateException("Unexpected value: " + this);
        }
    }
}
