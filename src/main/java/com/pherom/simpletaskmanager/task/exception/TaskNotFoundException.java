package com.pherom.simpletaskmanager.task.exception;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(long id) {
        super("Could not find a task with ID: " + id);
    }
}
