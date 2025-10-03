package com.pherom.simpletaskmanager.task;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    Optional<Task> save(Task task) {
        return repository.save(task);
    }

    Optional<Task> add(Task task) {
        return repository.add(task);
    }

    Optional<Task> findById(long id) {
        return repository.findById(id);
    }

    List<Task> findAll() {
        return repository.findAll();
    }

    Optional<Task> deleteById(long id) {
        return repository.deleteById(id);
    }

    Optional<Task> findByTitle(String title) {
        return repository.findByTitle(title);
    }

}
