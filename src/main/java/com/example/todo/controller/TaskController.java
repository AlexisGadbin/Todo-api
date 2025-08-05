package com.example.todo.controller;

import com.example.todo.dto.TaskDTO;
import com.example.todo.entity.Task;
import com.example.todo.mapper.TaskMapper;
import com.example.todo.service.TaskService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final TaskMapper taskMapper;

    @GetMapping
    public List<TaskDTO> listTasks(Principal principal) {
        return this.taskMapper.toDTOList(taskService.getTasksForUser(principal.getName()));
    }

    @PostMapping
    public ResponseEntity<TaskDTO> create(@RequestBody TaskDTO taskDTO, Principal principal) {
        Task task = this.taskMapper.toEntity(taskDTO);
        ResponseEntity<Task> response = taskService.create(task, principal.getName());

        return ResponseEntity.status(response.getStatusCode())
                .body(this.taskMapper.toDTO(response.getBody()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> update(@PathVariable Long id, @RequestBody TaskDTO taskDTO, Principal principal) {
        Task task = this.taskMapper.toEntity(taskDTO);
        ResponseEntity<Task> response = taskService.update(id, task, principal.getName());

        return ResponseEntity.status(response.getStatusCode())
                .body(this.taskMapper.toDTO(response.getBody()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Principal principal) {
        return taskService.delete(id, principal.getName());
    }
}