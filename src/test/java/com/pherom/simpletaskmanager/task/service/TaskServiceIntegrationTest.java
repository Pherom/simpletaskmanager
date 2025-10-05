package com.pherom.simpletaskmanager.task.service;

import static org.junit.jupiter.api.Assertions.*;
import com.pherom.simpletaskmanager.task.dto.TaskRequestDTO;
import com.pherom.simpletaskmanager.task.dto.TaskResponseDTO;
import com.pherom.simpletaskmanager.task.exception.TaskNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@SpringBootTest
class TaskServiceIntegrationTest {

    @Autowired
    private TaskService taskService;

    @BeforeEach
    void reset() {
        taskService.deleteAll();
    }

    @Test
    void saveNewTaskAndRetrieveIt() {
        String title = "TITLE";
        String description = "DESC";
        boolean completed = false;

        TaskRequestDTO request = new TaskRequestDTO(title, description, completed);
        TaskResponseDTO saveResponse = taskService.save(null, request);

        Optional<TaskResponseDTO> findResponse = taskService.findById(saveResponse.id());
        assertTrue(findResponse.isPresent());
        assertEquals(title, findResponse.get().title());
        assertEquals(description, findResponse.get().description());
        assertEquals(completed, findResponse.get().completed());
    }

    @Test
    void saveNewTaskUpdateAndRetrieveIt() {
        String title = "TITLE";
        String description = "DESC";
        boolean completed = false;

        String updatedTitle = "UPDATED_TITLE";
        String updatedDescription = "UPDATED_DESC";
        boolean updatedCompleted = true;

        TaskRequestDTO request = new TaskRequestDTO(title, description, completed);
        TaskResponseDTO saveResponse = taskService.save(null, request);

        TaskRequestDTO updateRequest = new TaskRequestDTO(updatedTitle, updatedDescription, updatedCompleted);
        taskService.save(saveResponse.id(), updateRequest);

        Optional<TaskResponseDTO> response = taskService.findById(saveResponse.id());
        assertTrue(response.isPresent());
        assertEquals(updatedTitle, response.get().title());
        assertEquals(updatedDescription, response.get().description());
        assertEquals(updatedCompleted, response.get().completed());
    }

    @Test
    void saveSeveralTasksAndFindEachByID() {
        int taskAmount = 20;
        String titlePrefix = "TASK";
        String descriptionPrefix = "DESC";
        boolean defaultCompleted = false;
        List<TaskResponseDTO> responseList = new ArrayList<>(taskAmount);

        for (int i = 1; i <= taskAmount; ++i) {
            responseList.add(taskService.save(null, new TaskRequestDTO(titlePrefix + i, descriptionPrefix + i, defaultCompleted)));
        }

        for (int i = 1; i <= taskAmount; ++i) {
            Optional<TaskResponseDTO> findResponse = taskService.findById(responseList.get(i - 1).id());
            assertTrue(findResponse.isPresent());
            assertEquals(titlePrefix + i, findResponse.get().title());
            assertEquals(descriptionPrefix + i, findResponse.get().description());
            assertEquals(defaultCompleted, findResponse.get().completed());
        }
    }

    @Test
    void findNonExistingTask() {
        Optional<TaskResponseDTO> response = taskService.findById(1);
        assertTrue(response.isEmpty());
    }

    @Test
    void saveSeveralTasksAndFindAll() {
        int taskAmount = 20;
        String titlePrefix = "TASK";
        String descriptionPrefix = "DESC";
        boolean defaultCompleted = false;
        List<TaskResponseDTO> saveResponseList = new ArrayList<>(taskAmount);

        for (int i = 1; i <= taskAmount; ++i) {
            saveResponseList.add(taskService.save(null, new TaskRequestDTO(titlePrefix + i, descriptionPrefix + i, defaultCompleted)));
        }

        List<TaskResponseDTO> findResponseList = taskService.findAll();

        IntStream.range(0, taskAmount).forEach(i -> assertEquals(saveResponseList.get(i), findResponseList.get(i)));
    }

    @Test
    void saveNewTaskAndDeleteIt() {
        String title = "TITLE";
        String description = "DESC";
        boolean completed = false;

        TaskRequestDTO request = new TaskRequestDTO(title, description, completed);
        TaskResponseDTO saveResponse = taskService.save(null, request);

        TaskResponseDTO deleteResponse = taskService.deleteById(saveResponse.id());
        Optional<TaskResponseDTO> findResponse = taskService.findById(saveResponse.id());

        assertEquals(saveResponse.id(), deleteResponse.id());
        assertEquals(saveResponse.title(), deleteResponse.title());
        assertEquals(saveResponse.description(), deleteResponse.description());
        assertEquals(saveResponse.completed(), deleteResponse.completed());
        assertTrue(findResponse.isEmpty());
    }

    @Test
    void deleteNonExistingTask() {
        assertThrows(TaskNotFoundException.class, () -> taskService.deleteById(1));
    }

    @Test
    void saveSeveralTasksAndDeleteAll() {
        int taskAmount = 20;
        String titlePrefix = "TASK";
        String descriptionPrefix = "DESC";
        boolean defaultCompleted = false;
        List<TaskResponseDTO> saveResponseList = new ArrayList<>(taskAmount);

        for (int i = 1; i <= taskAmount; ++i) {
            saveResponseList.add(taskService.save(null, new TaskRequestDTO(titlePrefix + i, descriptionPrefix + i, defaultCompleted)));
        }

        taskService.deleteAll();
        List<TaskResponseDTO> responseList = taskService.findAll();

        assertTrue(responseList.isEmpty());
    }

    @Test
    void saveTaskAndFindByTitle() {
        String title = "TITLE";
        String description = "DESC";
        boolean completed = false;

        TaskRequestDTO request = new TaskRequestDTO(title, description, completed);
        TaskResponseDTO saveResponse = taskService.save(null, request);

        Optional<TaskResponseDTO> findResponse = taskService.findByTitle(title);
        assertTrue(findResponse.isPresent());
        assertEquals(saveResponse.id(), findResponse.get().id());
        assertEquals(title, findResponse.get().title());
        assertEquals(description, findResponse.get().description());
        assertEquals(completed, findResponse.get().completed());
    }
}