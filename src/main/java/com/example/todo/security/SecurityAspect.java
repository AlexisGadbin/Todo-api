package com.example.todo.security;

import com.example.todo.entity.Role;
import com.example.todo.entity.Task;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import org.springframework.stereotype.Component;

/**
 * @author PAQUIN Pierre
 */
@Aspect
@Component
public class SecurityAspect {

    @Around("execution(* com.example.todo.repository.TaskRepository.save(..))")
    public Object neverDoneForAdmins(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();

        if (args.length > 0 && args[0] instanceof Task) {
            preventDoneForAdmins((Task) args[0]);
        }

        return joinPoint.proceed(args);

    }

    private void preventDoneForAdmins(final Task task) {
        if (task.getOwner().getRoles().contains(Role.ROLE_ADMIN)
                && !task.getDescription().startsWith("[Internal Use]")) {
            task.setDescription("[Internal Use] " + task.getDescription());
        }
    }
}
