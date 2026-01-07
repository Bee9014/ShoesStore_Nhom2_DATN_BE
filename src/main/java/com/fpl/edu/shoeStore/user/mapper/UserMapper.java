package com.fpl.edu.shoeStore.user.mapper;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.fpl.edu.shoeStore.user.entity.User;

@Mapper
public interface UserMapper {

    // ==================== CƠ BẢN (CRUD) ====================

    /**
     * Lấy danh sách toàn bộ người dùng. Thường dùng cho các báo cáo nội bộ không yêu cầu phân trang.
     */
    List<User> findAll();

    /**
     * Truy vấn chi tiết một người dùng qua ID. Dùng khi xem hồ sơ cá nhân hoặc chỉnh sửa.
     */
    User findById(@Param("id") Integer id);

    /**
     * Tìm kiếm người dùng bằng số điện thoại. Rất hữu ích khi tra cứu khách hàng tại quầy.
     */
    List<User> findByPhone(@Param("phone") String phone);

    /**
     * Tìm kiếm người dùng qua tên đăng nhập. 
     */
    List<User> findByUsername(@Param("username") String username);

    /**
     * Thêm mới người dùng. Trả về số dòng thành công (1).
     * ID tự tăng sẽ được MyBatis gán ngược vào object User.
     */
    Integer insert(User user);

    /**
     * Cập nhật thông tin người dùng dựa trên userId.
     */
    Integer update(User user);

    /**
     * Xóa vĩnh viễn tài khoản khỏi cơ sở dữ liệu theo ID.
     */
    Integer deleteById(@Param("id") Integer id);

    // ==================== PHÂN TRANG & BỘ LỌC (ADMIN) ====================

    /**
     * Tìm kiếm nâng cao và phân trang cho Admin. 
     * Hỗ trợ lọc theo Role, Status, Họ tên, Email, Phone...
     */
    List<User> findAllPaged(
            @Param("userId") Integer userId,
            @Param("username") String username,
            @Param("fullName") String fullName,
            @Param("gender") Integer gender,
            @Param("birthday") LocalDate birthday,
            @Param("email") String email,
            @Param("phone") String phone,
            @Param("roleId") Integer roleId,
            @Param("status") String status,
            @Param("offset") int offset,
            @Param("size") int size
    );

    /**
     * Đếm tổng số lượng bản ghi thỏa mãn bộ lọc để tính toán phân trang.
     */
    long countAll(
            @Param("userId") Integer userId,
            @Param("username") String username,
            @Param("fullName") String fullName,
            @Param("gender") Integer gender,
            @Param("birthday") LocalDate birthday,
            @Param("email") String email,
            @Param("phone") String phone,
            @Param("roleId") Integer roleId,
            @Param("status") String status
    );

    // ==================== KIỂM TRA TRÙNG LẶP (BOOLEAN) ====================

    /**
     * Kiểm tra Username đã tồn tại hay chưa. 
     * Trả về true nếu đã tồn tại, false nếu chưa.
     */
    @Select("SELECT COUNT(user_id) FROM sys_user WHERE username = #{username} AND [status] != 'deleted'")
    boolean countByUsername(@Param("username") String username);

    /**
     * Kiểm tra Email đã tồn tại hay chưa để tránh đăng ký trùng.
     */
    @Select("SELECT COUNT(user_id) FROM sys_user WHERE email = #{email} AND [status] != 'deleted'")
    boolean countByEmail(@Param("email") String email);

    /**
     * Kiểm tra số điện thoại đã được đăng ký chưa.
     */
    @Select("SELECT COUNT(user_id) FROM sys_user WHERE phone = #{phone} AND [status] != 'deleted'")
    boolean countByPhone(@Param("phone") String phone);

    /**
     * Kiểm tra trùng lặp Username khi UPDATE (loại trừ ID của chính mình).
     */
    @Select("SELECT COUNT(user_id) FROM sys_user WHERE username = #{username} AND user_id != #{id} AND [status] != 'deleted'")
    boolean countByUsernameExcludingId(@Param("username") String username, @Param("id") Integer id);

    /**
     * Kiểm tra trùng lặp Email khi UPDATE (loại trừ ID của chính mình).
     */
    @Select("SELECT COUNT(user_id) FROM sys_user WHERE email = #{email} AND user_id != #{id} AND [status] != 'deleted'")
    boolean countByEmailExcludingId(@Param("email") String email, @Param("id") Integer id);

    /**
     * Kiểm tra trùng lặp số điện thoại khi UPDATE (loại trừ ID của chính mình).
     */
    @Select("SELECT COUNT(user_id) FROM sys_user WHERE phone = #{phone} AND user_id != #{id} AND [status] != 'deleted'")
    boolean countByPhoneExcludingId(@Param("phone") String phone, @Param("id") Integer id);
    
    // ==================== THỐNG KÊ (DASHBOARD) ====================

    /**
     * Đếm tổng số người dùng đang hoạt động (loại trừ đã xóa).
     */
    @Select("SELECT COUNT(*) FROM sys_user WHERE [status] != 'deleted'")
    Long countAllUsers();
    
    /**
     * Đếm số lượng người dùng mới trong tháng của năm cụ thể.
     */
    Long countNewUsersInMonth(@Param("year") Integer year, @Param("month") Integer month);
}