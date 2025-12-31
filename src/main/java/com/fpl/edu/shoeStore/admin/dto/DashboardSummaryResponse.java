package com.fpl.edu.shoeStore.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryResponse {
    private Long totalOrders;
    private BigDecimal totalRevenue;
    private Long totalProducts;
    private Long totalUsers;
    private Long lowStockProducts;
    private Long newUsersThisMonth;
}
