package com.mathan.expensesplitter.service.impl;

import com.mathan.expensesplitter.dto.auth.LoginRequest;
import com.mathan.expensesplitter.dto.auth.LoginResponse;
import com.mathan.expensesplitter.dto.auth.RegisterRequest;
import com.mathan.expensesplitter.dto.auth.RegisterResponse;
import com.mathan.expensesplitter.entity.User;
import com.mathan.expensesplitter.exception.UserAlreadyExistsException;
import com.mathan.expensesplitter.repository.UserRepository;
import com.mathan.expensesplitter.security.JwtService;
import com.mathan.expensesplitter.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public RegisterResponse register(RegisterRequest request) {
        log.info("Attempting user registration for email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration failed. Email already exists: {}", request.getEmail());
            throw new UserAlreadyExistsException("Email already registered");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {} and email: {}", savedUser.getId(), savedUser.getEmail());

        return RegisterResponse.builder()
                .id(savedUser.getId())
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .build();
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        log.info("Attempting user login for email: {}", request.getEmail());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtService.generateToken(user.getEmail());
        log.info("User logged in successfully with email: {}", user.getEmail());

        return LoginResponse.builder()
                .token(token)
                .type("Bearer")
                .build();
    }
}