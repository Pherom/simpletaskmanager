package com.pherom.simpletaskmanager.task.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pherom.simpletaskmanager.task.dto.TaskResponseDTO;
import com.pherom.simpletaskmanager.task.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
}