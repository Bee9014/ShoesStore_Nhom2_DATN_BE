package com.fpl.edu.shoeStore.order.mapper;

import com.fpl.edu.shoeStore.order.entity.Order;
import com.fpl.edu.shoeStore.order.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface OrderMapper {
    int insertOrder(Order order);

    int insertOrderItem(OrderItem item);

    Order findById(@Param("orderId") Integer orderId);

    List<OrderItem> findItemsByOrderId(@Param("orderId") Integer orderId);

    int updateStatus(@Param("orderId") Integer orderId, @Param("status") String status);

    List<Order> findAllPaged(
            @Param("status") String status,
            @Param("searchTerm") String searchTerm,
            @Param("offset") int offset,
            @Param("size") int size
    );

    long countAll(@Param("status") String status, @Param("searchTerm") String searchTerm);
}