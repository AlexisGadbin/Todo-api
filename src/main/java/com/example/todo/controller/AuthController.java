package com.example.todo.controller;

import com.example.todo.dto.AuthRequest;
import com.example.todo.dto.AuthResponse;
import com.example.todo.dto.RegisterRequest;
import com.example.todo.security.JwtUtil;
import com.example.todo.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authManager;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest req) {
        userService.register(req.getUsername(), req.getPassword());
        return ResponseEntity.ok("User created");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest req) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));
        UserDetails user = (UserDetails) auth.getPrincipal();
        String token = jwtUtil.generateToken(user);
        return ResponseEntity.ok(new AuthResponse(token));
    }
}