package com.fpl.edu.shoeStore.promotion.mapper;

import com.fpl.edu.shoeStore.promotion.entity.Voucher;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface VoucherMapper {
    List<Voucher> findAll();

    Voucher findById(@Param("id") Integer id);

    Voucher findByCode(@Param("code") String code);

    int insert(Voucher voucher);

    int update(Voucher voucher);

    int deleteById(@Param("id") Integer id);

    List<Voucher> findAllPaged(
            @Param("voucherId") Integer voucherId,
            @Param("code") String code,
            @Param("voucherType") String voucherType,
            @Param("minSpend") Double minSpend,
            @Param("offset") int offset,
            @Param("size") int size
    );

    long countAll(
            @Param("voucherId") Integer voucherId,
            @Param("code") String code,
            @Param("voucherType") String voucherType
    );
}