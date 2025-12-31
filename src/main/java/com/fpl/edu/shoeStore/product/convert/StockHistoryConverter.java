package com.fpl.edu.shoeStore.product.convert;

import com.fpl.edu.shoeStore.product.dto.response.StockHistoryDtoResponse;
import com.fpl.edu.shoeStore.product.entity.StockHistory;

public class StockHistoryConverter {

    public static StockHistoryDtoResponse toResponse(StockHistory entity) {
        return StockHistoryDtoResponse.builder()
                .historyId(entity.getHistoryId())
                .variantId(entity.getVariantId())
                .amount(entity.getAmount())
                .stockBefore(entity.getStockBefore())
                .stockAfter(entity.getStockAfter())
                .note(entity.getNote())
                .createBy(String.valueOf(entity.getCreateBy())) // Chuyển ID người tạo sang String
                .createAt(entity.getCreateAt())
                .build();

               
    }
}