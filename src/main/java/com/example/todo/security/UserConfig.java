package com.example.todo.security;

import com.example.todo.repository.UserRepository;
import com.example.todo.service.UserService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author PAQUIN Pierre
 */
@Configuration
public class UserConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserService userService(final UserRepository userRepository, final PasswordEncoder passwordEncoder)
            throws Exception {
        return new UserService(userRepository, passwordEncoder);
    }
}
