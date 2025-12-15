package com.fpl.edu.shoeStore.voucher.convert;

import com.fpl.edu.shoeStore.voucher.dto.request.VoucherDTORequest;
import com.fpl.edu.shoeStore.voucher.dto.response.VoucherDTOResponse;
import com.fpl.edu.shoeStore.voucher.entity.Voucher;

import java.util.List;
import java.util.stream.Collectors;

public class VoucherConverter {

    public static Voucher toEntity(VoucherDTORequest dto) {
        if (dto ==null) return null;

        Voucher voucher = new Voucher();

        voucher.setVoucherId(dto.getVoucherId());
        voucher.setCode(dto.getCode());
        voucher.setDescription(dto.getDescription());
        voucher.setType(dto.getType());
        voucher.setDiscountValue(dto.getDiscountValue());
        voucher.setMinSpend(dto.getMinSpend());
        voucher.setStartDate(dto.getStartDate());
        voucher.setEndDate(dto.getEndDate());
        voucher.setUsageLimit(dto.getUsageLimit());

        return voucher;
    }

    public static VoucherDTOResponse toDTO(Voucher voucher) {
        if (voucher == null) return null;

        return VoucherDTOResponse.builder()
                .voucherId(voucher.getVoucherId())
                .code(voucher.getCode())
                .description(voucher.getDescription())
                .type(voucher.getType())
                .discountValue(voucher.getDiscountValue())
                .minSpend(voucher.getMinSpend())
                .startDate(voucher.getStartDate())
                .endDate(voucher.getEndDate())
                .usageLimit(voucher.getUsageLimit())
                .createdAt(voucher.getCreatedAt())
                .updateAt(voucher.getUpdateAt())
                .build();
    }

    public static List<VoucherDTOResponse> toDTOList(List<Voucher> vouchers) {
        if (vouchers == null || vouchers.isEmpty()) return List.of();
        return vouchers.stream()
                .map(VoucherConverter::toDTO)
                .collect(Collectors.toList());
    }

}
