package com.todolist.controller;

import com.todolist.dto.TopicDTO;
import com.todolist.dto.TaskDTO;
import com.todolist.entity.Topic;
import com.todolist.entity.Task;
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
import java.util.stream.Collectors;

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
@Tag(name = "主题管理", description = "主题相关的API操作")
public class TopicController {
    
    @Autowired
    private TopicService topicService;
    
    /**
     * 获取用户的所有主题
     * 
     * @param userId 用户ID
     * @return List<TopicDTO> 该用户的主题列表
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "获取用户的所有主题", description = "根据用户ID获取该用户创建的所有主题列表")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取主题列表"),
            @ApiResponse(responseCode = "404", description = "用户不存在"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public List<TopicDTO> getTopicsByUserId(
            @Parameter(description = "用户ID", required = true, example = "1")
            @PathVariable Long userId) {
        List<Topic> topics = topicService.getTopicsByUserId(userId);
        return topics.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * 根据ID获取特定主题
     * 
     * @param id 主题ID
     * @return ResponseEntity<TopicDTO> 包含主题信息的响应实体，如果主题不存在则返回404
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取主题", description = "根据主题ID获取特定主题的详细信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取主题信息"),
            @ApiResponse(responseCode = "404", description = "主题不存在"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<TopicDTO> getTopicById(
            @Parameter(description = "主题ID", required = true, example = "1")
            @PathVariable Long id) {
        Optional<Topic> topic = topicService.getTopicById(id);
        return topic.map(t -> ResponseEntity.ok(convertToDTO(t)))
                   .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 为指定用户创建新主题
     * 
     * @param userId 用户ID
     * @param topicDTO 要创建的主题信息
     * @return TopicDTO 创建成功的主题信息（包含生成的ID）
     */
    @PostMapping("/user/{userId}")
    @Operation(summary = "创建新主题", description = "为指定用户创建新的主题")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功创建主题"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "404", description = "用户不存在"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public TopicDTO createTopic(
            @Parameter(description = "用户ID", required = true, example = "1")
            @PathVariable Long userId,
            @Parameter(description = "主题信息", required = true)
            @RequestBody TopicDTO topicDTO) {
        Topic topic = convertToEntity(topicDTO);
        Topic savedTopic = topicService.createTopic(topic, userId);
        return convertToDTO(savedTopic);
    }
    
    /**
     * 更新主题信息
     * 
     * @param id 主题ID
     * @param topicDTO 更新后的主题信息
     * @return ResponseEntity<TopicDTO> 更新后的主题信息，如果主题不存在则返回404
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新主题", description = "更新指定主题的信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功更新主题"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "404", description = "主题不存在"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<TopicDTO> updateTopic(
            @Parameter(description = "主题ID", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "更新后的主题信息", required = true)
            @RequestBody TopicDTO topicDTO) {
        Optional<Topic> existingTopic = topicService.getTopicById(id);
        if (existingTopic.isPresent()) {
            Topic topic = existingTopic.get();
            topic.setName(topicDTO.getName());
            topic.setDescription(topicDTO.getDescription());
            Topic updatedTopic = topicService.updateTopic(topic);
            return ResponseEntity.ok(convertToDTO(updatedTopic));
        }
        return ResponseEntity.notFound().build();
    }
    
    /**
     * 删除主题
     * 
     * @param id 主题ID
     * @return ResponseEntity<Void> 删除成功返回204，如果主题不存在则返回404
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除主题", description = "删除指定的主题及其所有相关数据")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "成功删除主题"),
            @ApiResponse(responseCode = "404", description = "主题不存在"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Void> deleteTopic(
            @Parameter(description = "主题ID", required = true, example = "1")
            @PathVariable Long id) {
        boolean deleted = topicService.deleteTopic(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
    
    /**
     * 将 Topic 实体转换为 TopicDTO
     */
    private TopicDTO convertToDTO(Topic topic) {
        TopicDTO dto = new TopicDTO();
        dto.setId(topic.getId());
        dto.setName(topic.getName());
        dto.setDescription(topic.getDescription());
        dto.setCreatedAt(topic.getCreatedAt());
        dto.setUpdatedAt(topic.getUpdatedAt());
        
        // 安全地获取用户ID
        if (topic.getUser() != null) {
            dto.setUserId(topic.getUser().getId());
        }
        
        // 如果需要包含任务列表，可以在这里添加
        // 但建议在需要时才加载，避免性能问题
        // if (topic.getTasks() != null) {
        //     List<TaskDTO> taskDTOs = topic.getTasks().stream()
        //         .map(this::convertTaskToDTO)
        //         .collect(Collectors.toList());
        //     dto.setTasks(taskDTOs);
        // }
        
        return dto;
    }
    
    /**
     * 将 TopicDTO 转换为 Topic 实体
     */
    private Topic convertToEntity(TopicDTO dto) {
        Topic topic = new Topic();
        topic.setId(dto.getId());
        topic.setName(dto.getName());
        topic.setDescription(dto.getDescription());
        // 注意：不设置 user，让 service 层处理
        return topic;
    }
    
    /**
     * 将 Task 实体转换为 TaskDTO（如果需要）
     */
    private TaskDTO convertTaskToDTO(Task task) {
        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setStatus(task.getStatus());
        dto.setPriority(task.getPriority());
        dto.setDueDate(task.getDueDate());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());
        
        if (task.getTopic() != null) {
            dto.setTopicId(task.getTopic().getId());
        }
        
        return dto;
    }
}