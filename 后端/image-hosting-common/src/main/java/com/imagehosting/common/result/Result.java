package com.imagehosting.common.result;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一返回结果
 */
@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    private int code;

    /**
     * 消息
     */
    private String message;

    /**
     * 数据
     */
    private T data;

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 私有构造方法
     */
    private Result() {
    }

    /**
     * 成功返回结果
     *
     * @param <T> 泛型
     * @return 返回结果
     */
    public static <T> Result<T> success() {
        return success(null);
    }

    /**
     * 成功返回结果
     *
     * @param data 数据
     * @param <T>  泛型
     * @return 返回结果
     */
    public static <T> Result<T> success(T data) {
        return success(ResultCode.SUCCESS.getMessage(), data);
    }

    /**
     * 成功返回结果
     *
     * @param message 消息
     * @param data    数据
     * @param <T>     泛型
     * @return 返回结果
     */
    public static <T> Result<T> success(String message, T data) {
        return result(ResultCode.SUCCESS.getCode(), message, data, true);
    }

    /**
     * 失败返回结果
     *
     * @param <T> 泛型
     * @return 返回结果
     */
    public static <T> Result<T> failed() {
        return failed(ResultCode.SYSTEM_ERROR);
    }

    /**
     * 失败返回结果
     *
     * @param resultCode 结果码
     * @param <T>        泛型
     * @return 返回结果
     */
    public static <T> Result<T> failed(IResultCode resultCode) {
        return result(resultCode.getCode(), resultCode.getMessage(), null, false);
    }

    /**
     * 失败返回结果
     *
     * @param message 消息
     * @param <T>     泛型
     * @return 返回结果
     */
    public static <T> Result<T> failed(String message) {
        return result(ResultCode.SYSTEM_ERROR.getCode(), message, null, false);
    }

    /**
     * 失败返回结果
     *
     * @param code    状态码
     * @param message 消息
     * @param <T>     泛型
     * @return 返回结果
     */
    public static <T> Result<T> failed(int code, String message) {
        return result(code, message, null, false);
    }

    /**
     * 参数验证失败返回结果
     *
     * @param message 消息
     * @param <T>     泛型
     * @return 返回结果
     */
    public static <T> Result<T> validateFailed(String message) {
        return result(ResultCode.VALIDATE_FAILED.getCode(), message, null, false);
    }

    /**
     * 返回结果
     *
     * @param code    状态码
     * @param message 消息
     * @param data    数据
     * @param success 是否成功
     * @param <T>     泛型
     * @return 返回结果
     */
    private static <T> Result<T> result(int code, String message, T data, boolean success) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        result.setData(data);
        result.setSuccess(success);
        return result;
    }
} 