package com.fpl.edu.shoeStore.voucher.convert;

import java.util.List;
import java.util.stream.Collectors;

import com.fpl.edu.shoeStore.voucher.dto.request.VoucherDTORequest;
import com.fpl.edu.shoeStore.voucher.dto.response.VoucherDTOResponse;
import com.fpl.edu.shoeStore.voucher.entity.Voucher;

public class VoucherConverter {

    public static Voucher toEntity(VoucherDTORequest dto) {
        if (dto == null)
            return null;
        return Voucher.builder()
                .voucherId(dto.getVoucherId())
                .code(dto.getCode())
                .discountType(dto.getDiscountType())
                .discountValue(dto.getDiscountValue())
                .maxDiscountAmount(dto.getMaxDiscountAmount())
                .minOrderValue(dto.getMinOrderValue())
                .description(dto.getDescription())
                .usageLimit(dto.getUsageLimit())
                .usageLimitPerUser(dto.getUsageLimitPerUser())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .isActive(dto.getIsActive())
                .build();
    }

    public static VoucherDTOResponse toResponse(Voucher entity) {
        if (entity == null)
            return null;
        return VoucherDTOResponse.builder()
                .voucherId(entity.getVoucherId())
                .code(entity.getCode())
                .discountType(entity.getDiscountType())
                .discountValue(entity.getDiscountValue())
                .maxDiscountAmount(entity.getMaxDiscountAmount())
                .minOrderValue(entity.getMinOrderValue())
                .description(entity.getDescription())
                .usageLimit(entity.getUsageLimit())
                .usedCount(entity.getUsedCount())
                .usageLimitPerUser(entity.getUsageLimitPerUser())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public static List<VoucherDTOResponse> toResponseList(List<Voucher> entities) {
        if (entities == null)
            return List.of();
        return entities.stream()
                .map(VoucherConverter::toResponse)
                .collect(Collectors.toList());
    }
}
