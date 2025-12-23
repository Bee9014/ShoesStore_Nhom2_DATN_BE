package com.fpl.edu.shoeStore.order.service;

import com.fpl.edu.shoeStore.common.handler.PageResponse;
import com.fpl.edu.shoeStore.order.dto.request.OrderCreateRequest;
import com.fpl.edu.shoeStore.order.dto.response.OrderResponse;
import com.fpl.edu.shoeStore.order.exception.OrderException;

public interface OrderService {
    /**
     * Tạo một đơn hàng mới. Bao gồm tính toán giá, áp dụng voucher và lưu vào CSDL.
     * @param request DTO chứa thông tin đơn hàng cần tạo.
     * @return OrderResponse DTO của đơn hàng đã tạo.
     * @throws OrderException nếu có lỗi trong quá trình tạo (ví dụ: hết hàng, voucher không hợp lệ).
     */
    OrderResponse createOrder(OrderCreateRequest request) throws OrderException;

    /**
     * Lấy thông tin chi tiết một đơn hàng.
     */
    OrderResponse getOrderDetails(int orderId) throws OrderException;

    /**
     * Cập nhật trạng thái đơn hàng (ví dụ: từ PENDING sang SHIPPED).
     */
    void updateOrderStatus(int orderId, String newStatus) throws OrderException;

    /**
     * Lấy danh sách đơn hàng của user (buyerId)
     */
    PageResponse<OrderResponse> getMyOrders(int userId, int page, int size);

    /**
     * Lấy tất cả đơn hàng (Admin) với filter và search
     */
    PageResponse<OrderResponse> getAllOrders(String status, String searchTerm, int page, int size);

    /**
     * Hủy đơn hàng (chỉ khi status = PENDING)
     */
    void cancelOrder(int orderId, int userId) throws OrderException;

    /**
     * ADMIN: Lấy tất cả đơn hàng với filter
     */
    PageResponse<OrderResponse> getAllOrdersForAdmin(String status, String searchTerm, int page, int size);

    /**
     * ADMIN: Đếm tổng số đơn hàng
     */
    long countAllOrders();

    /**
     * ADMIN: Đếm số đơn hàng theo status
     */
    long countOrdersByStatus(String status);
}