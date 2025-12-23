package com.fpl.edu.shoeStore.order.controller.api;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fpl.edu.shoeStore.common.handler.ApiResponse;
import com.fpl.edu.shoeStore.common.handler.PageResponse;
import com.fpl.edu.shoeStore.order.dto.request.OrderCreateRequest;
import com.fpl.edu.shoeStore.order.dto.response.OrderResponse;
import com.fpl.edu.shoeStore.order.exception.OrderException;
import com.fpl.edu.shoeStore.order.service.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService orderService;

    // ==================== USER ENDPOINTS ====================
    
    /**
     * USER: Tạo đơn hàng mới
     * POST /api/v1/orders
     */
    @PostMapping("/orders")
    public ApiResponse<OrderResponse> createOrder(@Valid @RequestBody OrderCreateRequest request) {
        try {
            OrderResponse orderResponse = orderService.createOrder(request);
            return ApiResponse.<OrderResponse>builder()
                    .success(true)
                    .statusCode(HttpStatus.CREATED.value())
                    .message("Đặt hàng thành công!")
                    .data(orderResponse)
                    .build();
        } catch (OrderException e) {
            return ApiResponse.<OrderResponse>builder()
                    .success(false)
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .message("Đặt hàng thất bại: " + e.getMessage())
                    .data(null)
                    .build();
        } catch (Exception e) {
            return ApiResponse.<OrderResponse>builder()
                    .success(false)
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Lỗi hệ thống: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    /**
     * USER: Lấy chi tiết đơn hàng
     * GET /api/v1/orders/{orderId}
     */
    @GetMapping("/orders/{orderId}")
    public ApiResponse<OrderResponse> getOrderDetail(@PathVariable int orderId) {
        try {
            OrderResponse orderResponse = orderService.getOrderDetails(orderId);
            return ApiResponse.<OrderResponse>builder()
                    .success(true)
                    .statusCode(HttpStatus.OK.value())
                    .message("Lấy thông tin đơn hàng thành công")
                    .data(orderResponse)
                    .build();
        } catch (OrderException e) {
            return ApiResponse.<OrderResponse>builder()
                    .success(false)
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .message(e.getMessage())
                    .data(null)
                    .build();
        } catch (Exception e) {
            return ApiResponse.<OrderResponse>builder()
                    .success(false)
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Lỗi hệ thống: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    /**
     * USER: Lấy lịch sử đơn hàng của user đang đăng nhập
     * GET /api/v1/orders/my-orders?page=1&size=10
     */
    @GetMapping("/orders/my-orders")
    public ApiResponse<PageResponse<OrderResponse>> getMyOrders(
            @RequestParam Integer userId, // REQUIRED - No fallback
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            PageResponse<OrderResponse> pageResponse = orderService.getMyOrders(userId, page, size);
            return ApiResponse.<PageResponse<OrderResponse>>builder()
                    .success(true)
                    .statusCode(HttpStatus.OK.value())
                    .message("Lấy danh sách đơn hàng thành công")
                    .data(pageResponse)
                    .build();
        } catch (Exception e) {
            return ApiResponse.<PageResponse<OrderResponse>>builder()
                    .success(false)
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Lỗi hệ thống: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    /**
     * USER: Hủy đơn hàng (chỉ khi status = PENDING)
     * PUT /api/v1/orders/{orderId}/cancel
     */
    @PutMapping("/orders/{orderId}/cancel")
    public ApiResponse<Void> cancelOrder(
            @PathVariable int orderId,
            @RequestParam Integer userId // REQUIRED - No fallback
    ) {
        try {
            orderService.cancelOrder(orderId, userId);
            return ApiResponse.<Void>builder()
                    .success(true)
                    .statusCode(HttpStatus.OK.value())
                    .message("Hủy đơn hàng thành công")
                    .data(null)
                    .build();
        } catch (OrderException e) {
            return ApiResponse.<Void>builder()
                    .success(false)
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .message(e.getMessage())
                    .data(null)
                    .build();
        } catch (Exception e) {
            return ApiResponse.<Void>builder()
                    .success(false)
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Lỗi hệ thống: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    // ==================== ADMIN ENDPOINTS ====================

    /**
     * ADMIN: Lấy danh sách tất cả đơn hàng
     * GET /api/v1/admin/orders?status=PENDING&page=1&size=20
     */
    @GetMapping("/admin/orders")
    public ApiResponse<PageResponse<OrderResponse>> getAllOrdersForAdmin(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        try {
            // Validate page and size
            if (page < 1) page = 1;
            if (size <= 0) size = 20;
            if (size > 100) size = 100;

            PageResponse<OrderResponse> pageResponse = orderService.getAllOrdersForAdmin(
                    status, searchTerm, page, size
            );

            return ApiResponse.<PageResponse<OrderResponse>>builder()
                    .success(true)
                    .statusCode(HttpStatus.OK.value())
                    .message("Lấy danh sách đơn hàng thành công")
                    .data(pageResponse)
                    .build();

        } catch (Exception e) {
            return ApiResponse.<PageResponse<OrderResponse>>builder()
                    .success(false)
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Lỗi khi tải danh sách đơn hàng: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    /**
     * ADMIN: Lấy chi tiết đơn hàng
     * GET /api/v1/admin/orders/{orderId}
     */
    @GetMapping("/admin/orders/{orderId}")
    public ApiResponse<OrderResponse> getOrderDetailForAdmin(@PathVariable int orderId) {
        try {
            OrderResponse orderResponse = orderService.getOrderDetails(orderId);

            return ApiResponse.<OrderResponse>builder()
                    .success(true)
                    .statusCode(HttpStatus.OK.value())
                    .message("Lấy chi tiết đơn hàng thành công")
                    .data(orderResponse)
                    .build();

        } catch (OrderException e) {
            return ApiResponse.<OrderResponse>builder()
                    .success(false)
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .message(e.getMessage())
                    .data(null)
                    .build();

        } catch (Exception e) {
            return ApiResponse.<OrderResponse>builder()
                    .success(false)
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Lỗi khi tải chi tiết đơn hàng: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    /**
     * ADMIN: Cập nhật trạng thái đơn hàng
     * PUT /api/v1/admin/orders/{orderId}/status
     * Body: { "status": "SHIPPING" }
     */
    @PutMapping("/admin/orders/{orderId}/status")
    public ApiResponse<Void> updateOrderStatus(
            @PathVariable int orderId,
            @RequestBody Map<String, String> requestBody
    ) {
        try {
            String newStatus = requestBody.get("status");

            // Validate status
            if (newStatus == null || newStatus.trim().isEmpty()) {
                return ApiResponse.<Void>builder()
                        .success(false)
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .message("Trạng thái không được để trống")
                        .data(null)
                        .build();
            }

            // Validate status value
            if (!isValidStatus(newStatus)) {
                return ApiResponse.<Void>builder()
                        .success(false)
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .message("Trạng thái không hợp lệ")
                        .data(null)
                        .build();
            }

            // Update status
            orderService.updateOrderStatus(orderId, newStatus);

            return ApiResponse.<Void>builder()
                    .success(true)
                    .statusCode(HttpStatus.OK.value())
                    .message("Cập nhật trạng thái thành công")
                    .data(null)
                    .build();

        } catch (OrderException e) {
            return ApiResponse.<Void>builder()
                    .success(false)
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .message(e.getMessage())
                    .data(null)
                    .build();

        } catch (Exception e) {
            return ApiResponse.<Void>builder()
                    .success(false)
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Lỗi khi cập nhật trạng thái: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    /**
     * ADMIN: Lấy thống kê đơn hàng
     * GET /api/v1/admin/orders/statistics
     */
    @GetMapping("/admin/orders/statistics")
    public ApiResponse<Map<String, Long>> getOrderStatistics() {
        try {
            Map<String, Long> stats = new HashMap<>();

            stats.put("totalOrders", orderService.countAllOrders());
            stats.put("pendingCount", orderService.countOrdersByStatus("PENDING"));
            stats.put("shippingCount", orderService.countOrdersByStatus("SHIPPING"));
            stats.put("deliveredCount", orderService.countOrdersByStatus("DELIVERED"));
            stats.put("cancelledCount", orderService.countOrdersByStatus("CANCELLED"));

            return ApiResponse.<Map<String, Long>>builder()
                    .success(true)
                    .statusCode(HttpStatus.OK.value())
                    .message("Lấy thống kê thành công")
                    .data(stats)
                    .build();

        } catch (Exception e) {
            return ApiResponse.<Map<String, Long>>builder()
                    .success(false)
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Lỗi khi tải thống kê: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    // ==================== HELPER METHODS ====================

    private boolean isValidStatus(String status) {
        return "PENDING".equals(status) ||
               "SHIPPING".equals(status) ||
               "DELIVERED".equals(status) ||
               "CANCELLED".equals(status);
    }
}
