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
    User findByPhone(@Param("phone") String phone);

    /**
     * Tìm kiếm người dùng qua tên đăng nhập. 
     * Thường dùng để lấy thông tin chi tiết sau khi người dùng đã xác thực thành công.
     */
    User findByUsername(@Param("username") String username);

    /**
     * Thêm mới người dùng. MyBatis sẽ tự động xử lý mapping các thuộc tính như fullName, email, birthday...
     * Trả về số dòng thành công (1).
     */
    Integer insert(User user);

    /**
     * Cập nhật thông tin người dùng. Lưu ý: Chỉ cập nhật các trường được truyền giá trị vào đối tượng User.
     */
    Integer update(User user);

    /**
     * Xóa vĩnh viễn tài khoản khỏi cơ sở dữ liệu. 
     * (Khuyên dùng: Nên sử dụng cập nhật status sang 'deleted' thay vì xóa vật lý).
     */
    Integer deleteById(@Param("id") Integer id);

    // ==================== PHÂN TRANG & BỘ LỌC (ADMIN) ====================

    /**
     * Tìm kiếm nâng cao và phân trang cho Admin. 
     * Cho phép lọc theo tất cả các tiêu chí như Role, Status, Họ tên, Email...
     * @param offset Vị trí bắt đầu (Số trang * Kích thước trang)
     * @param size Số lượng bản ghi cần lấy
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
     * Đếm tổng số lượng bản ghi thỏa mãn bộ lọc phía trên để tính toán tổng số trang hiển thị.
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
     * Kiểm tra Username đã tồn tại hay chưa. 
     * Trả về số lượng (thường là 0 hoặc 1).
     */
    @Select("SELECT COUNT(user_id) FROM sys_user WHERE username = #{username} AND [status] != 'deleted'")
    int countByUsername(@Param("username") String username);

    /**
     * Kiểm tra Email đã tồn tại hay chưa để tránh đăng ký trùng.
     */
    @Select("SELECT COUNT(user_id) FROM sys_user WHERE email = #{email} AND [status] != 'deleted'")
    int countByEmail(@Param("email") String email);

    /**
     * Kiểm tra số điện thoại đã được đăng ký cho tài khoản nào khác chưa.
     */
    @Select("SELECT COUNT(user_id) FROM sys_user WHERE phone = #{phone} AND [status] != 'deleted'")
    int countByPhone(@Param("phone") String phone);

    /**
     * Kiểm tra trùng lặp Username khi UPDATE. 
     * Phải loại trừ ID của chính người dùng đang sửa để không tự trùng với chính mình.
     */
    @Select("SELECT COUNT(user_id) FROM sys_user WHERE username = #{username} AND user_id != #{id} AND [status] != 'deleted'")
    int countByUsernameExcludingId(@Param("username") String username, @Param("id") Integer id);

    /**
     * Kiểm tra trùng lặp Email khi UPDATE (loại trừ chính mình).
     */
    @Select("SELECT COUNT(user_id) FROM sys_user WHERE email = #{email} AND user_id != #{id} AND [status] != 'deleted'")
    int countByEmailExcludingId(@Param("email") String email, @Param("id") Integer id);

    /**
     * Kiểm tra trùng lặp số điện thoại khi UPDATE (loại trừ chính mình).
     */
    @Select("SELECT COUNT(user_id) FROM sys_user WHERE phone = #{phone} AND user_id != #{id} AND [status] != 'deleted'")
    int countByPhoneExcludingId(@Param("phone") String phone, @Param("id") Integer id);
    
    // ==================== THỐNG KÊ (DASHBOARD) ====================

    /**
     * Đếm tổng số người dùng thực tế đang hoạt động/tồn tại (loại trừ đã xóa).
     */
    @Select("SELECT COUNT(*) FROM sys_user WHERE [status] != 'deleted'")
    Long countAllUsers();
    
    /**
     * Đếm số lượng người dùng mới trong tháng của năm cụ thể. 
     * Thường dùng cho biểu đồ tăng trưởng Line Chart trên Dashboard.
     */
    Long countNewUsersInMonth(@Param("year") Integer year, @Param("month") Integer month);
}