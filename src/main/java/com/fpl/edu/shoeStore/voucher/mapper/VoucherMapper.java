package com.fpl.edu.shoeStore.voucher.mapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.fpl.edu.shoeStore.voucher.entity.Voucher;

@Mapper
public interface VoucherMapper {
    List<Voucher> findAll();

    Voucher findById(Integer id);

    // Trả về danh sách vì có thể một code có nhiều đợt phát hành
    List<Voucher> findByCode(String code);

    int insert(Voucher voucher);

    int update(Voucher voucher);

    int deleteById(Integer id);

    /**
     * Lưu ý XML: SQL Server cần dùng OFFSET #{offset} ROWS FETCH NEXT #{size} ROWS ONLY
     */
    List<Voucher> findAllPaged(
            @Param("voucherId") Integer voucherId,
            @Param("code") String code,
            @Param("description") String description,
            @Param("type") String type,
            @Param("discountValue") BigDecimal discountValue,
            @Param("minSpend") BigDecimal minSpend,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("usageLimit") Integer usageLimit,
            @Param("offset") int offset,
            @Param("size") int size
    );

    long countAll(
            @Param("voucherId") Integer voucherId,
            @Param("code") String code,
            @Param("description") String description,
            @Param("type") String type,
            @Param("discountValue") BigDecimal discountValue,
            @Param("minSpend") BigDecimal minSpend,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("usageLimit") Integer usageLimit
    );
}