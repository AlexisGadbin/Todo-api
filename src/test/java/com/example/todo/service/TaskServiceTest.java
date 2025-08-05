package com.example.todo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.todo.entity.Role;
import com.example.todo.entity.Task;
import com.example.todo.entity.User;
import com.example.todo.repository.TaskRepository;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserService userService;

    private TaskService taskService;

    private User mockUser;
    private User mockAdmin;

    @BeforeEach
    void setUp() {
        this.taskService = new TaskService(this.taskRepository, this.userService);

        this.mockUser = new User();
        this.mockUser.setUsername("testUser");
        this.mockUser.setPassword("network");
        this.mockUser.getRoles().add(Role.ROLE_USER);

        this.mockAdmin = new User();
        this.mockAdmin.setUsername("testAdmin");
        this.mockAdmin.setPassword("network");
        this.mockAdmin.getRoles().add(Role.ROLE_ADMIN);
    }

    @Test
    void shouldCreateTask_whenUserHasUserRole_givenTaskNotDone() {
        when(this.userService.findByUsername("testUser")).thenReturn(Optional.of(this.mockUser));

        Task taskToCreate = new Task();
        taskToCreate.setTitle("Titre");
        taskToCreate.setDescription("Description");
        taskToCreate.setDone(false);

        ResponseEntity<Task> response = this.taskService.create(taskToCreate, "testUser");

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(this.taskRepository).save(any(Task.class));
    }

    @Test
    void shouldReturnForbidden_whenUserHasUserRole_givenTaskIsDone() {
        when(this.userService.findByUsername("testUser")).thenReturn(Optional.of(this.mockUser));

        Task taskToCreate = new Task();
        taskToCreate.setTitle("Titre");
        taskToCreate.setDescription("Description");
        taskToCreate.setDone(true);

        ResponseEntity<Task> response = this.taskService.create(taskToCreate, "testUser");

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(this.taskRepository, never()).save(any(Task.class));
    }

    @Test
    void shouldCreateTask_whenUserHasAdminRole_givenTaskIsDone() {
        when(this.userService.findByUsername("testAdmin")).thenReturn(Optional.of(this.mockAdmin));
        when(taskRepository.save(any(Task.class))).thenReturn(new Task());

        Task taskToCreate = new Task();
        taskToCreate.setTitle("Titre");
        taskToCreate.setDescription("Description");
        taskToCreate.setDone(true);

        ResponseEntity<Task> response = this.taskService.create(taskToCreate, "testAdmin");

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(this.taskRepository).save(any(Task.class));
    }

    @Test
    void shouldReturnUserTasks_whenGettingTasks_givenValidUsername() {
        Task task1 = new Task();
        task1.setId(1L);
        Task task2 = new Task();
        task2.setId(2L);
        List<Task> expectedTasks = Arrays.asList(task1, task2);

        when(this.taskRepository.findByOwnerUsername("testUser")).thenReturn(expectedTasks);

        List<Task> result = this.taskService.getTasksForUser("testUser");

        assertEquals(expectedTasks, result);
        verify(this.taskRepository).findByOwnerUsername("testUser");
    }

    @Test
    void shouldUpdateTask_whenUserIsOwner_givenValidTaskData() {
        Task existingTask = new Task();
        existingTask.setId(1L);
        existingTask.setOwner(this.mockUser);
        existingTask.setTitle("Titre");
        existingTask.setDescription("Description");
        existingTask.setDone(false);

        Task updatedTask = new Task();
        updatedTask.setTitle("Titre modif");
        updatedTask.setDescription("Description modif");
        updatedTask.setDone(false);

        when(this.taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
        when(this.taskRepository.save(any(Task.class))).thenReturn(existingTask);

        ResponseEntity<?> response = this.taskService.update(1L, updatedTask, "testUser");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Titre modif", existingTask.getTitle());
        assertEquals("Description modif", existingTask.getDescription());
        verify(this.taskRepository).save(existingTask);
    }

    @Test
    void shouldReturnForbidden_whenUserIsNotOwner_givenTaskUpdate() {
        User otherUser = new User();
        otherUser.setUsername("otherUser");

        Task existingTask = new Task();
        existingTask.setId(1L);
        existingTask.setOwner(otherUser);

        Task updatedTask = new Task();
        updatedTask.setTitle("Titre");

        when(this.taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));

        ResponseEntity<?> response = this.taskService.update(1L, updatedTask, "testUser");

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(this.taskRepository, never()).save(any(Task.class));
    }

    @Test
    void shouldReturnForbidden_whenUserHasUserRole_givenTaskSetToDone() {
        Task existingTask = new Task();
        existingTask.setId(1L);
        existingTask.setOwner(this.mockUser);
        existingTask.setDone(false);

        Task updatedTask = new Task();
        updatedTask.setTitle("Titre");
        updatedTask.setDescription("Description");
        updatedTask.setDone(true);

        when(this.taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));

        ResponseEntity<?> response = this.taskService.update(1L, updatedTask, "testUser");

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(this.taskRepository, never()).save(any(Task.class));
    }

    @Test
    void shouldUpdateTask_whenUserHasAdminRole_givenTaskSetToDone() {
        Task existingTask = new Task();
        existingTask.setId(1L);
        existingTask.setOwner(this.mockAdmin);
        existingTask.setDone(false);

        Task updatedTask = new Task();
        updatedTask.setTitle("Titre");
        updatedTask.setDescription("Description");
        updatedTask.setDone(true);

        when(this.taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
        when(this.taskRepository.save(any(Task.class))).thenReturn(existingTask);

        ResponseEntity<?> response = taskService.update(1L, updatedTask, "testAdmin");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(existingTask.isDone());
        verify(this.taskRepository).save(existingTask);
    }

    @Test
    void shouldDeleteTask_whenUserIsOwner_givenValidTaskId() {
        Task existingTask = new Task();
        existingTask.setId(1L);
        existingTask.setOwner(this.mockUser);

        when(this.taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));

        ResponseEntity<?> response = this.taskService.delete(1L, "testUser");

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(this.taskRepository).delete(existingTask);
    }

    @Test
    void shouldReturnForbidden_whenUserIsNotOwner_givenTaskDeletion() {
        User otherUser = new User();
        otherUser.setUsername("otherUser");

        Task existingTask = new Task();
        existingTask.setId(1L);
        existingTask.setOwner(otherUser);

        when(this.taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));

        ResponseEntity<?> response = this.taskService.delete(1L, "testUser");

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(this.taskRepository, never()).delete(any(Task.class));
    }
}
