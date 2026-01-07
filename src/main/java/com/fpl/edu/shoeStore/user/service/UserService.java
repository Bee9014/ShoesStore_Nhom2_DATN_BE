package com.fpl.edu.shoeStore.user.service;

import com.fpl.edu.shoeStore.common.handler.PageResponse;
import com.fpl.edu.shoeStore.user.dto.request.UserDtoRequest;
import com.fpl.edu.shoeStore.user.dto.response.UserDtoResponse;

import java.time.LocalDate;
import java.util.List;

public interface UserService {

    /**
     * Tìm kiếm và phân trang User
     * Các tham số isActive, isDeleted cũ đã được gộp vào status
     */
    PageResponse<UserDtoResponse> findAllPaged(
            Integer userId,
            String username,
            String fullName,      // Đổi từ hoVaTen
            Integer gender,
            LocalDate birthday,
            String email,
            String phone,         // Đổi từ phoneNumber
            Integer roleId,
            String status,        // Đổi từ Boolean sang String (active/blocked/deleted)
            int page,
            int size
    );

    UserDtoResponse createUser(UserDtoRequest request);

    UserDtoResponse updateUser(Integer id, UserDtoRequest request);

    int deleteUser(Integer id);

    UserDtoResponse findById(Integer id);

    // Sửa kiểu tham số từ Integer -> String vì số điện thoại là chuỗi
    List<UserDtoResponse> findByPhone(String phone);

    List<UserDtoResponse> findByUsername(String username);
}
