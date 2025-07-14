package com.example.demo.common;  // 放在 common 或 dto 文件夹

import java.time.LocalDateTime;

public class Result<T> {

    private boolean success;    // 请求是否成功
    private String message;     // 请求的消息
    private T data;             // 返回的数据（可以是任意类型）
    private LocalDateTime timestamp; // 响应时间戳

    // 默认构造函数
    public Result() {
        this.timestamp = LocalDateTime.now(); // 默认设置响应时间
    }

    // 带参数构造函数
    public Result(boolean success, String message) {
        this();
        this.success = success;
        this.message = message;
    }

    // 带参数构造函数（带数据）
    public Result(boolean success, String message, T data) {
        this(success, message);
        this.data = data;
    }

    // Getters 和 Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
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

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    // 工具方法
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(true, message, data);
    }

    public static <T> Result<T> success(String message) {
        return new Result<>(true, message);
    }

    public static <T> Result<T> failure(String message) {
        return new Result<>(false, message);
    }

    public static <T> Result<T> failure(String message, T data) {
        return new Result<>(false, message, data);
    }
}
