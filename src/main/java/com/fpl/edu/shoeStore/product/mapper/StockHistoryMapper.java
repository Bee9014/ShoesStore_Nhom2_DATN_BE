package com.fpl.edu.shoeStore.product.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.fpl.edu.shoeStore.product.entity.StockHistory;

@Mapper
public interface StockHistoryMapper {
    void insert(StockHistory history);
    List<StockHistory> findByVariantId(@Param("variantId") Integer variantId);
}