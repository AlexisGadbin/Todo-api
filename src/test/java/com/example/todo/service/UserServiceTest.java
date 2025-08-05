package com.example.todo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.todo.entity.Role;
import com.example.todo.entity.User;
import com.example.todo.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserService userService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        this.userService = new UserService(this.userRepository, this.passwordEncoder);

        this.mockUser = new User();
        this.mockUser.setId(1L);
        this.mockUser.setUsername("testUser");
        this.mockUser.setPassword("encodedPassword");
        this.mockUser.getRoles().add(Role.ROLE_USER);
    }

    @Test
    void shouldRegisterUser_whenUsernameIsNew_givenValidCredentials() {
        when(this.userRepository.findByUsername("testUser")).thenReturn(Optional.empty());
        when(this.passwordEncoder.encode("network")).thenReturn("encodedPassword");
        when(this.userRepository.save(any(User.class))).thenReturn(this.mockUser);

        this.userService.register("testUser", "network");

        verify(this.userRepository).findByUsername("testUser");
        verify(this.passwordEncoder).encode("network");
        verify(this.userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowException_whenUsernameExists_givenExistingUser() {
        when(this.userRepository.findByUsername("existingUser")).thenReturn(Optional.of(this.mockUser));

        assertThrows(RuntimeException.class, () -> {
            this.userService.register("existingUser", "network");
        });

        verify(this.userRepository).findByUsername("existingUser");
        verify(this.passwordEncoder, never()).encode(anyString());
        verify(this.userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldReturnUser_whenFindingByUsername_givenExistingUser() {
        when(this.userRepository.findByUsername("testUser")).thenReturn(Optional.of(this.mockUser));

        Optional<User> result = this.userService.findByUsername("testUser");

        assertTrue(result.isPresent());
        assertEquals(this.mockUser, result.get());

        verify(this.userRepository).findByUsername("testUser");
    }

    @Test
    void shouldReturnEmpty_whenFindingByUsername_givenNonExistentUser() {
        when(this.userRepository.findByUsername("nonExistentUser")).thenReturn(Optional.empty());

        Optional<User> result = this.userService.findByUsername("nonExistentUser");

        assertFalse(result.isPresent());

        verify(this.userRepository).findByUsername("nonExistentUser");
    }

    @Test
    void shouldReturnUserDetails_whenLoadingByUsername_givenExistingUser() {
        when(this.userRepository.findByUsername("testUser")).thenReturn(Optional.of(this.mockUser));

        UserDetails result = this.userService.loadUserByUsername("testUser");

        assertNotNull(result);
        assertEquals("testUser", result.getUsername());
        assertEquals("encodedPassword", result.getPassword());
        assertEquals(1, result.getAuthorities().size());

        verify(this.userRepository).findByUsername("testUser");
    }

    @Test
    void shouldThrowException_whenLoadingByUsername_givenNonExistentUser() {
        when(this.userRepository.findByUsername("nonExistentUser")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            this.userService.loadUserByUsername("nonExistentUser");
        });

        verify(this.userRepository).findByUsername("nonExistentUser");
    }
}
