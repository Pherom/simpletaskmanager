package com.pherom.simpletaskmanager.task;

import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryTaskRespository implements TaskRepository{
    private final Map<Long, Task> tasks = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong();

    @Override
    public Optional<Task> save(Task task) {
        if (!tasks.containsKey(task.id())) {
            return Optional.empty();
        }

        tasks.put(task.id(), task);
        return Optional.of(task);
    }

    @Override
    public Optional<Task> add(Task task) {
        Task previous = tasks.putIfAbsent(task.id(), task);
        return previous == null ? Optional.of(task) : Optional.empty();
    }

    @Override
    public Optional<Task> findById(long id) {
        return Optional.ofNullable(tasks.get(id));
    }

    @Override
    public List<Task> findAll() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public Optional<Task> deleteById(long id) {
        return Optional.ofNullable(tasks.remove(id));
    }

    @Override
    public Optional<Task> findByTitle(String title) {
        return tasks.values().stream()
                .filter(task -> task.title().equals(title))
                .findFirst();
    }
}
