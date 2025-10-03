package com.pherom.simpletaskmanager.task;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryTaskRespository implements TaskRepository{
    private final Map<Long, Task> tasks = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong();

    @Override
    public Task save(Task task) {
        if (!tasks.containsKey(task.id())) {
            throw new RuntimeException("Could not find task with ID: " + task.id());
        }

        tasks.put(task.id(), task);
        return task;
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
    public void deleteById(long id) {
        tasks.remove(id);
    }

    @Override
    public Optional<Task> findByTitle(String title) {
        return tasks.values().stream()
                .filter(task -> task.title().equals(title))
                .findFirst();
    }
}
