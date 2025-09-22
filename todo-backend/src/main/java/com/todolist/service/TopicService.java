package com.todolist.service;

import com.todolist.entity.Topic;
import com.todolist.entity.User;
import com.todolist.repository.TopicRepository;
import com.todolist.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

/**
 * 主题服务类
 * 
 * 负责处理主题相关的业务逻辑，包括主题的增删改查操作
 * 主题是任务的分类容器，每个主题属于一个用户
 * 
 * @author todolist
 * @version 1.0
 * @since 2025-09-12
 */
@Service
public class TopicService {
    
    @Autowired
    private TopicRepository topicRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * 根据用户ID获取该用户的所有主题
     * 按创建时间倒序排列
     * 
     * @param userId 用户ID
     * @return List<Topic> 该用户的主题列表
     */
    public List<Topic> getTopicsByUserId(Long userId) {
        return topicRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    /**
     * 根据ID获取特定主题
     * 
     * @param id 主题ID
     * @return Optional<Topic> 主题信息，如果不存在则返回空
     */
    public Optional<Topic> getTopicById(Long id) {
        return topicRepository.findById(id);
    }
    
    /**
     * 为指定用户创建新主题
     * 
     * @param topic 要创建的主题信息
     * @param userId 用户ID
     * @return Topic 创建成功的主题信息（包含生成的ID）
     * @throws RuntimeException 如果用户不存在则抛出异常
     */
    public Topic createTopic(Topic topic, Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("用户不存在，ID: " + userId));
        topic.setUser(user);
        return topicRepository.save(topic);
    }
    
    /**
     * 更新主题信息
     * 
     * @param topic 要更新的主题信息
     * @return Topic 更新后的主题信息
     */
    public Topic updateTopic(Topic topic) {
        return topicRepository.save(topic);
    }
    
    /**
     * 删除主题
     * 
     * @param id 要删除的主题ID
     * @return boolean 删除是否成功
     */
    public boolean deleteTopic(Long id) {
        if (topicRepository.existsById(id)) {
            topicRepository.deleteById(id);
            return true;
        }
        return false;
    }
}