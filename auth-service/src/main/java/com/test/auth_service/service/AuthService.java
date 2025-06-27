package com.test.auth_service.service;


import com.test.auth_service.dto.AuthRequest;
import com.test.auth_service.dto.AuthResponse;
import com.test.auth_service.model.User;
import com.test.auth_service.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse authenticate(AuthRequest request) {

        User user = userRepository.findByUsername(request.getUsername()).orElseThrow(() -> new BadCredentialsException("Credenciales invalidas"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Credenciales inválidas");
        }

        String token = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .build();
    }


    @PostConstruct
    public void insertUsersIfNotExist() {
        if (!userRepository.existsByUsername("admin")) {
            userRepository.save(new User(null, "admin", passwordEncoder.encode("admin123"), "admin@admin.com", "ADMIN", LocalDateTime.now(), LocalDateTime.now()));
        }
        if (!userRepository.existsByUsername("user")) {
            userRepository.save(new User(null, "user", passwordEncoder.encode("admin123"), "user@user.com", "USER", LocalDateTime.now(), LocalDateTime.now()));
        }
    }
}