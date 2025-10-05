package com.pherom.simpletaskmanager.task.mapper;

import com.pherom.simpletaskmanager.task.dto.TaskRequestDTO;
import com.pherom.simpletaskmanager.task.dto.TaskResponseDTO;
import com.pherom.simpletaskmanager.task.entity.Task;
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
