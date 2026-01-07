package com.fpl.edu.shoeStore.user.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {


    private int userId;
    private int roleId;
    private String username;
    private String passwordHash;
    private String fullName;
    private Integer gender;
    private LocalDate birthday;
    private String email;

    private String phone;

   
    private String status; 
  
    private LocalDateTime createdAt;


    private LocalDateTime updatedAt;


    
}
