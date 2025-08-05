package com.example.todo.service;

import com.example.todo.entity.Role;
import com.example.todo.entity.Task;
import com.example.todo.entity.User;
import com.example.todo.repository.TaskRepository;
import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
public class TaskService {
    private final TaskRepository taskRepo;
    private final UserService userService;

    public List<Task> getTasksForUser(String username) {
        return taskRepo.findByOwnerUsername(username);
    }

    @Transactional
    public ResponseEntity<Task> create(Task task, String username) {
        User owner = userService.findByUsername(username).orElseThrow();
        if (!canUserCreateOrUpdateTask(owner.getRoles(), task)) {
            return ResponseEntity.status(403).build();
        }
        task.setOwner(owner);
        Task createdTask = taskRepo.save(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    @Transactional
    public ResponseEntity<Task> update(Long id, Task task, String username) {
        Task existing = taskRepo.findById(id).orElseThrow();
        if (!existing.getOwner().getUsername().equals(username))
            return ResponseEntity.status(403).build();

        if (!canUserCreateOrUpdateTask(existing.getOwner().getRoles(), task)) {
            return ResponseEntity.status(403).build();
        }
        existing.setTitle(task.getTitle());
        existing.setDescription(task.getDescription());
        existing.setDone(task.isDone());
        return ResponseEntity.ok(taskRepo.save(existing));
    }

    @Transactional
    public ResponseEntity<?> delete(Long id, String username) {
        Task existing = taskRepo.findById(id).orElseThrow();
        if (!existing.getOwner().getUsername().equals(username))
            return ResponseEntity.status(403).build();
        taskRepo.delete(existing);
        return ResponseEntity.noContent().build();
    }

    private boolean canUserCreateOrUpdateTask(Set<Role> userRoles, Task task) {
        if (!task.isDone()) {
            return true;
        }

        return userRoles.contains(Role.ROLE_ADMIN);
    }
}