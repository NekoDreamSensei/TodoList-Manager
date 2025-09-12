package com.todolist.dto;

import com.todolist.entity.User;

/**
 * 认证响应DTO
 * 
 * @author todolist
 * @version 1.0
 * @since 2025-09-12
 */
public class AuthResponse {
    
    private User user;
    private String token;
    private String message;
    
    public AuthResponse() {}
    
    public AuthResponse(User user, String token, String message) {
        this.user = user;
        this.token = token;
        this.message = message;
    }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
