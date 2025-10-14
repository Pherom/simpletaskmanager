package com.pherom.simpletaskmanager.task.service;

import com.pherom.simpletaskmanager.task.dto.TaskRequestDTO;
import com.pherom.simpletaskmanager.task.dto.TaskResponseDTO;
import com.pherom.simpletaskmanager.task.entity.Task;
import com.pherom.simpletaskmanager.task.exception.TaskNotFoundException;
import com.pherom.simpletaskmanager.task.mapper.TaskMapper;
import com.pherom.simpletaskmanager.task.repository.JpaTaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private JpaTaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskService taskService;

    @Test
    void saveNewTask_ShouldPersistAndReturnResponse() {
        String title = "TITLE";
        String description = "DESC";
        boolean completed = false;

        TaskRequestDTO request = new TaskRequestDTO(title, description, completed);

        Task toSave = new Task(title, description, completed);
        Task savedTask = new Task(1, title, description, completed);

        TaskResponseDTO expectedDTO = new TaskResponseDTO(1, title, description, completed);

        when(taskMapper.toTask(request)).thenReturn(toSave);
        when(taskRepository.save(toSave)).thenReturn(savedTask);
        when(taskMapper.toDTO(savedTask)).thenReturn(expectedDTO);

        TaskResponseDTO response = taskService.save(null, request);

        assertSame(expectedDTO, response);

        verify(taskRepository).save(toSave);
        verifyNoMoreInteractions(taskRepository);
    }

    @Test
    void saveUpdatedTask_ShouldPersistAndReturnResponse() {
        String title = "TITLE";
        String desc = "DESC";
        boolean completed = false;

        String updatedTitle = "UPDATED_TITLE";
        String updatedDesc = "UPDATED_DESC";
        boolean updatedCompleted = true;

        Task existing = new Task(1L, title, desc, completed);
        Task updatedTask = new Task(1L, updatedTitle, updatedDesc, updatedCompleted);

        TaskRequestDTO updateRequest = new TaskRequestDTO(updatedTitle, updatedDesc, updatedCompleted);
        TaskResponseDTO expectedDTO = new TaskResponseDTO(1L, updatedTitle, updatedDesc, updatedCompleted);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);
        when(taskMapper.toDTO(updatedTask)).thenReturn(expectedDTO);

        TaskResponseDTO response = taskService.save(1L, updateRequest);

        assertSame(expectedDTO, response);

        verify(taskRepository).findById(1L);
        verify(taskRepository).save(updatedTask);
        verifyNoMoreInteractions(taskRepository);
    }

    @Test
    void saveUpdateToNonExistentTask_ShouldThrowTaskNotFoundException() {
        String updatedTitle = "UPDATED_TITLE";
        String updatedDesc = "UPDATED_DESC";
        boolean updatedCompleted = true;

        TaskRequestDTO request = new TaskRequestDTO(updatedTitle, updatedDesc, updatedCompleted);

        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        TaskNotFoundException ex = assertThrows(TaskNotFoundException.class, () -> taskService.save(1L, request));

        verify(taskRepository).findById(1L);
        verifyNoMoreInteractions(taskRepository);
        assertTrue(ex.getMessage().contains("1"));
    }

    @Test
    void findTaskById_ShouldFindAndReturnResponse() {
        String title = "TITLE";
        String desc = "DESC";
        boolean completed = false;

        Task foundTask = new Task(1L, title, desc, completed);
        TaskResponseDTO expectedDTO = new TaskResponseDTO(1L, title, desc, completed);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(foundTask));
        when(taskMapper.toDTO(foundTask)).thenReturn(expectedDTO);

        Optional<TaskResponseDTO> response = taskService.findById(1L);

        verify(taskRepository).findById(1L);
        verifyNoMoreInteractions(taskRepository);
        assertTrue(response.isPresent());
        assertSame(expectedDTO, response.get());
    }

    @Test
    void findNonExistentTaskByID_ShouldReturnEmpty() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<TaskResponseDTO> response = taskService.findById(1L);

        verify(taskRepository).findById(1L);
        verifyNoMoreInteractions(taskRepository);
        assertTrue(response.isEmpty());
    }

    @Test
    void findAllWhenEmpty_ShouldReturnEmptyList() {
        when(taskRepository.findAll()).thenReturn(List.of());

        List<TaskResponseDTO> response = taskService.findAll();

        verify(taskRepository).findAll();
        verifyNoMoreInteractions(taskRepository);

        assertTrue(response.isEmpty());
    }

    @Test
    void findAllSingleTask_ShouldReturnListWithSingleTaskResponseDTO() {
        String title = "TITLE";
        String desc = "DESC";
        boolean completed = false;

        List<Task> existingTasks = List.of(new Task(1L, title, desc, completed));
        List<TaskResponseDTO> expected = List.of(new TaskResponseDTO(1L, title, desc, completed));

        when(taskRepository.findAll()).thenReturn(existingTasks);
        when(taskMapper.toDTO(existingTasks.get(0))).thenReturn(expected.get(0));

        List<TaskResponseDTO> response = taskService.findAll();

        verify(taskRepository).findAll();
        verifyNoMoreInteractions(taskRepository);

        assertEquals(expected, response);
    }

    @Test
    void deleteByTaskId_ShouldDeleteAndReturnResponseDTO() {
        String title = "TITLE";
        String desc = "DESC";
        boolean completed = false;

        Task toDelete = new Task(1, title, desc, completed);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(toDelete));

        taskService.deleteById(1L);

        verify(taskRepository).findById(1L);
        verify(taskRepository).delete(toDelete);
        verifyNoMoreInteractions(taskRepository);
    }

    @Test
    void deleteNonExistentTaskByID_ShouldThrowTaskNotFoundException() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        TaskNotFoundException ex = assertThrows(TaskNotFoundException.class, () -> taskService.deleteById(1L));

        verify(taskRepository).findById(1L);
        verifyNoMoreInteractions(taskRepository);
        assertTrue(ex.getMessage().contains("1"));
    }

    @Test
    void deleteAll_ShouldDeleteAllExistingTasks() {
        taskService.deleteAll();

        verify(taskRepository).deleteAll();
        verifyNoMoreInteractions(taskRepository);
    }

    @Test
    void findTaskByTitle_ShouldReturnResponseDTO() {
        String title = "TITLE";
        String desc = "DESC";
        boolean completed = false;

        Task foundTask = new Task(1L, title, desc, completed);
        TaskResponseDTO expectedDTO = new TaskResponseDTO(1L, title, desc, completed);

        when(taskRepository.findByTitle(title)).thenReturn(Optional.of(foundTask));
        when(taskMapper.toDTO(foundTask)).thenReturn(expectedDTO);

        Optional<TaskResponseDTO> response = taskService.findByTitle(title);

        verify(taskRepository).findByTitle(title);
        verifyNoMoreInteractions(taskRepository);

        assertTrue(response.isPresent());
        assertSame(expectedDTO, response.get());
    }

    @Test
    void findNonExistentTaskByTitle_ShouldReturnEmpty() {
        String title = "TITLE";

        when(taskRepository.findByTitle(title)).thenReturn(Optional.empty());

        Optional<TaskResponseDTO> response = taskService.findByTitle(title);

        verify(taskRepository).findByTitle(title);
        verifyNoMoreInteractions(taskRepository);

        assertTrue(response.isEmpty());
    }
}