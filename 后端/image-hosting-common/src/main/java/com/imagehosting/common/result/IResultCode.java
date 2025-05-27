package com.imagehosting.common.result;

/**
 * 结果码接口
 */
public interface IResultCode {
    /**
     * 获取状态码
     *
     * @return 状态码
     */
    int getCode();

    /**
     * 获取消息
     *
     * @return 消息
     */
    String getMessage();
} 