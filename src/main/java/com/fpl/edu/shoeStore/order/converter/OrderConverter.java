package com.fpl.edu.shoeStore.order.converter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.fpl.edu.shoeStore.order.dto.request.OrderCreateRequest;
import com.fpl.edu.shoeStore.order.dto.request.OrderItemRequest;
import com.fpl.edu.shoeStore.order.dto.response.OrderItemResponse;
import com.fpl.edu.shoeStore.order.dto.response.OrderResponse;
import com.fpl.edu.shoeStore.order.entity.Order;
import com.fpl.edu.shoeStore.order.entity.OrderItem;

@Component
public class OrderConverter {

    // --- Entity to Response DTO ---

    public OrderResponse toResponse(Order order, List<OrderItem> items) {
        OrderResponse response = new OrderResponse();
        response.setOrderId(order.getOrderId());
        response.setBuyerId(order.getBuyerId());
        response.setVoucherId(order.getVoucherId());
        response.setOrderDate(order.getOrderDate());
        response.setStatus(order.getStatus());
        response.setTotalAmount(order.getTotalAmount());
        response.setDiscountAmount(order.getDiscountAmount());
        response.setFinalAmount(order.getFinalAmount());
        response.setShippingFee(order.getShippingFee());

        response.setShippingFullname(order.getShippingFullname());
        response.setShippingPhone(order.getShippingPhone());
        response.setShippingAddress(order.getShippingAddress());
        response.setShippingCity(order.getShippingCity());
        response.setShippingCountry(order.getShippingCountry());
        response.setNote(order.getNote());

        if (items != null) {
            response.setItems(items.stream().map(this::toItemResponse).collect(Collectors.toList()));
        }
        return response;
    }

    public OrderItemResponse toItemResponse(OrderItem item) {
        OrderItemResponse response = new OrderItemResponse();
        response.setOrderItemId(item.getOrderItemId());
        response.setVariantId(item.getVariantId());
        response.setProductNameSnapshot(item.getProductNameSnapshot());
        response.setQuantity(item.getQuantity());
        response.setUnitPrice(item.getUnitPrice());
        response.setTotalPrice(item.getTotalPrice());
        return response;
    }

    // --- Request DTO to Entity ---

    public Order toEntity(OrderCreateRequest request) {
        Order order = new Order();
        // ID orderId, orderDate, status, totalAmount, finalAmount, discountAmount
        // và các trường createAt/updateAt sẽ được thiết lập trong Service

        order.setBuyerId(request.getBuyerId());
        order.setVoucherId(request.getVoucherId());
        order.setShippingFee(request.getShippingFee());

        order.setShippingFullname(request.getShippingFullname());
        order.setShippingPhone(request.getShippingPhone());
        order.setShippingAddress(request.getShippingAddress());
        order.setShippingCity(request.getShippingCity());
        order.setShippingCountry(request.getShippingCountry());
        order.setNote(request.getNote());

        // Thiết lập giá trị mặc định ban đầu
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PENDING"); // Hoặc một trạng thái mặc định khác
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        // Các trường tiền tệ (Total/Discount/Final) sẽ được tính toán trong Service

        return order;
    }

    public OrderItem toItemEntity(OrderItemRequest request) {
        OrderItem item = new OrderItem();
        item.setVariantId(request.getVariantId());
        item.setQuantity(request.getQuantity());
        // orderId, unitPrice, totalPrice, productNameSnapshot được thiết lập trong Service
        return item;
    }
}