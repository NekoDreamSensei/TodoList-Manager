package com.todolist.controller;

import com.todolist.dto.TodoDTO;
import com.todolist.entity.Todo;
import com.todolist.service.TodoService;
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
 * 待办事项控制器
 * 
 * 负责处理与待办事项相关的HTTP请求，包括待办事项的增删改查操作
 * 待办事项是任务的具体执行步骤，每个待办事项属于一个任务
 * 
 * @author todolist
 * @version 1.0
 * @since 2025-09-12
 */
@RestController
@RequestMapping("/api/todos")
@Tag(name = "待办事项管理", description = "待办事项相关的API操作")
public class TodoController {
    
    @Autowired
    private TodoService todoService;
    
    /**
     * 根据任务ID获取该任务下的所有待办事项
     * 
     * @param taskId 任务ID
     * @return List<TodoDTO> 该任务下的待办事项列表
     */
    @GetMapping("/task/{taskId}")
    @Operation(summary = "获取任务下的所有待办事项", description = "根据任务ID获取该任务下的所有待办事项列表")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取待办事项列表"),
            @ApiResponse(responseCode = "404", description = "任务不存在"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public List<TodoDTO> getTodosByTaskId(
            @Parameter(description = "任务ID", required = true, example = "1")
            @PathVariable Long taskId) {
        List<Todo> todos = todoService.getTodosByTaskId(taskId);
        return todos.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * 根据ID获取特定待办事项
     * 
     * @param id 待办事项ID
     * @return ResponseEntity<TodoDTO> 包含待办事项信息的响应实体，如果待办事项不存在则返回404
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取待办事项", description = "根据待办事项ID获取特定待办事项的详细信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取待办事项信息"),
            @ApiResponse(responseCode = "404", description = "待办事项不存在"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<TodoDTO> getTodoById(
            @Parameter(description = "待办事项ID", required = true, example = "1")
            @PathVariable Long id) {
        Optional<Todo> todo = todoService.getTodoById(id);
        return todo.map(t -> ResponseEntity.ok(convertToDTO(t)))
                   .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 在指定任务下创建新待办事项
     * 
     * @param taskId 任务ID
     * @param todoDTO 要创建的待办事项信息
     * @return TodoDTO 创建成功的待办事项信息（包含生成的ID）
     */
    @PostMapping("/task/{taskId}")
    @Operation(summary = "创建新待办事项", description = "在指定任务下创建新的待办事项")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功创建待办事项"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "404", description = "任务不存在"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public TodoDTO createTodo(
            @Parameter(description = "任务ID", required = true, example = "1")
            @PathVariable Long taskId, 
            @Parameter(description = "待办事项信息", required = true)
            @RequestBody TodoDTO todoDTO) {
        Todo todo = convertToEntity(todoDTO);
        Todo savedTodo = todoService.createTodo(todo, taskId);
        return convertToDTO(savedTodo);
    }
    
    /**
     * 更新待办事项信息
     * 
     * @param id 要更新的待办事项ID
     * @param todoDTO 新的待办事项信息
     * @return ResponseEntity<TodoDTO> 更新后的待办事项信息
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新待办事项信息", description = "根据待办事项ID更新待办事项信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功更新待办事项信息"),
            @ApiResponse(responseCode = "404", description = "待办事项不存在"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<TodoDTO> updateTodo(
            @Parameter(description = "待办事项ID", required = true, example = "1")
            @PathVariable Long id, 
            @Parameter(description = "新的待办事项信息", required = true)
            @RequestBody TodoDTO todoDTO) {
        Optional<Todo> existingTodo = todoService.getTodoById(id);
        if (existingTodo.isPresent()) {
            Todo todo = existingTodo.get();
            todo.setContent(todoDTO.getContent());
            todo.setIsCompleted(todoDTO.getIsCompleted());
            todo.setCompletedAt(todoDTO.getCompletedAt());
            Todo updatedTodo = todoService.updateTodo(todo);
            return ResponseEntity.ok(convertToDTO(updatedTodo));
        }
        return ResponseEntity.notFound().build();
    }
    
    /**
     * 删除待办事项
     * 
     * @param id 要删除的待办事项ID
     * @return ResponseEntity<Void> 删除成功返回204状态码
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除待办事项", description = "根据待办事项ID删除待办事项")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "成功删除待办事项"),
            @ApiResponse(responseCode = "404", description = "待办事项不存在"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Void> deleteTodo(
            @Parameter(description = "待办事项ID", required = true, example = "1")
            @PathVariable Long id) {
        boolean deleted = todoService.deleteTodo(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
    
    /**
     * 将 Todo 实体转换为 TodoDTO
     */
    private TodoDTO convertToDTO(Todo todo) {
        TodoDTO dto = new TodoDTO();
        dto.setId(todo.getId());
        dto.setTitle(todo.getTitle());
        dto.setContent(todo.getContent());
        dto.setIsCompleted(todo.getIsCompleted());
        dto.setCompleted(todo.getCompleted());
        dto.setProgress(todo.getProgress());
        dto.setNote(todo.getNote());
        dto.setCompletedAt(todo.getCompletedAt());
        dto.setCreatedAt(todo.getCreatedAt());
        dto.setUpdatedAt(todo.getUpdatedAt());
        
        // 安全地获取任务ID
        if (todo.getTask() != null) {
            dto.setTaskId(todo.getTask().getId());
        }
        
        return dto;
    }
    
    /**
     * 将 TodoDTO 转换为 Todo 实体
     */
    private Todo convertToEntity(TodoDTO dto) {
        Todo todo = new Todo();
        todo.setId(dto.getId());
        todo.setTitle(dto.getTitle());
        todo.setContent(dto.getContent());
        todo.setIsCompleted(dto.getIsCompleted());
        todo.setCompleted(dto.getCompleted());
        todo.setProgress(dto.getProgress());
        todo.setNote(dto.getNote());
        todo.setCompletedAt(dto.getCompletedAt());
        // 注意：不设置 task，让 service 层处理
        return todo;
    }
}