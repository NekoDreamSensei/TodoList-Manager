package com.todolist.exception;

/**
 * 错误码常量类
 * 
 * 统一管理所有错误码，便于维护和扩展
 * 
 * @author todolist
 * @version 1.0
 * @since 2025-09-12
 */
public class ErrorCodes {
    
    // 用户相关错误 (1000-1999)
    public static final String USER_NOT_FOUND = "USER_001";
    public static final String USER_ALREADY_EXISTS = "USER_002";
    public static final String INVALID_CREDENTIALS = "USER_003";
    public static final String USER_VALIDATION_FAILED = "USER_004";
    
    // 专题相关错误 (2000-2999)
    public static final String TOPIC_NOT_FOUND = "TOPIC_001";
    public static final String TOPIC_ALREADY_EXISTS = "TOPIC_002";
    public static final String TOPIC_VALIDATION_FAILED = "TOPIC_003";
    
    // 任务相关错误 (3000-3999)
    public static final String TASK_NOT_FOUND = "TASK_001";
    public static final String TASK_ALREADY_EXISTS = "TASK_002";
    public static final String TASK_VALIDATION_FAILED = "TASK_003";
    
    // 待办相关错误 (4000-4999)
    public static final String TODO_NOT_FOUND = "TODO_001";
    public static final String TODO_ALREADY_EXISTS = "TODO_002";
    public static final String TODO_VALIDATION_FAILED = "TODO_003";
    
    // 系统相关错误 (5000-5999)
    public static final String SYSTEM_ERROR = "SYS_001";
    public static final String DATABASE_ERROR = "SYS_002";
    public static final String NETWORK_ERROR = "SYS_003";
}
