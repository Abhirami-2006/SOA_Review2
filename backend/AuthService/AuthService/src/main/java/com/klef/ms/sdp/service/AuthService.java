package com.klef.ms.sdp.service;

import com.klef.ms.sdp.dto.AuthResponse;
import com.klef.ms.sdp.dto.LoginRequest;
import com.klef.ms.sdp.dto.RegisterRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}