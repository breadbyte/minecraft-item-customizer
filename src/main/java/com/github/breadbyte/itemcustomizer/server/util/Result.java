package com.github.breadbyte.itemcustomizer.server.util;

import java.util.function.Function;

// Can be either a Success<T> or a Failure<E>
public sealed class Result<T, E> {

    // Success Type of Result<T,E>
    public static final class Success<T, E> extends Result<T, E> {
        public final T value;
        
        public Success(T value) {
            this.value = value;
        }
    }

    // Failure Type of Result<T,E>
    public static final class Failure<T, E> extends Result<T, E> {
        public final E error;
        
        public Failure(E error) {
            this.error = error;
        }
    }
    
    public static <T, E> Result<T, E> ok(T value) {
        return new Success<>(value);
    }
    
    public static <T, E> Result<T, E> err(E error) {
        return new Failure<>(error);
    }

    public boolean isOk() {
        return this instanceof Success;
    }

    public boolean isErr() {
        return this instanceof Failure;
    }

    public T unwrap() throws IllegalStateException {
        if (this instanceof Success<T, E> s) {
            return s.value;
        } else if (this instanceof Failure<T, E> f) {
            throw new IllegalStateException("Attempted to unwrap a Failure: " + f.error);
        } else {
            throw new IllegalStateException("Unexpected value: " + this);
        }
    }

    public E unwrapErr() throws IllegalStateException {
        if (this instanceof Failure<T, E> f) {
            return f.error;
        } else if (this instanceof Success<T, E> s) {
            throw new IllegalStateException("Attempted to unwrapErr a Success: " + s.value);
        } else {
            throw new IllegalStateException("Unexpected value: " + this);
        }
    }
}
