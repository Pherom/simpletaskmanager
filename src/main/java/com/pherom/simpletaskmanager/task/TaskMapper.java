package com.pherom.simpletaskmanager.task;

import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public Task toTask(TaskRequestDTO dto) {
        return new Task(dto.title(), dto.description(), dto.completed());
    }

    public TaskResponseDTO toDTO(Task task) {
        return new TaskResponseDTO(task.getId(), task.getTitle(), task.getDescription(), task.isCompleted());
    }

}
