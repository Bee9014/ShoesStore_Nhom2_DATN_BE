package com.fpl.edu.shoeStore.voucher.mapper;

import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.fpl.edu.shoeStore.voucher.entity.Voucher;

@Mapper
public interface VoucherMapper {
        List<Voucher> findAll();

        List<Voucher> findAllPaged(@Param("keyword") String keyword, @Param("status") String status,
                        @Param("offset") int offset, @Param("size") int size);

        long countAll(@Param("keyword") String keyword, @Param("status") String status);

        Voucher findById(Integer id);

        Voucher findByCode(String code);

        int insert(Voucher voucher);

        int update(Voucher voucher);

        int softDelete(Integer id);

        // 1. Đếm số lần user đã dùng voucher (Để chặn nếu vượt quá giới hạn)
        int countUsageByUser(@Param("userId") Integer userId, @Param("voucherId") Integer voucherId);

        // 2. Tăng số lượng đã dùng của voucher lên 1 (Trừ kho voucher)
        void incrementUsedCount(@Param("voucherId") Integer voucherId);

        // 3. Lưu lịch sử sử dụng vào bảng voucher_usage_history
        void insertHistory(@Param("userId") Integer userId,
                        @Param("voucherId") Integer voucherId,
                        @Param("orderId") Integer orderId);

        List<Voucher> findValidVouchers();

}