package com.todolist.service;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 测试服务类
 * 
 * 处理测试相关的业务逻辑
 * 
 * @author todolist
 * @version 1.0
 * @since 2025-01-27
 */
@Service
public class TestService {
    
    /**
     * 生成测试数据
     * 
     * @return Map<String, Object> 测试数据
     */
    public Map<String, Object> generateTestData() {
        Map<String, Object> data = new HashMap<>();
        data.put("message", "Test data from service layer");
        data.put("timestamp", LocalDateTime.now().toString());
        data.put("service", "TestService");
        data.put("version", "1.0.0");
        return data;
    }
    
    /**
     * 处理测试数据
     * 
     * @param inputData 输入数据
     * @return Map<String, Object> 处理后的数据
     */
    public Map<String, Object> processTestData(Map<String, Object> inputData) {
        Map<String, Object> result = new HashMap<>();
        result.put("original", inputData);
        result.put("processed_at", LocalDateTime.now().toString());
        result.put("processed_by", "TestService");
        result.put("status", "success");
        return result;
    }
}