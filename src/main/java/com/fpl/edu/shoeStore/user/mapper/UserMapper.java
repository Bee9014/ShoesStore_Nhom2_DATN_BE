package com.fpl.edu.shoeStore.user.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.fpl.edu.shoeStore.user.entity.User;

@Mapper
public interface UserMapper {

    // ==================== CƠ BẢN (CRUD) ====================

    /**
     * Lấy danh sách tất cả người dùng trong hệ thống (không phân trang).
     */
    List<User> findAll();

    /**
     * Tìm kiếm thông tin chi tiết của một người dùng dựa trên mã ID nội bộ.
     */
    User findById(Integer id);

    /**
     * Tìm kiếm người dùng dựa trên số điện thoại chính xác.
     */
    User findByPhone(String phone);

    /**
     * Tìm kiếm người dùng dựa trên tên đăng nhập (username) chính xác.
     */
    User findByUsername(String username);

    /**
     * Thêm mới một tài khoản người dùng vào hệ thống.
     * Trả về số dòng bị tác động (thường là 1 nếu thành công).
     */
    Integer insert(User user);

    /**
     * Cập nhật thông tin cá nhân hoặc trạng thái của người dùng hiện tại.
     */
    Integer update(User user);

    /**
     * Xóa vĩnh viễn bản ghi người dùng khỏi cơ sở dữ liệu dựa trên mã ID.
     */
    Integer deleteById(Integer id);

    // ==================== PHÂN TRANG & BỘ LỌC (ADMIN) ====================

    /**
     * Tìm kiếm, lọc và phân trang danh sách người dùng cho giao diện quản trị Admin.
     * Hỗ trợ lọc đa năng theo ID, tên, email, số điện thoại, vai trò và trạng thái.
     * Lưu ý: Trong XML phải dùng OFFSET...FETCH NEXT đặc thù của SQL Server.
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

    /**
     * Đếm tổng số lượng người dùng thỏa mãn bộ lọc để tính toán số trang trong phân trang.
     */
    long countAll(
            @Param("userId") Integer userId,
            @Param("username") String username,
            @Param("fullName") String fullName,
            @Param("email") String email,
            @Param("phone") String phone,
            @Param("roleId") Integer roleId,
            @Param("status") String status
    );

    // ==================== KIỂM TRA TRÙNG LẶP (VALIDATION) ====================

    /**
     * Đếm số lượng tài khoản sử dụng Username này (trừ các tài khoản đã bị xóa).
     */
    @Select("SELECT COUNT(user_id) FROM sys_user WHERE username = #{username} AND [status] != 'deleted'")
    int countByUsername(@Param("username") String username);

    /**
     * Đếm số lượng tài khoản sử dụng Email này.
     */
    @Select("SELECT COUNT(user_id) FROM sys_user WHERE email = #{email} AND [status] != 'deleted'")
    int countByEmail(@Param("email") String email);

    /**
     * Đếm số lượng tài khoản sử dụng số điện thoại này.
     */
    @Select("SELECT COUNT(user_id) FROM sys_user WHERE phone = #{phone} AND [status] != 'deleted'")
    int countByPhone(@Param("phone") String phone);

    /**
     * Kiểm tra trùng lặp Username khi cập nhật thông tin (bỏ qua bản ghi hiện tại qua ID).
     */
    @Select("SELECT COUNT(user_id) FROM sys_user WHERE username = #{username} AND user_id != #{id} AND [status] != 'deleted'")
    int countByUsernameExcludingId(@Param("username") String username, @Param("id") Integer id);

    /**
     * Kiểm tra trùng lặp Email khi cập nhật thông tin (bỏ qua bản ghi hiện tại qua ID).
     */
    @Select("SELECT COUNT(user_id) FROM sys_user WHERE email = #{email} AND user_id != #{id} AND [status] != 'deleted'")
    int countByEmailExcludingId(@Param("email") String email, @Param("id") Integer id);

    /**
     * Kiểm tra trùng lặp số điện thoại khi cập nhật thông tin (bỏ qua bản ghi hiện tại qua ID).
     */
    @Select("SELECT COUNT(user_id) FROM sys_user WHERE phone = #{phone} AND user_id != #{id} AND [status] != 'deleted'")
    int countByPhoneExcludingId(@Param("phone") String phone, @Param("id") Integer id);
    
    // ==================== THỐNG KÊ (DASHBOARD) ====================

    /**
     * Thống kê: Tổng số lượng người dùng hiện có trong hệ thống (không tính những người đã xóa).
     */
    @Select("SELECT COUNT(*) FROM sys_user WHERE [status] != 'deleted'")
    Long countAllUsers();
    
    /**
     * Thống kê: Số lượng khách hàng mới đăng ký trong một tháng và năm cụ thể.
     * Phục vụ vẽ biểu đồ tăng trưởng người dùng trên trang Dashboard.
     */
    Long countNewUsersInMonth(@Param("year") Integer year, @Param("month") Integer month);
}