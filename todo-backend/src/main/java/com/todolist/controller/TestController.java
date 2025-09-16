package com.todolist.controller;

import com.todolist.exception.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 测试控制器
 * 
 * 用于测试API接口和JWT认证系统
 * 
 * @author todolist
 * @version 1.0
 * @since 2025-01-27
 */
@RestController
@RequestMapping("/api/test")
@Tag(name = "测试接口", description = "用于测试API和认证系统的接口")
public class TestController {
    
    /**
     * 测试GET接口
     * 返回当前时间和一些测试数据
     * 
     * @return ResponseEntity<ApiResponse> 包含测试数据的响应
     */
    @GetMapping("/data")
    @Operation(summary = "测试GET接口", description = "返回测试数据，用于验证API调用")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取测试数据"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<ApiResponse> getTestData() {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("message", "Hello from backend!");
            data.put("timestamp", LocalDateTime.now().toString());
            data.put("server", "Spring Boot");
            data.put("version", "1.0.0");
            data.put("status", "running");
            
            return ResponseEntity.ok(ApiResponse.success(data, "获取测试数据成功"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("ERROR", "获取测试数据失败: " + e.getMessage()));
        }
    }
    
    /**
     * 测试POST接口
     * 接收前端发送的数据并返回处理结果
     * 
     * @param requestData 前端发送的请求数据
     * @return ResponseEntity<ApiResponse> 包含处理结果的响应
     */
    @PostMapping("/data")
    @Operation(summary = "测试POST接口", description = "接收并处理前端发送的数据")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功处理数据"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "请求参数错误"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<ApiResponse> postTestData(@RequestBody Map<String, Object> requestData) {
        try {
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("received", requestData);
            responseData.put("processed_at", LocalDateTime.now().toString());
            responseData.put("message", "数据已成功接收并处理");
            responseData.put("data_size", requestData.size());
            
            // 模拟一些处理逻辑
            if (requestData.containsKey("message")) {
                responseData.put("echo", "Echo: " + requestData.get("message"));
            }
            
            return ResponseEntity.ok(ApiResponse.success(responseData, "POST数据处理成功"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("ERROR", "POST数据处理失败: " + e.getMessage()));
        }
    }
    
    /**
     * 测试认证状态接口
     * 返回当前认证用户的详细信息
     * 
     * @return ResponseEntity<ApiResponse> 包含用户认证信息的响应
     */
    @GetMapping("/auth-status")
    @Operation(summary = "测试认证状态", description = "返回当前用户的认证状态信息")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取认证状态"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<ApiResponse> getAuthStatus() {
        try {
            Map<String, Object> authData = new HashMap<>();
            authData.put("authenticated", true);
            authData.put("timestamp", LocalDateTime.now().toString());
            authData.put("message", "JWT认证系统工作正常");
            authData.put("security_features", new String[]{
                "JWT Token验证",
                "请求头认证",
                "跨域支持",
                "异常处理"
            });
            
            return ResponseEntity.ok(ApiResponse.success(authData, "认证状态检查成功"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("ERROR", "认证状态检查失败: " + e.getMessage()));
        }
    }
}
