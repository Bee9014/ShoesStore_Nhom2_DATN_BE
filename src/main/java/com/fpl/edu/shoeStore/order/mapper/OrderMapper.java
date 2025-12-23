package com.fpl.edu.shoeStore.order.mapper;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.fpl.edu.shoeStore.order.entity.Order;
import com.fpl.edu.shoeStore.order.entity.OrderItem;

@Mapper
public interface OrderMapper {

    // Thêm mới Order (sử dụng <insert> và keyProperty/useGeneratedKeys trong XML)
    void insertOrder(Order order);

    // Thêm mới OrderItem
    void insertOrderItem(OrderItem item);

    // Lấy Order theo ID
    Order findById(int orderId);

    // Lấy tất cả OrderItem theo Order ID
    List<OrderItem> findItemsByOrderId(int orderId);

    // Cập nhật trạng thái
    int updateStatus(@Param("orderId") int orderId, @Param("status") String status);

    // Lấy danh sách Order theo Buyer ID
    List<Order> findByBuyerId(int buyerId);

    // Lấy danh sách Order với filter (Admin)
    List<Order> findAllPaged(
        @Param("status") String status,
        @Param("searchTerm") String searchTerm,
        @Param("offset") int offset,
        @Param("size") int size
    );

    // Đếm tổng số orders với filter
    long countAll(
        @Param("status") String status,
        @Param("searchTerm") String searchTerm
    );

    // Đếm số order theo status
    long countByStatus(@Param("status") String status);
}
