package com.fpl.edu.shoeStore.order.mapper;
import com.fpl.edu.shoeStore.order.model.Order;
import com.fpl.edu.shoeStore.order.model.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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
}
