package com.deepknow.goodface.portal.controller.common;

import java.io.Serializable;

public class ApiResponse<T> implements Serializable {
    private static final long serialVersionUID = 5452481903363231530L;
    private Integer code;
    private String message;
    private T data;
    private Long timestamp;

    // 默认构造函数
    public ApiResponse() {
        this.timestamp = System.currentTimeMillis();
    }

    // 带参数的构造函数
    public ApiResponse(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    // 成功响应的静态方法
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(200, "success", null);
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "success", data);
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(200, message, data);
    }

    // 失败响应的静态方法
    public static <T> ApiResponse<T> error() {
        return new ApiResponse<>(500, "error", null);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(500, message, null);
    }

    public static <T> ApiResponse<T> error(Integer code, String message) {
        return new ApiResponse<>(code, message, null);
    }

    // Getter和Setter方法
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}