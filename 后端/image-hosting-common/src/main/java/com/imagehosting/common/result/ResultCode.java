package com.imagehosting.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 结果码枚举
 */
@Getter
@AllArgsConstructor
public enum ResultCode implements IResultCode {

    /**
     * 操作成功
     */
    SUCCESS(200, "操作成功"),

    /**
     * 操作失败
     */
    FAILED(400, "操作失败"),

    /**
     * 参数错误
     */
    PARAM_ERROR(400, "参数错误"),

    /**
     * 参数验证失败
     */
    VALIDATE_FAILED(400, "参数验证失败"),

    /**
     * 未授权
     */
    UNAUTHORIZED(401, "未授权"),

    /**
     * 禁止访问
     */
    FORBIDDEN(403, "禁止访问"),

    /**
     * 数据不存在
     */
    DATA_NOT_FOUND(404, "数据不存在"),

    /**
     * 系统错误
     */
    SYSTEM_ERROR(500, "系统错误");

    /**
     * 状态码
     */
    private final int code;

    /**
     * 消息
     */
    private final String message;

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
} 