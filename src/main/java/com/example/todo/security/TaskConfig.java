package com.example.todo.security;

import com.example.todo.repository.TaskRepository;
import com.example.todo.service.TaskService;
import com.example.todo.service.UserService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author PAQUIN Pierre
 */
@Configuration
public class TaskConfig {

    @Bean
    public TaskService taskService(final TaskRepository taskRepository, final UserService userService)
            throws Exception {
        return new TaskService(taskRepository, userService);
    }
}
