package com.fpl.edu.shoeStore.voucher.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fpl.edu.shoeStore.common.handler.PageResponse;
import com.fpl.edu.shoeStore.voucher.convert.VoucherConverter;
import com.fpl.edu.shoeStore.voucher.dto.request.VoucherDTORequest;
import com.fpl.edu.shoeStore.voucher.dto.response.VoucherCalcResponse;
import com.fpl.edu.shoeStore.voucher.dto.response.VoucherDTOResponse;
import com.fpl.edu.shoeStore.voucher.entity.Voucher;
import com.fpl.edu.shoeStore.voucher.mapper.VoucherMapper;
import com.fpl.edu.shoeStore.voucher.service.VoucherService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VoucherServiceImpl implements VoucherService {

    private final VoucherMapper voucherMapper;

    @Override
    public PageResponse<VoucherDTOResponse> getAllVouchers(String keyword, String status, int page, int size) {
        int offset = (page - 1) * size;

        List<Voucher> vouchers = voucherMapper.findAllPaged(keyword, status, offset, size);
        long totalElements = voucherMapper.countAll(keyword, status);

        List<VoucherDTOResponse> content = vouchers.stream()
                .map(VoucherConverter::toResponse)
                .toList();

        int totalPages = (int) Math.ceil((double) totalElements / size);

        return PageResponse.<VoucherDTOResponse>builder()
                .pageNumber(page)
                .pageSize(size)
                .totalPages(totalPages)
                .totalElements(totalElements)
                .content(content)
                .build();
    }

    @Override
    public VoucherDTORequest getVoucherById(Integer id) {
        Voucher voucher = voucherMapper.findById(id);
        if (voucher == null) {
            throw new RuntimeException("Voucher not found");
        }
        // Map Entity to DTO Request for form binding using Manual Builder or a reverse
        // converter method
        // Here we can reuse Builder similar to converter
        return VoucherDTORequest.builder()
                .voucherId(voucher.getVoucherId())
                .code(voucher.getCode())
                .discountType(voucher.getDiscountType())
                .discountValue(voucher.getDiscountValue())
                .maxDiscountAmount(voucher.getMaxDiscountAmount())
                .minOrderValue(voucher.getMinOrderValue())
                .description(voucher.getDescription())
                .usageLimit(voucher.getUsageLimit())
                .usageLimitPerUser(voucher.getUsageLimitPerUser())
                .startDate(voucher.getStartDate())
                .endDate(voucher.getEndDate())
                .isActive(voucher.getIsActive())
                .build();
    }

    @Override
    @Transactional
    public void createVoucher(VoucherDTORequest request) {
        // Validation basic
        if (request.getCode() == null || request.getCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã Voucher không được để trống");
        }
        if (request.getStartDate() != null && request.getEndDate() != null) {
            if (request.getStartDate().isAfter(request.getEndDate())) {
                throw new IllegalArgumentException("Ngày bắt đầu phải trước ngày kết thúc");
            }
        }
        if (voucherMapper.findByCode(request.getCode()) != null) {
            throw new IllegalArgumentException("Mã Voucher đã tồn tại");
        }

        Voucher voucher = VoucherConverter.toEntity(request);
        voucher.setCode(request.getCode().toUpperCase());
        voucher.setUsedCount(0);

        // Default usagePerUser = 1 if null (Schema says DEFAULT 1)
        if (voucher.getUsageLimitPerUser() == null) {
            voucher.setUsageLimitPerUser(1);
        }

        voucherMapper.insert(voucher);
    }

    @Override
    @Transactional
    public void updateVoucher(Integer id, VoucherDTORequest request) {
        Voucher existing = voucherMapper.findById(id);
        if (existing == null) {
            throw new RuntimeException("Voucher Not Found");
        }

        if (request.getStartDate() != null && request.getEndDate() != null) {
            if (request.getStartDate().isAfter(request.getEndDate())) {
                throw new IllegalArgumentException("Ngày bắt đầu phải trước ngày kết thúc");
            }
        }

        // Check unique code if changed
        if (!existing.getCode().equalsIgnoreCase(request.getCode())) {
            if (voucherMapper.findByCode(request.getCode()) != null) {
                throw new IllegalArgumentException("Mã Voucher đã tồn tại");
            }
        }

        existing.setCode(request.getCode().toUpperCase());
        existing.setDiscountType(request.getDiscountType());
        existing.setDiscountValue(request.getDiscountValue());

        // Logic: Fixed Amount -> maxDiscount = null
        if ("FIXED_AMOUNT".equals(request.getDiscountType())) {
            existing.setMaxDiscountAmount(null);
        } else {
            existing.setMaxDiscountAmount(request.getMaxDiscountAmount());
        }

        existing.setMinOrderValue(request.getMinOrderValue());
        existing.setDescription(request.getDescription());
        existing.setUsageLimit(request.getUsageLimit());
        existing.setUsageLimitPerUser(request.getUsageLimitPerUser());
        existing.setStartDate(request.getStartDate());
        existing.setEndDate(request.getEndDate());
        existing.setIsActive(request.getIsActive());

        voucherMapper.update(existing);
    }

    @Override
    @Transactional
    public void deleteVoucher(Integer id) {
        voucherMapper.softDelete(id);
    }

    @Override
    public VoucherCalcResponse applyVoucher(String code, BigDecimal orderTotal, Integer userId) {
        // 1. Tìm voucher trong DB
        Voucher v = voucherMapper.findByCode(code);

        // Check tồn tại và trạng thái kích hoạt
        if (v == null || !v.getIsActive()) {
            throw new RuntimeException("Mã giảm giá không tồn tại hoặc đã bị khóa");
        }

        // 2. Kiểm tra ngày hết hạn
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(v.getStartDate())) {
            throw new RuntimeException("Mã giảm giá chưa bắt đầu");
        }
        if (now.isAfter(v.getEndDate())) {
            throw new RuntimeException("Mã giảm giá đã hết hạn");
        }

        // 3. Kiểm tra số lượng tổng (Global limit)
        if (v.getUsedCount() >= v.getUsageLimit()) {
            throw new RuntimeException("Mã giảm giá đã hết lượt sử dụng");
        }

        // 4. Kiểm tra giá trị đơn hàng tối thiểu
        if (orderTotal.compareTo(v.getMinOrderValue()) < 0) {
            // Format tiền cho dễ đọc (Tùy chọn)
            throw new RuntimeException("Đơn hàng phải từ " + v.getMinOrderValue() + "đ mới được áp dụng mã này");
        }

        // 5. Kiểm tra lịch sử sử dụng của User (Mỗi người dùng N lần)
        // Cần đảm bảo Mapper đã có hàm countUsageByUser
        int usedTimes = voucherMapper.countUsageByUser(userId, v.getVoucherId());
        if (usedTimes >= v.getUsageLimitPerUser()) {
            throw new RuntimeException(
                    "Bạn đã hết lượt sử dụng mã này (" + usedTimes + "/" + v.getUsageLimitPerUser() + ")");
        }

        // 6. Tính toán tiền giảm
        BigDecimal discount = BigDecimal.ZERO;

        if ("FIXED_AMOUNT".equals(v.getDiscountType())) {
            // Giảm thẳng tiền mặt
            discount = v.getDiscountValue();
        } else {
            // Giảm theo %: (Total * Value) / 100
            discount = orderTotal.multiply(v.getDiscountValue())
                    .divide(new BigDecimal(100));

            // Nếu có Giảm tối đa (Max Discount) thì so sánh
            if (v.getMaxDiscountAmount() != null && discount.compareTo(v.getMaxDiscountAmount()) > 0) {
                discount = v.getMaxDiscountAmount();
            }
        }

        // Tiền giảm không được lớn hơn tổng tiền đơn hàng (tránh âm tiền)
        if (discount.compareTo(orderTotal) > 0) {
            discount = orderTotal;
        }

        return VoucherCalcResponse.builder()
                .voucherId(v.getVoucherId())
                .code(v.getCode())
                .discountAmount(discount)
                .isValid(true)
                .message("Áp dụng mã thành công!")
                .build();
    }

    @Override
    public List<VoucherDTOResponse> getValidVouchersForClient(BigDecimal cartTotal) {
        // Xử lý null
        BigDecimal actualTotal = (cartTotal == null) ? BigDecimal.ZERO : cartTotal;

        // 1. Lấy tất cả voucher active, còn hạn, còn lượt từ DB
        List<Voucher> vouchers = voucherMapper.findValidVouchers();
        List<VoucherDTOResponse> response = new ArrayList<>();

        for (Voucher v : vouchers) {
            VoucherDTOResponse dto = VoucherConverter.toResponse(v);

            // 2. Logic kiểm tra điều kiện Upsell
            if (actualTotal.compareTo(v.getMinOrderValue()) >= 0) {
                // Đủ điều kiện
                dto.setEligible(true);
                dto.setReason("Có thể áp dụng");
                dto.setMissingAmount(BigDecimal.ZERO);
            } else {
                // Chưa đủ điều kiện -> Tính tiền thiếu
                dto.setEligible(false);
                BigDecimal missing = v.getMinOrderValue().subtract(actualTotal);
                dto.setMissingAmount(missing);
                dto.setReason("Mua thêm " + missing + "đ để sử dụng");
            }
            response.add(dto);
        }

        // 3. Sắp xếp: Voucher dùng được (Eligible = true) lên đầu
        response.sort((v1, v2) -> Boolean.compare(v2.isEligible(), v1.isEligible()));

        return response;
    }

}
