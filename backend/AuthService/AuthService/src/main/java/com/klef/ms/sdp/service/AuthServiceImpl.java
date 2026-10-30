package com.klef.ms.sdp.service;

import com.klef.ms.sdp.dto.AuthResponse;
import com.klef.ms.sdp.dto.LoginRequest;
import com.klef.ms.sdp.dto.RegisterRequest;
import com.klef.ms.sdp.entity.User;
import com.klef.ms.sdp.repository.UserRepository;
import com.klef.ms.sdp.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        User savedUser = userRepository.save(user);

        String token = jwtUtil.generateToken(
                savedUser.getUsername(),
                savedUser.getRole()
        );

        return new AuthResponse(
                token,
                savedUser.getUsername(),
                savedUser.getRole()
        );
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        String token = jwtUtil.generateToken(
                user.getUsername(),
                user.getRole()
        );

        return new AuthResponse(
                token,
                user.getUsername(),
                user.getRole()
        );
    }
}