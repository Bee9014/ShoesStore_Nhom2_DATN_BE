package com.fpl.edu.shoeStore.auth.mapper;

import com.fpl.edu.shoeStore.auth.entity.UserAuth;
import com.fpl.edu.shoeStore.user.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserAuthMapper {
    UserAuth findUserByUsername(@Param("username") String username);

    Integer getRoleIdByUsername(@Param("username") String username);

    Integer existsByUsername(@Param("username") String username);

    Integer existsByEmail(@Param("email") String email);

    Integer existsByPhone(@Param("phone") String phone);

    int insertUser(User user);
}