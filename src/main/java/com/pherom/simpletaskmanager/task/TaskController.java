package com.pherom.simpletaskmanager.task;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskRepository repository = new InMemoryTaskRespository();

    @GetMapping
    public List<Task> getAllTasks() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        Optional<Task> result = repository.add(task);

        if (result.isEmpty()) {
            return ResponseEntity.status(409).build();
        }

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(result.get().id())
                .toUri();

        return ResponseEntity.created(uri).body(result.get());
    }

    @PutMapping
    public ResponseEntity<Task> saveTask(@RequestBody Task task) {
        return repository.save(task)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Task> removeTask(@PathVariable long id) {
        return repository.deleteById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
