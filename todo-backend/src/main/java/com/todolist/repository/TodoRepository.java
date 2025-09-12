package com.todolist.repository;

import com.todolist.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findByTaskId(Long taskId);
    List<Todo> findByTaskIdOrderByCreatedAtDesc(Long taskId);
}