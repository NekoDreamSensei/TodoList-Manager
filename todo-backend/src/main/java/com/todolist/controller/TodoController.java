package com.todolist.controller;

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
@CrossOrigin(origins = "*")
@Tag(name = "待办事项管理", description = "待办事项相关的API操作")
public class TodoController {
    
    @Autowired
    private TodoService todoService;
    
    /**
     * 根据任务ID获取该任务下的所有待办事项
     * 
     * @param taskId 任务ID
     * @return List<Todo> 该任务下的待办事项列表
     */
    @GetMapping("/task/{taskId}")
    @Operation(summary = "获取任务下的所有待办事项", description = "根据任务ID获取该任务下的所有待办事项列表")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取待办事项列表"),
            @ApiResponse(responseCode = "404", description = "任务不存在"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public List<Todo> getTodosByTaskId(
            @Parameter(description = "任务ID", required = true, example = "1")
            @PathVariable Long taskId) {
        return todoService.getTodosByTaskId(taskId);
    }
    
    /**
     * 根据ID获取特定待办事项
     * 
     * @param id 待办事项ID
     * @return ResponseEntity<Todo> 包含待办事项信息的响应实体，如果待办事项不存在则返回404
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取待办事项", description = "根据待办事项ID获取特定待办事项的详细信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取待办事项信息"),
            @ApiResponse(responseCode = "404", description = "待办事项不存在"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Todo> getTodoById(
            @Parameter(description = "待办事项ID", required = true, example = "1")
            @PathVariable Long id) {
        Optional<Todo> todo = todoService.getTodoById(id);
        return todo.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 在指定任务下创建新待办事项
     * 
     * @param taskId 任务ID
     * @param todo 要创建的待办事项信息
     * @return Todo 创建成功的待办事项信息（包含生成的ID）
     */
    @PostMapping("/task/{taskId}")
    @Operation(summary = "创建新待办事项", description = "在指定任务下创建新的待办事项")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功创建待办事项"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "404", description = "任务不存在"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public Todo createTodo(
            @Parameter(description = "任务ID", required = true, example = "1")
            @PathVariable Long taskId, 
            @Parameter(description = "待办事项信息", required = true)
            @RequestBody Todo todo) {
        return todoService.createTodo(todo, taskId);
    }
    
    /**
     * 更新待办事项信息
     * 
     * @param id 要更新的待办事项ID
     * @param todo 新的待办事项信息
     * @return ResponseEntity<Todo> 更新后的待办事项信息
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新待办事项信息", description = "根据待办事项ID更新待办事项信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功更新待办事项信息"),
            @ApiResponse(responseCode = "404", description = "待办事项不存在"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Todo> updateTodo(
            @Parameter(description = "待办事项ID", required = true, example = "1")
            @PathVariable Long id, 
            @Parameter(description = "新的待办事项信息", required = true)
            @RequestBody Todo todo) {
        todo.setId(id);
        Todo updatedTodo = todoService.updateTodo(todo);
        return ResponseEntity.ok(updatedTodo);
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
        todoService.deleteTodo(id);
        return ResponseEntity.noContent().build();
    }
}