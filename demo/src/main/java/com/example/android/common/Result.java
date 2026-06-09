package com.example.android.common;

//统一响应类
public class Result<T> {
    private int code;
    private String message;
    private T data;

    //成功响应
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("success");
        result.setData(data);
        return result;
    }


    //成功响应自定义
    public static <T> Result<T> success(String message, T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    //失败响应
    public static <T> Result<T> error(int code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    //失败响应
    public static <T> Result<T> error(String message) {
        return error(500, message);
    }

    //参数错误
    public static <T> Result<T> badRequest(String message) {
        return error(400, message);
    }

    //未找到
    public static <T> Result<T> notFound(String message) {
        return error(404, message);
    }

    //未授权
    public static <T> Result<T> unauthorized(String message) {
        return error(401, message);
    }

    public int getCode() {
        return code;
    }


    public void setCode(int code) {
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
}