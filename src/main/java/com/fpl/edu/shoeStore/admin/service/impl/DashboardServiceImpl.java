package com.fpl.edu.shoeStore.admin.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fpl.edu.shoeStore.admin.dto.DashboardSummaryResponse;
import com.fpl.edu.shoeStore.admin.dto.MonthlyRevenueResponse;
import com.fpl.edu.shoeStore.admin.dto.TopProductResponse;
import com.fpl.edu.shoeStore.admin.service.DashboardService;
import com.fpl.edu.shoeStore.order.dto.response.OrderResponse;
import com.fpl.edu.shoeStore.order.mapper.OrderMapper;
import com.fpl.edu.shoeStore.order.service.OrderService;
import com.fpl.edu.shoeStore.product.mapper.ProductMapper;
import com.fpl.edu.shoeStore.product.mapper.ProductVariantMapper;
import com.fpl.edu.shoeStore.user.mapper.UserMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {
    
    private final OrderMapper orderMapper;
    private final ProductMapper productMapper;
    private final ProductVariantMapper productVariantMapper;
    private final UserMapper userMapper;
    private final OrderService orderService;

    @Override
    public DashboardSummaryResponse getSummary() {
        // Get current month and year
        LocalDateTime now = LocalDateTime.now();
        int currentYear = now.getYear();
        int currentMonth = now.getMonthValue();
        
        // Count total orders
        Long totalOrders = orderMapper.countAllOrders();
        
        // Calculate total revenue (from completed orders)
        BigDecimal totalRevenue = orderMapper.calculateTotalRevenue();
        if (totalRevenue == null) {
            totalRevenue = BigDecimal.ZERO;
        }
        
        // Count total products
        Long totalProducts = productMapper.countAllProducts();
        
        // Count total users
        Long totalUsers = userMapper.countAllUsers();
        
        // Count low stock products (stockQty < 10)
        Long lowStockProducts = productVariantMapper.countLowStock(10);
        
        // Count new users this month
        Long newUsersThisMonth = userMapper.countNewUsersInMonth(currentYear, currentMonth);
        
        return DashboardSummaryResponse.builder()
                .totalOrders(totalOrders != null ? totalOrders : 0L)
                .totalRevenue(totalRevenue)
                .totalProducts(totalProducts != null ? totalProducts : 0L)
                .totalUsers(totalUsers != null ? totalUsers : 0L)
                .lowStockProducts(lowStockProducts != null ? lowStockProducts : 0L)
                .newUsersThisMonth(newUsersThisMonth != null ? newUsersThisMonth : 0L)
                .build();
    }

    @Override
    public MonthlyRevenueResponse getMonthlyRevenue(Integer year) {
        if (year == null) {
            year = LocalDateTime.now().getYear();
        }
        
        List<BigDecimal> monthlyRevenue = new ArrayList<>();
        BigDecimal totalYearRevenue = BigDecimal.ZERO;
        
        // Get revenue for each month
        for (int month = 1; month <= 12; month++) {
            BigDecimal revenue = orderMapper.calculateRevenueByMonth(year, month);
            if (revenue == null) {
                revenue = BigDecimal.ZERO;
            }
            monthlyRevenue.add(revenue);
            totalYearRevenue = totalYearRevenue.add(revenue);
        }
        
        return MonthlyRevenueResponse.builder()
                .year(year)
                .monthlyRevenue(monthlyRevenue)
                .totalYearRevenue(totalYearRevenue)
                .build();
    }

    @Override
    public List<TopProductResponse> getTopProducts(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 5;
        }
        
        // Get top selling products from order_items
        List<Map<String, Object>> topProducts = orderMapper.findTopSellingProducts(limit);
        
        // Calculate total sold for percentage
        long totalSold = topProducts.stream()
                .mapToLong(m -> ((Number) m.get("total_sold")).longValue())
                .sum();
        
        return topProducts.stream()
                .map(map -> {
                    Long sold = ((Number) map.get("total_sold")).longValue();
                    Double percentage = totalSold > 0 
                            ? (sold * 100.0 / totalSold)
                            : 0.0;
                    
                    return TopProductResponse.builder()
                            .productId((Integer) map.get("product_id"))
                            .productName((String) map.get("product_name"))
                            .totalSold(sold)
                            .percentage(Math.round(percentage * 10.0) / 10.0) // Round to 1 decimal
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderResponse> getRecentOrders(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 5;
        }
        
        // Reuse OrderService method with pagination
        return orderService.getAllOrders(null, null, 0, limit).getContent();
    }
}
