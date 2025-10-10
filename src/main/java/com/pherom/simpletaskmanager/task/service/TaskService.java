package com.pherom.simpletaskmanager.task.service;

import com.pherom.simpletaskmanager.task.dto.TaskRequestDTO;
import com.pherom.simpletaskmanager.task.dto.TaskResponseDTO;
import com.pherom.simpletaskmanager.task.entity.Task;
import com.pherom.simpletaskmanager.task.exception.TaskNotFoundException;
import com.pherom.simpletaskmanager.task.mapper.TaskMapper;
import com.pherom.simpletaskmanager.task.repository.JpaTaskRepository;
import jakarta.transaction.Transactional;
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

    @Transactional
    public TaskResponseDTO save(Long id, TaskRequestDTO task) {
        Task saveMe = (id == null)
                ? mapper.toTask(task)
                : repository.findById(id).map(existing -> {
                    if (task.title() != null) {
                        existing.setTitle(task.title());
                    }
                    if (task.description() != null) {
                        existing.setDescription(task.description());
                    }
                    if (task.completed() != null) {
                        existing.setCompleted(task.completed());
                    }
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

    @Transactional
    public void deleteById(long id) {
        repository.delete(repository.findById(id).orElseThrow(() -> new TaskNotFoundException(id)));
    }

    @Transactional
    public void deleteAll() {
        repository.deleteAll();
    }

    public Optional<TaskResponseDTO> findByTitle(String title) {
        return repository.findByTitle(title).map(mapper::toDTO);
    }

}
