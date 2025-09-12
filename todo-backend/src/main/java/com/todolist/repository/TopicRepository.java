package com.todolist.repository;

import com.todolist.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {
    List<Topic> findByUserId(Long userId);
    List<Topic> findByUserIdOrderByCreatedAtDesc(Long userId);
}