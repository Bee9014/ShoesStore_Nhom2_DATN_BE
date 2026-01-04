package com.fpl.edu.shoeStore.order.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.fpl.edu.shoeStore.order.entity.Order;
import com.fpl.edu.shoeStore.order.entity.OrderItem;

@Mapper
public interface OrderMapper {

    /**
     * Lưu thông tin chung của đơn hàng (Người mua, tổng tiền, địa chỉ).
     */
    void insertOrder(Order order);

    /**
     * Lưu từng món hàng trong đơn hàng (Sản phẩm, số lượng, giá tại thời điểm mua).
     */
    void insertOrderItem(OrderItem item);

    /**
     * Tìm thông tin đơn hàng theo mã ID.
     */
    Order findById(@Param("orderId") int orderId);

    /**
     * Lấy danh sách sản phẩm chi tiết của một đơn hàng.
     */
    List<OrderItem> findItemsByOrderId(@Param("orderId") int orderId);

    /**
     * Cập nhật trạng thái đơn hàng (VD: Chờ duyệt -> Đang giao -> Thành công).
     */
    int updateStatus(@Param("orderId") int orderId, @Param("status") String status);

    /**
     * Xem lịch sử đơn hàng của một người dùng cụ thể.
     */
    List<Order> findByBuyerId(@Param("userId") int userId);

    /**
     * Tìm kiếm và phân trang đơn hàng dành cho Admin quản lý đơn.
     */
    List<Order> findAllPaged(
        @Param("status") String status,
        @Param("searchTerm") String searchTerm,
        @Param("offset") int offset,
        @Param("size") int size
    );

    /**
     * Đếm tổng số đơn hàng theo bộ lọc để tính trang.
     */
    long countAll(@Param("status") String status, @Param("searchTerm") String searchTerm);
}