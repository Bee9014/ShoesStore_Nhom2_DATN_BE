package com.fpl.edu.shoeStore.user.dto.request;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDtoRequest {
    private int userId;
    private int roleId;
    private String username;
    private String passwordHash;
    private String fullName;
    private String email;
    private Integer gender;
    private LocalDate birthday;
    private String phone; 
    private String status; 
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
}
