package com.jewelry.jewelryshopbackend.service.auth;

import com.jewelry.jewelryshopbackend.dto.request.auth.LoginRequest;
import com.jewelry.jewelryshopbackend.dto.request.auth.GoogleLoginRequest;
import com.jewelry.jewelryshopbackend.dto.request.auth.RegisterRequest;
import com.jewelry.jewelryshopbackend.dto.response.auth.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse googleLogin(GoogleLoginRequest request);
}
