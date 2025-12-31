package com.fpl.edu.shoeStore.admin.controller.api;

import com.fpl.edu.shoeStore.order.dto.response.OrderResponse;
import com.fpl.edu.shoeStore.admin.dto.DashboardSummaryResponse;
import com.fpl.edu.shoeStore.admin.dto.MonthlyRevenueResponse;
import com.fpl.edu.shoeStore.admin.dto.TopProductResponse;
import com.fpl.edu.shoeStore.admin.service.DashboardService;
import com.fpl.edu.shoeStore.common.handler.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    
    private final DashboardService dashboardService;
    
    /**
     * GET /api/v1/dashboard/summary
     * Lấy tổng quan thống kê
     */
    @GetMapping("/summary")
    public ApiResponse<DashboardSummaryResponse> getSummary() {
        try {
            DashboardSummaryResponse summary = dashboardService.getSummary();
            
            return ApiResponse.<DashboardSummaryResponse>builder()
                    .success(true)
                    .statusCode(HttpStatus.OK.value())
                    .message("Lấy thống kê tổng quan thành công")
                    .data(summary)
                    .build();
        } catch (Exception e) {
            return ApiResponse.<DashboardSummaryResponse>builder()
                    .success(false)
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Lỗi khi tải thống kê: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }
    
    /**
     * GET /api/v1/dashboard/revenue?year=2025
     * Lấy doanh thu theo tháng
     */
    @GetMapping("/revenue")
    public ApiResponse<MonthlyRevenueResponse> getRevenue(
            @RequestParam(required = false) Integer year
    ) {
        try {
            MonthlyRevenueResponse revenue = dashboardService.getMonthlyRevenue(year);
            
            return ApiResponse.<MonthlyRevenueResponse>builder()
                    .success(true)
                    .statusCode(HttpStatus.OK.value())
                    .message("Lấy doanh thu thành công")
                    .data(revenue)
                    .build();
        } catch (Exception e) {
            return ApiResponse.<MonthlyRevenueResponse>builder()
                    .success(false)
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Lỗi khi tải doanh thu: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }
    
    /**
     * GET /api/v1/dashboard/top-products?limit=5
     * Lấy top sản phẩm bán chạy
     */
    @GetMapping("/top-products")
    public ApiResponse<List<TopProductResponse>> getTopProducts(
            @RequestParam(defaultValue = "5") Integer limit
    ) {
        try {
            List<TopProductResponse> topProducts = dashboardService.getTopProducts(limit);
            
            return ApiResponse.<List<TopProductResponse>>builder()
                    .success(true)
                    .statusCode(HttpStatus.OK.value())
                    .message("Lấy sản phẩm bán chạy thành công")
                    .data(topProducts)
                    .build();
        } catch (Exception e) {
            return ApiResponse.<List<TopProductResponse>>builder()
                    .success(false)
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Lỗi khi tải sản phẩm bán chạy: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }
    
    /**
     * GET /api/v1/dashboard/recent-orders?limit=5
     * Lấy đơn hàng gần đây
     */
    @GetMapping("/recent-orders")
    public ApiResponse<List<OrderResponse>> getRecentOrders(
            @RequestParam(defaultValue = "5") Integer limit
    ) {
        try {
            List<OrderResponse> recentOrders = dashboardService.getRecentOrders(limit);
            
            return ApiResponse.<List<OrderResponse>>builder()
                    .success(true)
                    .statusCode(HttpStatus.OK.value())
                    .message("Lấy đơn hàng gần đây thành công")
                    .data(recentOrders)
                    .build();
        } catch (Exception e) {
            return ApiResponse.<List<OrderResponse>>builder()
                    .success(false)
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Lỗi khi tải đơn hàng: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }
}
