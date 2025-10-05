package com.pherom.simpletaskmanager.task.service;

import com.pherom.simpletaskmanager.task.dto.TaskRequestDTO;
import com.pherom.simpletaskmanager.task.dto.TaskResponseDTO;
import com.pherom.simpletaskmanager.task.entity.Task;
import com.pherom.simpletaskmanager.task.exception.TaskNotFoundException;
import com.pherom.simpletaskmanager.task.mapper.TaskMapper;
import com.pherom.simpletaskmanager.task.repository.JpaTaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final JpaTaskRepository repository;
    private final TaskMapper mapper;

    public TaskService(JpaTaskRepository repository, TaskMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public TaskResponseDTO save(Long id, TaskRequestDTO task) {
        Task saveMe = (id == null)
                ? mapper.toTask(task)
                : repository.findById(id).map(existing -> {
                    existing.setTitle(task.title());
                    existing.setDescription(task.description());
                    existing.setCompleted(task.completed());
                    return existing;
        }).orElseThrow(() -> new TaskNotFoundException(id));

        return mapper.toDTO(repository.save(saveMe));
    }

    public Optional<TaskResponseDTO> findById(long id) {
        return repository.findById(id).map(mapper::toDTO);
    }

    public List<TaskResponseDTO> findAll() {
        return repository.findAll().stream().map(mapper::toDTO).toList();
    }

    public TaskResponseDTO deleteById(long id) {
        Task toDelete = repository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
        TaskResponseDTO result = mapper.toDTO(toDelete);
        repository.delete(toDelete);
        return result;
    }

    public void deleteAll() {
        repository.deleteAll();
    }

    public Optional<TaskResponseDTO> findByTitle(String title) {
        return repository.findByTitle(title).map(mapper::toDTO);
    }

}
