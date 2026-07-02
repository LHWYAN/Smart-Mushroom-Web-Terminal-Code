package com.smartmushroom.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private int code;
    private T data;
    private String message;
    private Long total;

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(0, data, "success", null);
    }

    public static <T> ApiResponse<T> ok(T data, long total) {
        return new ApiResponse<>(0, data, "success", total);
    }

    public static <T> ApiResponse<T> fail(String message) {
        return new ApiResponse<>(-1, null, message, null);
    }
}
