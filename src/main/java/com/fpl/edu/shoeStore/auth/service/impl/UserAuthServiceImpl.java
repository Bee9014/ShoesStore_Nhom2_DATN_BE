package com.fpl.edu.shoeStore.auth.service.impl;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fpl.edu.shoeStore.auth.converter.UserAuthConverter;
import com.fpl.edu.shoeStore.auth.dto.request.RegisterRequestDto;
import com.fpl.edu.shoeStore.auth.dto.response.UserAuthResponseDto;
import com.fpl.edu.shoeStore.auth.mapper.UserAuthMapper;
import com.fpl.edu.shoeStore.auth.service.UserAuthService;
import com.fpl.edu.shoeStore.user.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserAuthServiceImpl implements UserAuthService {

    private final UserAuthMapper userAuthMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserAuthResponseDto findUserByUserName(String userName) {
        UserAuthResponseDto userAuthResponseDto = UserAuthConverter.toDto(userAuthMapper.findUserByUsername(userName));
        return userAuthResponseDto;
    }

    @Override
    public boolean checkLoginByUserNameAndPassword(String userName, String password) {
        UserAuthResponseDto userAuthResponseDto = UserAuthConverter.toDto(userAuthMapper.findUserByUsername(userName));
        if (userAuthResponseDto == null) {
            return false;
        }

        String storedPassword = userAuthMapper.findUserByUsername(userName).getPasswordHash();
        return passwordEncoder.matches(password, storedPassword);
    }

    @Override
    public Integer getRoleIdByUserName(String userName) {
        return userAuthMapper.getRoleIdByUsername(userName);
    }

    @Override
    public boolean existsByUsername(String username) {
        Integer count = userAuthMapper.existsByUsername(username);
        return count != null && count > 0;
    }

    @Override
    public boolean existsByEmail(String email) {
        Integer count = userAuthMapper.existsByEmail(email);
        return count != null && count > 0;
    }

    @Override
    public boolean existsByPhone(String phone) {
        Integer count = userAuthMapper.existsByPhone(phone);
        return count != null && count > 0;
    }

    @Override
    public User registerUser(RegisterRequestDto registerRequest) {
        User user = User.builder()
                .username(registerRequest.getUsername())
                .passwordHash(passwordEncoder.encode(registerRequest.getPassword()))
                .fullName(registerRequest.getFullName())
                .email(registerRequest.getEmail())
                .email(registerRequest.getEmail())
                .phone(registerRequest.getPhone())
                .gender(registerRequest.getGender() != null ? registerRequest.getGender() : 0) // Mặc định 0 nếu null
                .birthday(registerRequest.getBirthday())
                .roleId(2) // Default role: USER
                .status("active")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        userAuthMapper.insertUser(user);
        return user;
    }
}
