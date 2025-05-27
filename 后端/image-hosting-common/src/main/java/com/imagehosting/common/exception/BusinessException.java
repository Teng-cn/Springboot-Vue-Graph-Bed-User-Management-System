package com.imagehosting.common.exception;

import com.imagehosting.common.result.IResultCode;
import com.imagehosting.common.result.ResultCode;
import lombok.Getter;

/**
 * 业务异常
 */
@Getter
public class BusinessException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private final IResultCode resultCode;

    public BusinessException() {
        super(ResultCode.FAILED.getMessage());
        this.resultCode = ResultCode.FAILED;
    }

    public BusinessException(String message) {
        super(message);
        this.resultCode = ResultCode.FAILED;
    }

    public BusinessException(Throwable cause) {
        super(cause);
        this.resultCode = ResultCode.FAILED;
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.resultCode = ResultCode.FAILED;
    }

    public BusinessException(IResultCode resultCode) {
        super(resultCode.getMessage());
        this.resultCode = resultCode;
    }

    public BusinessException(IResultCode resultCode, Throwable cause) {
        super(resultCode.getMessage(), cause);
        this.resultCode = resultCode;
    }

    /**
     * 构造方法
     *
     * @param resultCode 错误码
     * @param message    错误信息
     */
    public BusinessException(IResultCode resultCode, String message) {
        super(message);
        this.resultCode = resultCode;
    }

    /**
     * 构造方法
     *
     * @param code    错误码
     * @param message 错误信息
     */
    public BusinessException(int code, String message) {
        super(message);
        this.resultCode = new IResultCode() {
            @Override
            public int getCode() {
                return code;
            }

            @Override
            public String getMessage() {
                return message;
            }
        };
    }

    /**
     * 参数错误
     *
     * @param message 错误信息
     * @return 业务异常
     */
    public static BusinessException paramError(String message) {
        return new BusinessException(ResultCode.PARAM_ERROR, message);
    }

    /**
     * 数据不存在
     *
     * @param message 错误信息
     * @return 业务异常
     */
    public static BusinessException dataNotFound(String message) {
        return new BusinessException(ResultCode.DATA_NOT_FOUND, message);
    }

    /**
     * 无权限
     *
     * @param message 错误信息
     * @return 业务异常
     */
    public static BusinessException forbidden(String message) {
        return new BusinessException(ResultCode.FORBIDDEN, message);
    }

    /**
     * 未授权
     *
     * @param message 错误信息
     * @return 业务异常
     */
    public static BusinessException unauthorized(String message) {
        return new BusinessException(ResultCode.UNAUTHORIZED, message);
    }

    /**
     * 系统错误
     *
     * @param message 错误信息
     * @return 业务异常
     */
    public static BusinessException systemError(String message) {
        return new BusinessException(ResultCode.SYSTEM_ERROR, message);
    }
} 