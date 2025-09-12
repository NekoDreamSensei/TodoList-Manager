package com.todolist.controller;

import com.todolist.entity.Topic;
import com.todolist.service.TopicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

/**
 * 主题控制器
 * 
 * 负责处理与主题相关的HTTP请求，包括主题的增删改查操作
 * 主题是任务的分类容器，每个主题属于一个用户
 * 
 * @author todolist
 * @version 1.0
 * @since 2025-09-12
 */
@RestController
@RequestMapping("/api/topics")
@CrossOrigin(origins = "*")
@Tag(name = "主题管理", description = "主题相关的API操作")
public class TopicController {
    
    @Autowired
    private TopicService topicService;
    
    /**
     * 根据用户ID获取该用户的所有主题
     * 
     * @param userId 用户ID
     * @return List<Topic> 该用户的主题列表
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "获取用户的所有主题", description = "根据用户ID获取该用户创建的所有主题列表")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取主题列表"),
            @ApiResponse(responseCode = "404", description = "用户不存在"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public List<Topic> getTopicsByUserId(
            @Parameter(description = "用户ID", required = true, example = "1")
            @PathVariable Long userId) {
        return topicService.getTopicsByUserId(userId);
    }
    
    /**
     * 根据ID获取特定主题
     * 
     * @param id 主题ID
     * @return ResponseEntity<Topic> 包含主题信息的响应实体，如果主题不存在则返回404
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取主题", description = "根据主题ID获取特定主题的详细信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取主题信息"),
            @ApiResponse(responseCode = "404", description = "主题不存在"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Topic> getTopicById(
            @Parameter(description = "主题ID", required = true, example = "1")
            @PathVariable Long id) {
        Optional<Topic> topic = topicService.getTopicById(id);
        return topic.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 为指定用户创建新主题
     * 
     * @param userId 用户ID
     * @param topic 要创建的主题信息
     * @return Topic 创建成功的主题信息（包含生成的ID）
     */
    @PostMapping("/user/{userId}")
    @Operation(summary = "创建新主题", description = "为指定用户创建新的主题分类")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功创建主题"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "404", description = "用户不存在"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public Topic createTopic(
            @Parameter(description = "用户ID", required = true, example = "1")
            @PathVariable Long userId, 
            @Parameter(description = "主题信息", required = true)
            @RequestBody Topic topic) {
        return topicService.createTopic(topic, userId);
    }
    
    /**
     * 更新主题信息
     * 
     * @param id 要更新的主题ID
     * @param topic 新的主题信息
     * @return ResponseEntity<Topic> 更新后的主题信息
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新主题信息", description = "根据主题ID更新主题信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功更新主题信息"),
            @ApiResponse(responseCode = "404", description = "主题不存在"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Topic> updateTopic(
            @Parameter(description = "主题ID", required = true, example = "1")
            @PathVariable Long id, 
            @Parameter(description = "新的主题信息", required = true)
            @RequestBody Topic topic) {
        topic.setId(id);
        Topic updatedTopic = topicService.updateTopic(topic);
        return ResponseEntity.ok(updatedTopic);
    }
    
    /**
     * 删除主题
     * 
     * @param id 要删除的主题ID
     * @return ResponseEntity<Void> 删除成功返回204状态码
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除主题", description = "根据主题ID删除主题及其所有相关任务")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "成功删除主题"),
            @ApiResponse(responseCode = "404", description = "主题不存在"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Void> deleteTopic(
            @Parameter(description = "主题ID", required = true, example = "1")
            @PathVariable Long id) {
        topicService.deleteTopic(id);
        return ResponseEntity.noContent().build();
    }
}