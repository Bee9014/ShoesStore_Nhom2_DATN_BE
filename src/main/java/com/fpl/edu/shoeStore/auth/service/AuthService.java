package com.fpl.edu.shoeStore.auth.service;

import com.fpl.edu.shoeStore.auth.dto.request.LoginRequestDto;
import com.fpl.edu.shoeStore.auth.dto.request.RegisterRequestDto;
import org.springframework.http.ResponseEntity;

public interface AuthService {

    ResponseEntity<?> login(LoginRequestDto loginRequestDto);

    ResponseEntity<?> register(RegisterRequestDto registerRequestDto);

    ResponseEntity<?> refresh(String token);

    ResponseEntity<?> logout();

    ResponseEntity<?> getCurrentUser();

    String getUsernameFromAccessToken(String token);
}
