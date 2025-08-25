package com.github.breadbyte.itemcustomizer.server.data;

public record OperationResult(boolean ok, String details, int cost) {

    public static OperationResult ok(String s) {
        return new OperationResult(true, s, 0);
    }

    public static OperationResult ok(String s, int cost) {
        return new OperationResult(true, s, cost);
    }

    public static OperationResult fail(String details) {
        return new OperationResult(false, details, 0);
    }
}
