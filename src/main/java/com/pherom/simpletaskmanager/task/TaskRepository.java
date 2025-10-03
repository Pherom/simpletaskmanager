package com.pherom.simpletaskmanager.task;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface TaskRepository {

    Optional<Task> save(Task task);
    Optional<Task> add(Task task);
    Optional<Task> findById(long id);
    List<Task> findAll();
    Optional<Task> deleteById(long id);
    Optional<Task> findByTitle(String title);

}
