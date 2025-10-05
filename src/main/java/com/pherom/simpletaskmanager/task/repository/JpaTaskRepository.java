package com.pherom.simpletaskmanager.task.repository;

import com.pherom.simpletaskmanager.task.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaTaskRepository extends JpaRepository<Task, Long> {
    Optional<Task> findByTitle(String title);
}
