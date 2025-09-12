package com.todolist.service;

import com.todolist.entity.Todo;
import com.todolist.entity.Task;
import com.todolist.repository.TodoRepository;
import com.todolist.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

/**
 * 待办事项服务类
 * 
 * 负责处理待办事项相关的业务逻辑，包括待办事项的增删改查操作
 * 待办事项是任务的具体执行步骤，每个待办事项属于一个任务
 * 
 * @author todolist
 * @version 1.0
 * @since 2025-09-12
 */
@Service
public class TodoService {
    
    @Autowired
    private TodoRepository todoRepository;
    
    @Autowired
    private TaskRepository taskRepository;
    
    /**
     * 根据任务ID获取该任务下的所有待办事项
     * 按创建时间倒序排列
     * 
     * @param taskId 任务ID
     * @return List<Todo> 该任务下的待办事项列表
     */
    public List<Todo> getTodosByTaskId(Long taskId) {
        return todoRepository.findByTaskIdOrderByCreatedAtDesc(taskId);
    }
    
    /**
     * 根据ID获取特定待办事项
     * 
     * @param id 待办事项ID
     * @return Optional<Todo> 待办事项信息，如果不存在则返回空
     */
    public Optional<Todo> getTodoById(Long id) {
        return todoRepository.findById(id);
    }
    
    /**
     * 在指定任务下创建新待办事项
     * 
     * @param todo 要创建的待办事项信息
     * @param taskId 任务ID
     * @return Todo 创建成功的待办事项信息（包含生成的ID）
     * @throws RuntimeException 如果任务不存在则抛出异常
     */
    public Todo createTodo(Todo todo, Long taskId) {
        Optional<Task> task = taskRepository.findById(taskId);
        if (task.isPresent()) {
            todo.setTask(task.get());
            return todoRepository.save(todo);
        }
        throw new RuntimeException("任务不存在");
    }
    
    /**
     * 更新待办事项信息
     * 
     * @param todo 要更新的待办事项信息
     * @return Todo 更新后的待办事项信息
     */
    public Todo updateTodo(Todo todo) {
        return todoRepository.save(todo);
    }
    
    /**
     * 删除待办事项
     * 
     * @param id 要删除的待办事项ID
     */
    public void deleteTodo(Long id) {
        todoRepository.deleteById(id);
    }
}