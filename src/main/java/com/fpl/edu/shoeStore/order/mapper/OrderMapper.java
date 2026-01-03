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

    // Thêm mới Order (MyBatis sẽ lấy Identity từ SQL Server gán vào object)
    void insertOrder(Order order);

    // Thêm mới OrderItem
    void insertOrderItem(OrderItem item);

    // Lấy Order theo ID
    Order findById(int orderId);

    // Lấy tất cả OrderItem theo Order ID
    List<OrderItem> findItemsByOrderId(int orderId);

    // Cập nhật trạng thái (SQL Server trả về số dòng bị tác động)
    int updateStatus(@Param("orderId") int orderId, @Param("status") String status);

    /**
     * Lấy danh sách Order theo Buyer ID
     * Lưu ý XML: Sử dụng OFFSET #{offset} ROWS FETCH NEXT #{limit} ROWS ONLY
     */
    List<Order> findByBuyerId(@Param("userId") int userId, 
                              @Param("status") String status, 
                              @Param("offset") int offset, 
                              @Param("limit") int limit);

    /**
     * Lấy danh sách Order với filter (Admin)
     * SQL Server yêu cầu ORDER BY khi dùng phân trang
     */
    List<Order> findAllPaged(
        @Param("status") String status,
        @Param("searchTerm") String searchTerm,
        @Param("offset") int offset,
        @Param("size") int size
    );

    // Đếm trả về kiểu long (BIGINT trong SQL Server)
    long countByBuyerId(@Param("userId") int userId, @Param("status") String status);

    long countAll(
        @Param("status") String status,
        @Param("searchTerm") String searchTerm
    );

    long countByStatus(@Param("status") String status);
    
    // ==================== DASHBOARD STATISTICS ====================
    
    /**
     * Đếm tổng số đơn hàng
     * Dùng Long để tránh overflow nếu dữ liệu lớn
     */
    Long countAllOrders();
    
    /**
     * Tính tổng doanh thu (Chỉ đơn DELIVERED)
     * BigDecimal tương ứng với kiểu DECIMAL/MONEY trong SQL Server
     */
    BigDecimal calculateTotalRevenue();
    
    /**
     * Tính doanh thu theo tháng
     * XML lưu ý dùng: WHERE YEAR(order_date) = #{year} AND MONTH(order_date) = #{month}
     */
    BigDecimal calculateRevenueByMonth(@Param("year") Integer year, @Param("month") Integer month);
    
    /**
     * Lấy top sản phẩm bán chạy
     * SQL Server dùng SELECT TOP #{limit} ...
     */
    List<Map<String, Object>> findTopSellingProducts(@Param("limit") Integer limit);
}