package com.todolist.service;

import com.todolist.entity.User;
import com.todolist.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import com.todolist.dto.*;
import com.todolist.util.JwtUtil;
import com.todolist.exception.BusinessException;
import com.todolist.exception.ErrorCodes;

/**
 * 用户服务类
 * 
 * 负责处理用户相关的业务逻辑，包括用户的增删改查操作
 * 作为Controller和Repository之间的业务逻辑层
 * 
 * @author todolist
 * @version 1.0
 * @since 2025-09-12
 */
@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 获取所有用户
     * 
     * @return List<User> 所有用户的列表
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    /**
     * 根据ID获取用户
     * 
     * @param id 用户ID
     * @return Optional<User> 用户信息，如果不存在则返回空
     */
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    /**
     * 根据用户名获取用户
     * 
     * @param username 用户名
     * @return Optional<User> 用户信息，如果不存在则返回空
     */
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    /**
     * 创建新用户
     * 
     * @param user 要创建的用户信息
     * @return User 创建成功的用户信息（包含生成的ID）
     */
    public User createUser(User user) {
        return userRepository.save(user);
    }
    
    /**
     * 更新用户信息
     * 
     * @param user 要更新的用户信息
     * @return User 更新后的用户信息
     */
    public User updateUser(User user) {
        return userRepository.save(user);
    }
    
    /**
     * 删除用户
     * 
     * @param id 要删除的用户ID
     */
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    
    /**
     * 检查用户名是否存在
     * 
     * @param username 要检查的用户名
     * @return boolean 如果用户名存在返回true，否则返回false
     */
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * 用户注册
     * 
     * @param registerRequest 注册请求
     * @return AuthResponse 认证响应
     */
    public AuthResponse register(RegisterRequest registerRequest) {
        // 验证用户名长度
        if (registerRequest.getUsername().length() < 3) {
            throw new BusinessException(ErrorCodes.USER_VALIDATION_FAILED, "用户名长度至少3个字符", 400);
        }
        
        if (registerRequest.getUsername().length() > 20) {
            throw new BusinessException(ErrorCodes.USER_VALIDATION_FAILED, "用户名长度不能超过20个字符", 400);
        }
        
        // 验证密码长度
        if (registerRequest.getPassword().length() < 6) {
            throw new BusinessException(ErrorCodes.USER_VALIDATION_FAILED, "密码长度至少6个字符", 400);
        }
        
        // 检查用户名是否已存在
        if (existsByUsername(registerRequest.getUsername())) {
            throw new BusinessException(ErrorCodes.USER_ALREADY_EXISTS, 
                "用户名 '" + registerRequest.getUsername() + "' 已存在，请选择其他用户名", 409);
        }
        
        // 创建新用户
        User user = new User(registerRequest.getUsername(), registerRequest.getPassword());
        User savedUser = createUser(user);
        
        // 生成JWT token
        String token = jwtUtil.generateToken(savedUser.getUsername());
        
        return new AuthResponse(savedUser, token, "注册成功！欢迎 " + savedUser.getUsername());
    }

    /**
     * 用户登录
     * 
     * @param loginRequest 登录请求
     * @return AuthResponse 认证响应
     */
    public AuthResponse login(LoginRequest loginRequest) {
        // 验证输入
        if (loginRequest.getUsername() == null || loginRequest.getUsername().trim().isEmpty()) {
            throw new BusinessException(ErrorCodes.USER_VALIDATION_FAILED, "用户名不能为空", 400);
        }
        
        if (loginRequest.getPassword() == null || loginRequest.getPassword().trim().isEmpty()) {
            throw new BusinessException(ErrorCodes.USER_VALIDATION_FAILED, "密码不能为空", 400);
        }
        
        // 根据用户名查找用户
        Optional<User> userOpt = getUserByUsername(loginRequest.getUsername().trim());
        if (userOpt.isEmpty()) {
            throw new BusinessException(ErrorCodes.INVALID_CREDENTIALS, "用户名或密码错误，请检查后重试", 401);
        }
        
        User user = userOpt.get();
        
        // 验证密码
        if (!user.getPassword().equals(loginRequest.getPassword())) {
            throw new BusinessException(ErrorCodes.INVALID_CREDENTIALS, "用户名或密码错误，请检查后重试", 401);
        }
        
        // 生成JWT token
        String token = jwtUtil.generateToken(user.getUsername());
        
        return new AuthResponse(user, token, "登录成功！欢迎回来 " + user.getUsername());
    }
}