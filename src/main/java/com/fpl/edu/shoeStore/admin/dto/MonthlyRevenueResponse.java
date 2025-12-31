package com.fpl.edu.shoeStore.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyRevenueResponse {
    private Integer year;
    private List<BigDecimal> monthlyRevenue; // 12 months data
    private BigDecimal totalYearRevenue;
}
