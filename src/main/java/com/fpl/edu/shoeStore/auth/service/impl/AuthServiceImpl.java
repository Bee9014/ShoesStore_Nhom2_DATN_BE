package com.fpl.edu.shoeStore.auth.service.impl;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fpl.edu.shoeStore.auth.dto.request.LoginRequestDto;
import com.fpl.edu.shoeStore.auth.dto.request.RegisterRequestDto;
import com.fpl.edu.shoeStore.auth.dto.response.UserAuthResponseDto;
import com.fpl.edu.shoeStore.auth.security.JwtUtil;
import com.fpl.edu.shoeStore.auth.service.AuthService;
import com.fpl.edu.shoeStore.auth.service.UserAuthService;
import com.fpl.edu.shoeStore.common.enums.ErrorCode;
import com.fpl.edu.shoeStore.common.handler.ApiResponse;
import com.fpl.edu.shoeStore.user.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final JwtUtil jwtUtil;
    private final UserAuthService userAuthService;

    @Override
    public ResponseEntity<?> login(LoginRequestDto req) {
        String username = req.getUsername();
        String password = req.getPassword();

        if (!userAuthService.checkLoginByUserNameAndPassword(username, password)) {
            return buildErrorResponse(HttpStatus.UNAUTHORIZED, ErrorCode.INVALID_CREDENTIALS);
        }

        Integer roleId = userAuthService.getRoleIdByUserName(username);
        if (roleId == null) {
            return buildErrorResponse(HttpStatus.LOCKED, ErrorCode.ACCOUNT_LOCKED);
        }

        String accessToken = jwtUtil.generateAccessToken(username, roleId);
        String refreshToken = jwtUtil.generateRefreshToken(username, roleId);
        UserAuthResponseDto loggedUser = userAuthService.findUserByUserName(username);

        //trả về dữ liệu với accessToken, refreshToken, user info
        Map<String, Object> data = new HashMap<>();
        data.put("accessToken", accessToken);
        data.put("refreshToken", refreshToken);
        data.put("user", loggedUser);

        // xài chung apiresponse để vue nhận chung format
        ApiResponse<Map<String, Object>> apiResponse = ApiResponse.<Map<String, Object>>builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message("Đăng nhập thành công")
                .data(data)
                .build();

        ResponseCookie cookie = createRefreshTokenCookie(refreshToken);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(apiResponse);
    }

    @Override
    public ResponseEntity<?> register(RegisterRequestDto registerRequest) {
        if (userAuthService.existsByUsername(registerRequest.getUsername())) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, ErrorCode.DUPLICATE_USERNAME);
        }

        if (registerRequest.getEmail() != null && userAuthService.existsByEmail(registerRequest.getEmail())) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, ErrorCode.DUPLICATE_EMAIL);
        }

        if (registerRequest.getPhone() != null && userAuthService.existsByPhone(registerRequest.getPhone())) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, ErrorCode.DUPLICATE_PHONE);
        }

        try {
            User newUser = userAuthService.registerUser(registerRequest);
            
            ApiResponse<User> apiResponse = ApiResponse.<User>builder()
                    .success(true)
                    .statusCode(HttpStatus.CREATED.value())
                    .message(ErrorCode.REGISTER_SUCCESS.getMessage())
                    .data(newUser)
                    .build();
            
            return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
        } catch (Exception e) {
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.FAILED_TO_CREATE_ACCOUNT);
        }
    }

    @Override
    public ResponseEntity<?> refresh(String token) {
        if (token == null || !jwtUtil.isValid(token)) {
            return buildErrorResponse(HttpStatus.UNAUTHORIZED, ErrorCode.INVALID_REFRESH_TOKEN);
        }

        String username = jwtUtil.getUsernameFromToken(token);
        int roleId = jwtUtil.getRoleIdFromToken(token);
        String newAccessToken = jwtUtil.generateAccessToken(username, roleId);

        Map<String, String> data = new HashMap<>();
        data.put("accessToken", newAccessToken);
        
        ApiResponse<Map<String, String>> apiResponse = ApiResponse.<Map<String, String>>builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message("Token đã được làm mới")
                .data(data)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @Override
    public ResponseEntity<?> logout() {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .path("/api/v1/auth/refresh")
                .maxAge(0)
                .build();

        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message(ErrorCode.LOGOUT_SUCCESS.getMessage())
                .data(null)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(apiResponse);
    }

    @Override
    public String getUsernameFromAccessToken(String token) {
        return jwtUtil.getUsernameFromToken(token);
    }

    private ResponseEntity<?> buildErrorResponse(HttpStatus status, ErrorCode errorCode) {
        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .success(false)
                .statusCode(status.value())
                .message(errorCode.getMessage())
                .data(null)
                .build();
        
        return ResponseEntity.status(status).body(apiResponse);
    }

    private ResponseCookie createRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .path("/api/v1/auth/refresh")
                .maxAge(Duration.ofDays(7))
                .build();
    }
}
