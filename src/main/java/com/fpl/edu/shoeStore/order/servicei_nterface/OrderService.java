package com.fpl.edu.shoeStore.order.servicei_nterface;

import com.fpl.edu.shoeStore.order.dto.OrderCreateRequest;
import com.fpl.edu.shoeStore.order.dto.OrderResponse;
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

    // Thêm các phương thức khác như getListOrders, cancelOrder...
}