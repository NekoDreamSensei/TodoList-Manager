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
import com.todolist.exception.ApiResponse;  // 明确添加这行
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
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
        logger.info("=== 用户注册开始 ===");
        logger.info("注册用户名: {}", registerRequest.getUsername());
        
        // 验证用户名长度
        if (registerRequest.getUsername().length() < 3) {
            logger.warn("注册失败: 用户名长度不足 - {}", registerRequest.getUsername());
            throw new BusinessException(ErrorCodes.USER_VALIDATION_FAILED, "用户名长度至少3个字符", 400);
        }
        
        if (registerRequest.getUsername().length() > 20) {
            logger.warn("注册失败: 用户名长度超限 - {}", registerRequest.getUsername());
            throw new BusinessException(ErrorCodes.USER_VALIDATION_FAILED, "用户名长度不能超过20个字符", 400);
        }
        
        // 验证密码长度
        if (registerRequest.getPassword().length() < 6) {
            logger.warn("注册失败: 密码长度不足 - 用户: {}", registerRequest.getUsername());
            throw new BusinessException(ErrorCodes.USER_VALIDATION_FAILED, "密码长度至少6个字符", 400);
        }
        
        logger.info("开始检查用户名是否已存在: {}", registerRequest.getUsername());
        
        // 检查用户名是否已存在
        if (existsByUsername(registerRequest.getUsername())) {
            logger.warn("注册失败: 用户名已存在 - {}", registerRequest.getUsername());
            throw new BusinessException(ErrorCodes.USER_ALREADY_EXISTS, 
                "用户名 '" + registerRequest.getUsername() + "' 已存在，请选择其他用户名", 409);
        }
        
        logger.info("用户名检查通过，开始创建新用户");
        
        // 创建新用户
        User user = new User(registerRequest.getUsername(), registerRequest.getPassword());
        User savedUser = createUser(user);
        logger.info("新用户创建成功: ID={}, 用户名={}", savedUser.getId(), savedUser.getUsername());
        
        // 生成JWT token
        String token = jwtUtil.generateToken(savedUser.getUsername());
        logger.info("JWT token生成成功，长度: {}", token.length());
        
        logger.info("=== 用户注册成功 ===");
        logger.info("新用户: {}, ID: {}, Token: {}", savedUser.getUsername(), savedUser.getId(), token.substring(0, 20) + "...");
        
        return new AuthResponse(savedUser, token, "注册成功！欢迎 " + savedUser.getUsername());
    }

    /**
     * 用户登录
     * 
     * @param loginRequest 登录请求
     * @return AuthResponse 认证响应
     */
    public AuthResponse login(LoginRequest loginRequest) {
        logger.info("=== 用户登录开始 ===");
        logger.info("登录用户名: {}", loginRequest.getUsername());
        
        // 验证输入
        if (loginRequest.getUsername() == null || loginRequest.getUsername().trim().isEmpty()) {
            logger.warn("登录失败: 用户名为空");
            throw new BusinessException(ErrorCodes.USER_VALIDATION_FAILED, "用户名不能为空", 400);
        }
        
        if (loginRequest.getPassword() == null || loginRequest.getPassword().trim().isEmpty()) {
            logger.warn("登录失败: 密码为空");
            throw new BusinessException(ErrorCodes.USER_VALIDATION_FAILED, "密码不能为空", 400);
        }
        
        logger.info("开始查找用户: {}", loginRequest.getUsername().trim());
        
        // 根据用户名查找用户
        Optional<User> userOpt = getUserByUsername(loginRequest.getUsername().trim());
        if (userOpt.isEmpty()) {
            logger.warn("登录失败: 用户不存在 - {}", loginRequest.getUsername());
            throw new BusinessException(ErrorCodes.INVALID_CREDENTIALS, "用户名或密码错误，请检查后重试", 401);
        }
        
        User user = userOpt.get();
        logger.info("找到用户: ID={}, 用户名={}", user.getId(), user.getUsername());
        
        // 验证密码
        if (!user.getPassword().equals(loginRequest.getPassword())) {
            logger.warn("登录失败: 密码错误 - 用户: {}", user.getUsername());
            throw new BusinessException(ErrorCodes.INVALID_CREDENTIALS, "用户名或密码错误，请检查后重试", 401);
        }
        
        logger.info("密码验证成功，开始生成JWT token");
        
        // 生成JWT token
        String token = jwtUtil.generateToken(user.getUsername());
        logger.info("JWT token生成成功，长度: {}", token.length());
        
        logger.info("=== 用户登录成功 ===");
        logger.info("用户: {}, ID: {}, Token: {}", user.getUsername(), user.getId(), token.substring(0, 20) + "...");
        
        return new AuthResponse(user, token, "登录成功！欢迎回来 " + user.getUsername());
    }

    /**
     * 用户退出登录
     * 
     * @param request HTTP请求
     * @return ApiResponse 响应结果
     */
    public ApiResponse<Object> logout(HttpServletRequest request) {
        logger.info("=== 用户退出登录开始 ===");
        
        try {
            // 从请求头获取token
            String token = getTokenFromRequest(request);
            if (token == null) {
                logger.info("退出登录: 未提供token，直接返回成功");
                return ApiResponse.success(null, "退出登录成功");
            }
            
            logger.info("获取到token，长度: {}", token.length());
            
            // 验证token
            if (!jwtUtil.validateToken(token)) {
                logger.info("退出登录: token无效或已过期，返回成功");
                return ApiResponse.success(null, "退出登录成功");
            }
            
            String username = jwtUtil.getUsernameFromToken(token);
            logger.info("退出登录用户: {}", username);
            
            // 这里可以添加服务端token黑名单逻辑
            // 比如将token加入Redis黑名单，或者记录到数据库
            // 目前JWT是无状态的，所以直接返回成功即可
            
            logger.info("=== 用户退出登录成功 ===");
            return ApiResponse.success(null, "退出登录成功");
            
        } catch (Exception e) {
            logger.error("退出登录过程中发生异常: {}", e.getMessage(), e);
            // 即使出错也返回成功，因为退出登录不应该失败
            return ApiResponse.success(null, "退出登录成功");
        }
    }

    /**
     * 从HTTP请求中提取token
     * 
     * @param request HTTP请求
     * @return String token字符串
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            logger.debug("从请求头提取token: {}", token.substring(0, Math.min(20, token.length())) + "...");
            return token;
        }
        logger.debug("请求头中未找到有效的Authorization token");
        return null;
    }

    /**
     * 检查登录状态
     * 
     * @param request HTTP请求
     * @return ApiResponse 响应结果
     */
    public ApiResponse<Object> checkAuthStatus(HttpServletRequest request) {
        logger.info("=== 检查登录状态开始 ===");
        
        try {
            // 从请求头获取token
            String token = getTokenFromRequest(request);
            if (token == null) {
                logger.info("检查登录状态: 未提供token");
                return ApiResponse.success(false, "未登录", null);
            }
            
            logger.info("检查登录状态: 获取到token，长度: {}", token.length());
            logger.info("检查登录状态: token内容: {}", token.substring(0, Math.min(50, token.length())) + "...");
            
            // 验证token
            logger.info("开始验证JWT token...");
            boolean isValid = jwtUtil.validateToken(token);
            logger.info("JWT token验证结果: {}", isValid);
            
            if (!isValid) {
                logger.info("检查登录状态: token无效或已过期");
                return ApiResponse.success(false, "token无效或已过期", null);
            }
            
            // 获取用户名并验证用户是否存在
            String username = jwtUtil.getUsernameFromToken(token);
            logger.info("检查登录状态: 解析用户名: {}", username);
            
            User user = getUserByUsername(username).orElse(null);
            if (user == null) {
                logger.info("检查登录状态: 用户不存在 - {}", username);
                return ApiResponse.success(false, "用户不存在", null);
            }
            
            logger.info("=== 检查登录状态: 已登录 ===");
            return ApiResponse.success(true, "已登录", user);
            
        } catch (Exception e) {
            logger.error("检查登录状态过程中发生异常: {}", e.getMessage(), e);
            return ApiResponse.success(false, "检查登录状态失败: " + e.getMessage(), null);
        }
    }

    /**
     * 获取当前登录用户信息
     * 
     * @param request HTTP请求
     * @return User 用户信息
     */
    public User getCurrentUser(HttpServletRequest request) {
        logger.info("=== 获取当前用户信息开始 ===");
        
        try {
            // 从请求头获取token
            String token = getTokenFromRequest(request);
            if (token == null) {
                logger.warn("获取当前用户信息失败: 未提供认证token");
                throw new BusinessException(ErrorCodes.INVALID_CREDENTIALS, "未提供认证token", 401);
            }
            
            logger.info("获取当前用户信息: 获取到token，长度: {}", token.length());
            
            // 验证token并获取用户名
            if (!jwtUtil.validateToken(token)) {
                logger.warn("获取当前用户信息失败: token无效或已过期");
                throw new BusinessException(ErrorCodes.INVALID_CREDENTIALS, "token无效或已过期", 401);
            }
            
            String username = jwtUtil.getUsernameFromToken(token);
            logger.info("获取当前用户信息: 解析用户名: {}", username);
            
            // 查找用户
            Optional<User> userOpt = getUserByUsername(username);
            if (userOpt.isEmpty()) {
                logger.warn("获取当前用户信息失败: 用户不存在 - {}", username);
                throw new BusinessException(ErrorCodes.USER_NOT_FOUND, "用户不存在", 404);
            }
            
            User user = userOpt.get();
            logger.info("=== 获取当前用户信息成功 ===");
            logger.info("用户: {}, ID: {}", user.getUsername(), user.getId());
            
            return user;
            
        } catch (BusinessException e) {
            logger.error("获取当前用户信息失败: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("获取当前用户信息过程中发生异常: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCodes.SYSTEM_ERROR, "获取用户信息失败: " + e.getMessage(), 500);
        }
    }
}