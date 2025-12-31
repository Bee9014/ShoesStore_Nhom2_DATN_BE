package com.fpl.edu.shoeStore.admin.service;

import java.util.List;

import com.fpl.edu.shoeStore.admin.dto.DashboardSummaryResponse;
import com.fpl.edu.shoeStore.admin.dto.MonthlyRevenueResponse;
import com.fpl.edu.shoeStore.admin.dto.TopProductResponse;
import com.fpl.edu.shoeStore.order.dto.response.OrderResponse;

public interface DashboardService {
    
    /**
     * Get dashboard summary statistics
     */
    DashboardSummaryResponse getSummary();
    
    /**
     * Get monthly revenue for a specific year
     */
    MonthlyRevenueResponse getMonthlyRevenue(Integer year);
    
    /**
     * Get top selling products
     */
    List<TopProductResponse> getTopProducts(Integer limit);
    
    /**
     * Get recent orders
     */
    List<OrderResponse> getRecentOrders(Integer limit);
}
