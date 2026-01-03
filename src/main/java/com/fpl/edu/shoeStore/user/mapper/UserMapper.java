package com.fpl.edu.shoeStore.user.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.fpl.edu.shoeStore.user.entity.User;

@Mapper
public interface UserMapper {

    List<User> findAll();

    User findById(Integer id);

    User findByPhone(String phone);

    User findByUsername(String username);

    // Trả về số dòng ảnh hưởng
    Integer insert(User user);

    Integer update(User user);

    Integer deleteById(Integer id);

    /**
     * Phân trang SQL Server yêu cầu ORDER BY.
     * Logic này sẽ nằm trong file XML tương ứng với OFFSET/FETCH.
     */
    List<User> findAllPaged(
            @Param("userId") Integer userId,
            @Param("username") String username,
            @Param("fullName") String fullName,
            @Param("email") String email,
            @Param("phone") String phone,
            @Param("roleId") Integer roleId,
            @Param("status") String status,
            @Param("offset") int offset,
            @Param("size") int size
    );

    long countAll(
            @Param("userId") Integer userId,
            @Param("username") String username,
            @Param("fullName") String fullName,
            @Param("email") String email,
            @Param("phone") String phone,
            @Param("roleId") Integer roleId,
            @Param("status") String status
    );

    // SQL Server bọc [status] nếu cần thiết trong XML, ở đây là chuỗi nên giữ nguyên
    @Select("SELECT COUNT(user_id) FROM sys_user WHERE username = #{username} AND [status] != 'deleted'")
    int countByUsername(@Param("username") String username);

    @Select("SELECT COUNT(user_id) FROM sys_user WHERE email = #{email} AND [status] != 'deleted'")
    int countByEmail(@Param("email") String email);

    @Select("SELECT COUNT(user_id) FROM sys_user WHERE phone = #{phone} AND [status] != 'deleted'")
    int countByPhone(@Param("phone") String phone);

    @Select("SELECT COUNT(user_id) FROM sys_user WHERE username = #{username} AND user_id != #{id} AND [status] != 'deleted'")
    int countByUsernameExcludingId(@Param("username") String username, @Param("id") Integer id);

    @Select("SELECT COUNT(user_id) FROM sys_user WHERE email = #{email} AND user_id != #{id} AND [status] != 'deleted'")
    int countByEmailExcludingId(@Param("email") String email, @Param("id") Integer id);

    @Select("SELECT COUNT(user_id) FROM sys_user WHERE phone = #{phone} AND user_id != #{id} AND [status] != 'deleted'")
    int countByPhoneExcludingId(@Param("phone") String phone, @Param("id") Integer id);
    
    @Select("SELECT COUNT(*) FROM sys_user WHERE [status] != 'deleted'")
    Long countAllUsers();
    
    /**
     * Dashboard: SQL Server sử dụng YEAR() và MONTH() giống MySQL.
     */
    Long countNewUsersInMonth(@Param("year") Integer year, @Param("month") Integer month);
}