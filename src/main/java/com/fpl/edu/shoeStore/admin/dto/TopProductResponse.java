package com.fpl.edu.shoeStore.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopProductResponse {
    private Integer productId;
    private String productName;
    private Long totalSold;
    private Double percentage;
}
