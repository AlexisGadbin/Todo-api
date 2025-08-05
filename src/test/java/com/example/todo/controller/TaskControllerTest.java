package com.example.todo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.todo.dto.TaskDTO;
import com.example.todo.entity.Role;
import com.example.todo.entity.Task;
import com.example.todo.entity.User;
import com.example.todo.mapper.TaskMapper;
import com.example.todo.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

        @Mock
        private TaskService taskService;

        @Mock
        private Principal principal;

        private TaskController taskController;
        private TaskMapper taskMapper;

        private MockMvc mockMvc;
        private ObjectMapper objectMapper;

        private User mockUser;
        private Task mockTask;

        @BeforeEach
        void setUp() {
                this.taskMapper = new TaskMapper();
                this.taskController = new TaskController(this.taskService, this.taskMapper);
                this.mockMvc = MockMvcBuilders.standaloneSetup(this.taskController).build();
                this.objectMapper = new ObjectMapper();

                this.mockUser = new User();
                this.mockUser.setId(1L);
                this.mockUser.setUsername("testUser");
                this.mockUser.setPassword("encodedPassword");
                this.mockUser.getRoles().add(Role.ROLE_USER);

                this.mockTask = new Task();
                this.mockTask.setId(1L);
                this.mockTask.setTitle("Titre");
                this.mockTask.setDescription("Description");
                this.mockTask.setDone(false);
                this.mockTask.setOwner(this.mockUser);

                when(this.principal.getName()).thenReturn("testUser");
        }

        @Test
        void shouldReturnUserTasks_whenListingTasks_givenAuthenticatedUser() throws Exception {
                List<Task> tasks = Arrays.asList(this.mockTask);
                when(this.taskService.getTasksForUser("testUser")).thenReturn(tasks);

                this.mockMvc.perform(get("/tasks")
                                .principal(this.principal))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$").isArray())
                                .andExpect(jsonPath("$.length()").value(1))
                                .andExpect(jsonPath("$[0].title").value("Titre"))
                                .andExpect(jsonPath("$[0].description").value("Description"))
                                .andExpect(jsonPath("$[0].done").value(false));
        }

        @Test
        void shouldReturnCreatedTask_whenCreatingTask_givenValidTaskData() throws Exception {
                TaskDTO taskToCreate = new TaskDTO();
                taskToCreate.setTitle("Titre");
                taskToCreate.setDescription("Description");
                taskToCreate.setDone(false);

                when(this.taskService.create(any(Task.class), eq("testUser")))
                                .thenReturn(new ResponseEntity<>(this.mockTask, HttpStatus.CREATED));

                this.mockMvc.perform(post("/tasks")
                                .principal(this.principal)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(this.objectMapper.writeValueAsString(taskToCreate)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.title").value("Titre"))
                                .andExpect(jsonPath("$.description").value("Description"))
                                .andExpect(jsonPath("$.done").value(false));
        }

        @Test
        void shouldReturnUpdatedTask_whenUpdatingTask_givenValidTaskData() throws Exception {
                TaskDTO taskToUpdate = new TaskDTO();
                taskToUpdate.setTitle("Titre");
                taskToUpdate.setDescription("Description");
                taskToUpdate.setDone(false);

                Task updatedTask = new Task();
                updatedTask.setId(1L);
                updatedTask.setTitle("Titre modif");
                updatedTask.setDescription("Description modif");
                updatedTask.setDone(false);

                when(this.taskService.update(eq(1L), any(Task.class), eq("testUser")))
                                .thenReturn(ResponseEntity.ok(updatedTask));

                this.mockMvc.perform(put("/tasks/1")
                                .principal(this.principal)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(this.objectMapper.writeValueAsString(taskToUpdate)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.title").value("Titre modif"))
                                .andExpect(jsonPath("$.description").value("Description modif"));
        }

        @Test
        void shouldReturnNoContent_whenDeletingTask_givenValidTaskId() throws Exception {
                when(this.taskService.delete(1L, "testUser"))
                                .thenReturn(new ResponseEntity<>(HttpStatus.NO_CONTENT));

                this.mockMvc.perform(delete("/tasks/1")
                                .principal(this.principal))
                                .andExpect(status().isNoContent());
        }
}
