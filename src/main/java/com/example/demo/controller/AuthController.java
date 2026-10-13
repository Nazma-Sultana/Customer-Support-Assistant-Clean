package com.example.demo.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.LoginRequest;
import com.example.demo.security.JwtUtils;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public AuthController(AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    // POST /api/auth/login — authenticate user and return JWT token
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest loginRequest) {
        // Step 1: wrap credentials into a token Spring Security understands
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                );

        // Step 2: trigger full authentication — loads user from DB, verifies BCrypt password
        // throws BadCredentialsException automatically if credentials are wrong
        Authentication authentication = authenticationManager.authenticate(authToken);

        // Step 3: get the authenticated username from the result
        String username = authentication.getName();

        // Step 4: generate JWT token using the username
        String token = jwtUtils.generateToken(username);

        // Step 5: return token as JSON  { "token": "eyJhbGci..." }
        return ResponseEntity.ok(Map.of("token", token));
    }
}

