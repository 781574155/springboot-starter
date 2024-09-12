package com.openai36.aggregation.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class GeneralOperationResult<T> {
    boolean success;
    String message;
    T data;

    public static <T> GeneralOperationResult<T> success() {
        return new GeneralOperationResult<>(true, null, null);
    }

    public static <T> GeneralOperationResult<T> success(T data) {
        return new GeneralOperationResult<>(true, null, data);
    }

    public static GeneralOperationResult<StringResult> success(String message) {
        return new GeneralOperationResult<>(true, null, new StringResult(message));
    }

    public static <T> GeneralOperationResult<T> failure(String message) {
        return new GeneralOperationResult<>(false, message, null);
    }

    public static <T> GeneralOperationResult<T> failureWithNotFound() {
        return new GeneralOperationResult<>(false, "指定资源不存在", null);
    }
}
