package com.example.demo.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.dto.CreateUserRequest;
import com.example.demo.dto.UpdateUserRequest;
import com.example.demo.dto.UserResponse;
import com.example.demo.exception.APIException;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.model.Role;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getRole());
    }

    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new APIException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new APIException("Email already exists");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        //user.setRole(request.getRole());
        user.setRole(Role.CUSTOMER);
        return toResponse(userRepository.save(user));
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public Optional<UserResponse> getUserByUsername(String username) {
        return userRepository.findByUsername(username).map(this::toResponse);
    }

    public Optional<UserResponse> updateUser(Long id, UpdateUserRequest request) {
        return userRepository.findById(id).map(existingUser -> {
            existingUser.setUsername(request.getUsername());
            existingUser.setEmail(request.getEmail());
            return toResponse(userRepository.save(existingUser));
        });
    }

    public boolean deleteUser(Long id) {
        if (!userRepository.existsById(id)) { return false; }
        userRepository.deleteById(id);
        return true;
    }

    public String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}