package com.fpl.edu.shoeStore.auth.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.fpl.edu.shoeStore.auth.entity.UserAuth;
import com.fpl.edu.shoeStore.user.entity.User;

@Mapper
public interface UserAuthMapper {
    /** * Tìm kiếm thông tin đăng nhập của người dùng qua Username.
     * Thường dùng trong quá trình xử lý Login của Spring Security.
     */
    UserAuth findUserByUsername(@Param("username") String username);
    
    /** * Lấy mã quyền (Role ID) từ Username để phân quyền (Admin/User).
     */
    Integer getRoleIdByUsername(@Param("username") String username);
    
    /** * Kiểm tra Username đã tồn tại chưa (trả về true nếu đã có).
     */
    boolean existsByUsername(@Param("username") String username);
    
    /** * Kiểm tra Email đã tồn tại chưa để tránh đăng ký trùng.
     */
    boolean existsByEmail(@Param("email") String email);
    
    /** * Kiểm tra số điện thoại đã tồn tại chưa.
     */
    boolean existsByPhone(@Param("phone") String phone);
    
    /** * Đăng ký/Thêm mới người dùng vào hệ thống.
     */
    int insertUser(User user);
}