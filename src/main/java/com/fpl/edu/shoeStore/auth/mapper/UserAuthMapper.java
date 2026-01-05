package com.fpl.edu.shoeStore.auth.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.fpl.edu.shoeStore.auth.entity.UserAuth;
import com.fpl.edu.shoeStore.user.entity.User;

@Mapper
public interface UserAuthMapper {

    /**
     * Tìm kiếm người dùng kèm theo thông tin vai trò (Role) để phục vụ đăng nhập.
     * @param username Tên đăng nhập.
     * @return Đối tượng UserAuth chứa thông tin tài khoản và phân quyền.
     */
    UserAuth findUserByUsername(@Param("username") String username);

    /**
     * Lấy ID vai trò của người dùng dựa trên username (chỉ áp dụng cho tài khoản 'active').
     * @param username Tên đăng nhập.
     * @return ID của vai trò (Integer).
     */
    Integer getRoleIdByUsername(@Param("username") String username);

    /**
     * Kiểm tra sự tồn tại của tên đăng nhập trong hệ thống.
     * @param username Tên cần kiểm tra.
     * @return true nếu đã tồn tại, false nếu chưa.
     */
    Integer existsByUsername(@Param("username") String username);

    /**
     * Kiểm tra sự tồn tại của email trong hệ thống.
     * @param email Email cần kiểm tra.
     * @return true nếu đã tồn tại, false nếu chưa.
     */
    Integer existsByEmail(@Param("email") String email);

    /**
     * Kiểm tra sự tồn tại của số điện thoại trong hệ thống.
     * @param phone Số điện thoại cần kiểm tra.
     * @return true nếu đã tồn tại, false nếu chưa.
     */
    Integer existsByPhone(@Param("phone") String phone);

    /**
     * Thêm mới tài khoản người dùng (thường dùng cho chức năng Đăng ký).
     * @param user Đối tượng chứa thông tin người dùng mới.
     * @return Số dòng được thêm thành công (int).
     */
    int insertUser(User user);
}