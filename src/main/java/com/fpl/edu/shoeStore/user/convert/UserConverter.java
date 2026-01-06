package com.fpl.edu.shoeStore.user.convert;

import java.util.List;
import java.util.stream.Collectors;

import com.fpl.edu.shoeStore.user.dto.request.UserDtoRequest;
import com.fpl.edu.shoeStore.user.dto.response.UserDtoResponse;
import com.fpl.edu.shoeStore.user.entity.User;

public class UserConverter {

    /**
     * RequestDto -> Entity
     * Dùng khi Tạo mới (Create) hoặc Cập nhật (Update)
     */
    public static User toEntity(UserDtoRequest dto) {
        if (dto == null) return null;

        User user = new User();
        
        // Mapping các trường cơ bản
        user.setUsername(dto.getUsername());
        
       
        user.setPasswordHash(dto.getPasswordHash());

        user.setFullName(dto.getFullName()); 

        user.setGender(dto.getGender());

        user.setBirthday(dto.getBirthday());

        user.setEmail(dto.getEmail());
        
    
        user.setPhone(dto.getPhone());

        user.setStatus(dto.getStatus());
        
        user.setRoleId(dto.getRoleId());

        return user;
    }

    /**
     * Entity -> ResponseDto
     * Dùng để trả dữ liệu ra API
     */
    public static UserDtoResponse toDto(User user) {
        if (user == null) return null;

        return UserDtoResponse.builder()
                .userId(user.getUserId())
                .roleId(user.getRoleId())
                .username(user.getUsername())
                
                // Mapping các trường tên mới
                .fullName(user.getFullName())
                .gender(user.getGender())
                .birthday(user.getBirthday())
                .phone(user.getPhone())
                
                .email(user.getEmail())
                .status(user.getStatus())
                
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                
                // LƯU Ý: Không trả về passwordHash
                .build();
    }

    /**
     * List<Entity> -> List<ResponseDto>
     */
    public static List<UserDtoResponse> toDtoList(List<User> users) {
        if (users == null || users.isEmpty()) return List.of();
        return users.stream()
                .map(UserConverter::toDto)
                .collect(Collectors.toList());
    }
}
