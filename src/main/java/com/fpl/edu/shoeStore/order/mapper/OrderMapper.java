package com.fpl.edu.shoeStore.order.mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.fpl.edu.shoeStore.order.entity.Order;
import com.fpl.edu.shoeStore.order.entity.OrderItem;

@Mapper
public interface OrderMapper {

    // ==================== ORDER PROCESSING (XỬ LÝ ĐƠN HÀNG) ====================

    /**
     * Tạo một đơn hàng mới (Master record).
     * MyBatis sẽ tự động lấy ID tự tăng từ SQL Server và gán ngược lại vào object 'order'.
     */
    void insertOrder(Order order);

    /**
     * Lưu chi tiết từng sản phẩm trong đơn hàng (Detail records).
     * Mỗi OrderItem sẽ liên kết với OrderId vừa được tạo.
     */
    void insertOrderItem(OrderItem item);

    /**
     * Tìm kiếm thông tin đơn hàng theo mã ID duy nhất.
     */
    Order findById(int orderId);

    /**
     * Lấy danh sách tất cả sản phẩm thuộc về một đơn hàng cụ thể.
     */
    List<OrderItem> findItemsByOrderId(int orderId);

    /**
     * Cập nhật trạng thái của đơn hàng (ví dụ: PENDING -> DELIVERED).
     * Trả về số dòng bị tác động trong SQL Server.
     */
    int updateStatus(@Param("orderId") int orderId, @Param("status") String status);

    // ==================== QUERIES & PAGINATION (TRUY VẤN & PHÂN TRANG) ====================

    /**
     * Lấy danh sách lịch sử mua hàng của một khách hàng (có phân trang).
     * Phù hợp cho trang "Đơn hàng của tôi" trên Website.
     */
    List<Order> findByBuyerId(@Param("userId") int userId, 
                              @Param("status") String status, 
                              @Param("offset") int offset, 
                              @Param("limit") int limit);

    /**
     * Tìm kiếm và lọc danh sách đơn hàng cho trang quản trị Admin.
     * Cho phép lọc theo trạng thái và tìm kiếm tên khách hàng hoặc mã đơn.
     */
    List<Order> findAllPaged(
        @Param("status") String status,
        @Param("searchTerm") String searchTerm,
        @Param("offset") int offset,
        @Param("size") int size
    );

    /**
     * Đếm tổng số đơn hàng của một người dùng để tính toán phân trang.
     */
    long countByBuyerId(@Param("userId") int userId, @Param("status") String status);

    /**
     * Đếm tổng số đơn hàng thỏa mãn bộ lọc (Admin) để phục vụ phân trang.
     */
    long countAll(
        @Param("status") String status,
        @Param("searchTerm") String searchTerm
    );

    /**
     * Đếm số lượng đơn hàng theo một trạng thái cụ thể (ví dụ: có bao nhiêu đơn đang chờ duyệt).
     */
    long countByStatus(@Param("status") String status);
    
    // ==================== DASHBOARD STATISTICS (THỐNG KÊ DOANH THU) ====================
    
    /**
     * Thống kê: Tổng số lượng đơn hàng đã phát sinh trên toàn hệ thống.
     */
    Long countAllOrders();
    
    /**
     * Thống kê: Tính tổng doanh thu thực tế (chỉ tính trên các đơn hàng đã giao thành công - DELIVERED).
     */
    BigDecimal calculateTotalRevenue();
    
    /**
     * Thống kê: Tính doanh thu của một tháng cụ thể trong năm.
     * Dùng để vẽ biểu đồ doanh thu theo thời gian.
     */
    BigDecimal calculateRevenueByMonth(@Param("year") Integer year, @Param("month") Integer month);
    
    /**
     * Thống kê: Tìm danh sách các sản phẩm bán chạy nhất.
     * Trả về List các Map chứa thông tin sản phẩm và số lượng đã bán.
     */
    List<Map<String, Object>> findTopSellingProducts(@Param("limit") Integer limit);
}