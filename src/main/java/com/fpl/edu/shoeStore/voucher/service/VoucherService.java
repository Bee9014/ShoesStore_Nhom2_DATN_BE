package com.fpl.edu.shoeStore.voucher.service;

import com.fpl.edu.shoeStore.common.handler.PageResponse;
import com.fpl.edu.shoeStore.voucher.dto.request.VoucherDTORequest;
import com.fpl.edu.shoeStore.voucher.dto.response.VoucherDTOResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface VoucherService {

    PageResponse<VoucherDTOResponse> findAllPaged(
            Integer voucherId,
            String code,
            String description,
            String type,
            BigDecimal discountValue,
            BigDecimal minSpend,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Integer usageLimit,
            int page,
            int size
    );

    VoucherDTOResponse createVoucher(VoucherDTORequest request);

    VoucherDTOResponse updateVoucher(Integer id, VoucherDTORequest request);

    int deleteVoucher(Integer id);

    VoucherDTOResponse findById(Integer id);

    VoucherDTOResponse findByCode(String code);
}
