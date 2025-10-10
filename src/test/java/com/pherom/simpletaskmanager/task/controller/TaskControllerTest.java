package com.pherom.simpletaskmanager.task.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pherom.simpletaskmanager.task.dto.TaskRequestDTO;
import com.pherom.simpletaskmanager.task.dto.TaskResponseDTO;
import com.pherom.simpletaskmanager.task.exception.TaskNotFoundException;
import com.pherom.simpletaskmanager.task.service.TaskService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllTasks_ShouldReturnListOfEmptyTasks() throws Exception {
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getAllTasks_ShouldReturnListWithAllTasks() throws Exception {
        List<TaskResponseDTO> tasks = List.of(
                new TaskResponseDTO(1, "TASK1", "DESC1", false),
                new TaskResponseDTO(2, "TASK2", "DESC2", false),
                new TaskResponseDTO(3, "TASK3", "DESC3", true)
        );

        when(taskService.findAll()).thenReturn(tasks);

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("TASK1"))
                .andExpect(jsonPath("$[0].description").value("DESC1"))
                .andExpect(jsonPath("$[0].completed").value(false))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title").value("TASK2"))
                .andExpect(jsonPath("$[1].description").value("DESC2"))
                .andExpect(jsonPath("$[1].completed").value(false))
                .andExpect(jsonPath("$[2].id").value(3))
                .andExpect(jsonPath("$[2].title").value("TASK3"))
                .andExpect(jsonPath("$[2].description").value("DESC3"))
                .andExpect(jsonPath("$[2].completed").value(true));
    }

    @Test
    void getTaskById_ShouldReturn404NotFound() throws Exception {
        when(taskService.findById(1)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getTaskById_ShouldReturnTask() throws Exception {
        TaskResponseDTO responseDTO = new TaskResponseDTO(1, "TASK1", "DESC1", false);

        when(taskService.findById(1)).thenReturn(Optional.of(responseDTO));

        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("TASK1"))
                .andExpect(jsonPath("$.description").value("DESC1"))
                .andExpect(jsonPath("$.completed").value(false));
    }

    @Test
    void createTask_ShouldCreateTaskAndReturnCreatedWithLocationHeader() throws Exception {
        String title = "TASK";
        String description = "DESC";
        boolean completed = false;

        TaskRequestDTO requestDTO = new TaskRequestDTO(title, description, completed);
        TaskResponseDTO responseDTO = new TaskResponseDTO(1, title, description, completed);

        when(taskService.save(null, requestDTO)).thenReturn(responseDTO);

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", Matchers.endsWith("/api/tasks/1")))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value(title))
                .andExpect(jsonPath("$.description").value(description))
                .andExpect(jsonPath("$.completed").value(completed));
    }

    @Test
    void createEmptyTitledTask_ShouldReturnBadRequest() throws Exception {
        TaskRequestDTO requestDTO = new TaskRequestDTO("", "DESC", false);

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(Matchers.containsString("title: must not be blank")));
    }

    @Test
    void createLongTitledTask_ShouldReturnBadRequest() throws Exception {
        TaskRequestDTO requestDTO = new TaskRequestDTO("INVALIDTASK INVALIDTASK INVALIDTASK INVALIDTASK INVALIDTASK", "DESC", false);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(Matchers.containsString("title: size must be between 0 and 50")));
    }

    @Test
    void createLongDescribedTask_ShouldReturnBadRequest() throws Exception {
        TaskRequestDTO requestDTO = new TaskRequestDTO("TASK", "INVALIDDESC INVALIDDESC INVALIDDESC INVALIDDESC INVALIDDESC INVALIDDESC INVALIDDESC INVALIDDESC INVALIDDESC INVALIDDESC INVALIDDESC INVALIDDESC INVALIDDESC INVALIDDESC INVALIDDESC INVALIDDESC INVALIDDESC INVALIDDESC INVALIDDESC INVALIDDESC INVALIDDESC INVALIDDESC INVALIDDESC INVALIDDESC INVALIDDESC", false);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(Matchers.containsString("description: size must be between 0 and 255")));
    }

    @Test
    void saveTask_ShouldReturnUpdatedTask() throws Exception {
        String updatedTitle = "UPDATED_TASK";
        String updatedDesc = "UPDATED_DESC";
        boolean updatedComp = true;

        TaskResponseDTO existing = new TaskResponseDTO(1, "TASK", "DESCRIPTION", false);
        TaskRequestDTO updateRequest = new TaskRequestDTO(updatedTitle, updatedDesc, updatedComp);
        TaskResponseDTO updated = new TaskResponseDTO(1, updatedTitle, updatedDesc, updatedComp);

        when(taskService.findById(1)).thenReturn(Optional.of(existing));
        when(taskService.save(1L, updateRequest)).thenReturn(updated);

        mockMvc.perform(put("/api/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value(updatedTitle))
                .andExpect(jsonPath("$.description").value(updatedDesc))
                .andExpect(jsonPath("$.completed").value(updatedComp));
    }

    @Test
    void saveTask_ShouldReturn404NotFound() throws Exception {
        TaskRequestDTO requestDTO = new TaskRequestDTO("UPDATED_TASK", "UPDATED_DESC", true);

        when(taskService.save(1L, requestDTO)).thenThrow(new TaskNotFoundException(1));

        mockMvc.perform(put("/api/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void saveUpdatedEmptyTitledTask_ShouldReturnBadRequest() throws Exception {
        TaskRequestDTO requestDTO = new TaskRequestDTO("", "DESC", false);

        mockMvc.perform(put("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(Matchers.containsString("title: must not be blank")));
    }

    @Test
    void saveUpdatedLongTitledTask_ShouldReturnBadRequest() throws Exception {
        TaskRequestDTO requestDTO = new TaskRequestDTO("INVALIDTASK INVALIDTASK INVALIDTASK INVALIDTASK INVALIDTASK", "DESC", false);

        mockMvc.perform(put("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(Matchers.containsString("title: size must be between 0 and 50")));
    }

    @Test
    void saveUpdatedLongDescribedTask_ShouldReturnBadRequest() throws Exception {
        TaskRequestDTO requestDTO = new TaskRequestDTO("TASK", "INVALIDDESC INVALIDDESC INVALIDDESC INVALIDDESC INVALIDDESC INVALIDDESC INVALIDDESC INVALIDDESC INVALIDDESC INVALIDDESC INVALIDDESC INVALIDDESC INVALIDDESC INVALIDDESC INVALIDDESC INVALIDDESC INVALIDDESC INVALIDDESC INVALIDDESC INVALIDDESC INVALIDDESC INVALIDDESC INVALIDDESC INVALIDDESC INVALIDDESC", false);

        mockMvc.perform(put("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(Matchers.containsString("description: size must be between 0 and 255")));
    }

    @Test
    void removeTask_ShouldReturnDeletedTask() throws Exception {
        String deletedTitle = "TASK";
        String deletedDesc = "DESC";
        boolean deletedComp = false;

        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void removeNonExistentTask_ShouldReturnNotFound() throws Exception {
        doThrow(new TaskNotFoundException(1)).when(taskService).deleteById(1);

        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isNotFound());
    }
}