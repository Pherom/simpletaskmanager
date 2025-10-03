package com.pherom.simpletaskmanager.task;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(long id) {
        super("Could not find a task with ID: " + id);
    }
}
