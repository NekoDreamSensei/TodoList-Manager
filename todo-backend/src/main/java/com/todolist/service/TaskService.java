package com.todolist.service;

import com.todolist.entity.Task;
import com.todolist.entity.Topic;
import com.todolist.repository.TaskRepository;
import com.todolist.repository.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

/**
 * 任务服务类
 * 
 * 负责处理任务相关的业务逻辑，包括任务的增删改查操作
 * 任务是待办事项的基本单位，每个任务属于一个主题
 * 
 * @author todolist
 * @version 1.0
 * @since 2025-09-12
 */
@Service
public class TaskService {
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private TopicRepository topicRepository;
    
    /**
     * 根据主题ID获取该主题下的所有任务
     * 
     * @param topicId 主题ID
     * @return List<Task> 该主题下的任务列表
     */
    public List<Task> getTasksByTopicId(Long topicId) {
        return taskRepository.findByTopicId(topicId);
    }
    
    /**
     * 根据ID获取特定任务
     * 
     * @param id 任务ID
     * @return Optional<Task> 任务信息，如果不存在则返回空
     */
    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }
    
    /**
     * 在指定主题下创建新任务
     * 
     * @param task 要创建的任务信息
     * @param topicId 主题ID
     * @return Task 创建成功的任务信息（包含生成的ID）
     * @throws RuntimeException 如果主题不存在则抛出异常
     */
    public Task createTask(Task task, Long topicId) {
        Topic topic = topicRepository.findById(topicId)
            .orElseThrow(() -> new RuntimeException("主题不存在，ID: " + topicId));
        task.setTopic(topic);
        return taskRepository.save(task);
    }
    
    /**
     * 更新任务信息
     * 
     * @param task 要更新的任务信息
     * @return Task 更新后的任务信息
     */
    public Task updateTask(Task task) {
        return taskRepository.save(task);
    }
    
    /**
     * 删除任务
     * 
     * @param id 要删除的任务ID
     * @return boolean 删除是否成功
     */
    public boolean deleteTask(Long id) {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
            return true;
        }
        return false;
    }
}