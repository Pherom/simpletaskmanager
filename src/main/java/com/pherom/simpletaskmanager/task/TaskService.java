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

    public Optional<Task> save(Task task) {
        return repository.save(task);
    }

    public Task add(Task task) {
        return repository.add(task);
    }

    public Optional<Task> findById(long id) {
        return repository.findById(id);
    }

    public List<Task> findAll() {
        return repository.findAll();
    }

    public Optional<Task> deleteById(long id) {
        return repository.deleteById(id);
    }

    public Optional<Task> findByTitle(String title) {
        return repository.findByTitle(title);
    }

}
