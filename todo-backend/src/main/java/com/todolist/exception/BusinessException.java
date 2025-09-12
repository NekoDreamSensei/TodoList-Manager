package com.todolist.exception;

/**
 * 业务异常类
 * 
 * 用于处理所有业务逻辑相关的异常，通过错误码和错误信息来区分不同类型的错误
 * 
 * @author todolist
 * @version 1.0
 * @since 2025-09-12
 */
public class BusinessException extends RuntimeException {
    
    private final String errorCode;
    private final int httpStatus;
    
    public BusinessException(String errorCode, String message, int httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }
    
    public BusinessException(String errorCode, String message) {
        this(errorCode, message, 400); // 默认400状态码
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public int getHttpStatus() {
        return httpStatus;
    }
}
