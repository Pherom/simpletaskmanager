package com.pherom.simpletaskmanager.task;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @GetMapping
    public List<TaskResponseDTO> getAllTasks() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> getTaskById(@PathVariable long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TaskResponseDTO> createTask(@RequestBody TaskRequestDTO task) {
        TaskResponseDTO result = service.save(null, task);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(result.id())
                .toUri();

        return ResponseEntity.created(uri).body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> saveTask(@PathVariable long id, @RequestBody TaskRequestDTO task) {
        return ResponseEntity.ok(service.save(id, task));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> removeTask(@PathVariable long id) {
        return ResponseEntity.ok(service.deleteById(id));
    }
}
