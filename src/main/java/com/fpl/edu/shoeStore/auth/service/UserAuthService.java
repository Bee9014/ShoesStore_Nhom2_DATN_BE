package com.fpl.edu.shoeStore.auth.service;

import com.fpl.edu.shoeStore.auth.dto.request.RegisterRequestDto;
import com.fpl.edu.shoeStore.auth.dto.response.UserAuthResponseDto;
import com.fpl.edu.shoeStore.user.entity.User;

public interface UserAuthService {

    UserAuthResponseDto findUserByUserName(String userName);

    boolean checkLoginByUserNameAndPassword(String userName, String password);

    Integer getRoleIdByUserName(String userName);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    User registerUser(RegisterRequestDto registerRequest);
}
