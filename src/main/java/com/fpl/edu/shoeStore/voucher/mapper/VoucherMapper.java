package com.fpl.edu.shoeStore.voucher.mapper;

import com.fpl.edu.shoeStore.voucher.dto.response.VoucherDTOResponse;
import com.fpl.edu.shoeStore.voucher.entity.Voucher;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface VoucherMapper {
    // CRUD methods for Voucher
    List<Voucher> findAll();

    Voucher findById(Integer id);

    List<Voucher> findByCode(String code);

    int insert(Voucher voucher);

    int update(Voucher voucher);

    int deleteById(Integer id);

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
