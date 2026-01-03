package com.fpl.edu.shoeStore.auth.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.fpl.edu.shoeStore.auth.entity.UserAuth;
import com.fpl.edu.shoeStore.user.entity.User;

@Mapper
public interface UserAuthMapper {
    
    // Tìm người dùng để phục vụ Login (Spring Security)
    UserAuth findUserByUsername(@Param("username") String username);
    // Lấy Role ID của người dùng
    Integer getRoleIdByUsername(@Param("username") String username);
    
    boolean existsByUsername(@Param("username") String username);
    
    boolean existsByEmail(@Param("email") String email);
    
    boolean existsByPhone(@Param("phone") String phone);
    
    int insertUser(User user);
}