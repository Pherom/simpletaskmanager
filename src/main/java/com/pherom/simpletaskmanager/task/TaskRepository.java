package com.pherom.simpletaskmanager.task;

import java.util.List;
import java.util.Optional;

public interface TaskRepository {

    Task save(Task task);
    Optional<Task> findById(long id);
    List<Task> findAll();
    void deleteById(long id);
    Optional<Task> findByTitle(String title);

}
