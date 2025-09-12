package com.todolist.dto;

/**
 * 统一API响应类
 * 
 * 用于统一API响应格式，包含成功和错误两种情况
 * 
 * @author todolist
 * @version 1.0
 * @since 2025-09-12
 */
public class ApiResponse<T> {
    
    private boolean success;
    private String message;
    private String errorCode;
    private T data;
    
    // 成功响应构造函数
    public static <T> ApiResponse<T> success(T data, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = true;
        response.data = data;
        response.message = message;
        return response;
    }
    
    public static <T> ApiResponse<T> success(T data) {
        return success(data, "操作成功");
    }
    
    // 错误响应构造函数
    public static <T> ApiResponse<T> error(String errorCode, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = false;
        response.errorCode = errorCode;
        response.message = message;
        return response;
    }
    
    // Getter和Setter
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
    
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
}
