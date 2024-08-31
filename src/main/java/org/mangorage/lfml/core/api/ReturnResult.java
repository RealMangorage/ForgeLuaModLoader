package org.mangorage.lfml.core.api;

public record ReturnResult<T>(ReturnType returnType, T returnObject) {
    private static final ReturnResult<?> EMPTY = new ReturnResult<>(null, null);

    @SuppressWarnings("unchecked")
    public static <T> ReturnResult<T> empty() {
        return (ReturnResult<T>) EMPTY;
    }
}
