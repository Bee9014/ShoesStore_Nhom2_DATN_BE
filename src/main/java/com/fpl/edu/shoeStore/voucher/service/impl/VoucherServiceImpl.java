package com.fpl.edu.shoeStore.voucher.service.impl;


import com.fpl.edu.shoeStore.common.handler.PageResponse;
import com.fpl.edu.shoeStore.voucher.convert.VoucherConverter;
import com.fpl.edu.shoeStore.voucher.dto.request.VoucherDTORequest;
import com.fpl.edu.shoeStore.voucher.dto.response.VoucherDTOResponse;
import com.fpl.edu.shoeStore.voucher.entity.Voucher;
import com.fpl.edu.shoeStore.voucher.mapper.VoucherMapper;
import com.fpl.edu.shoeStore.voucher.service.VoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VoucherServiceImpl implements VoucherService {

    private final VoucherMapper voucherMapper;

    @Override
    @Transactional
    public PageResponse<VoucherDTOResponse> findAllPaged(Integer voucherId, String code, String description, String type, BigDecimal discountValue, BigDecimal minSpend, LocalDateTime startDate, LocalDateTime endDate, Integer usageLimit, int page, int size) {
        int offset = (page - 1) * size;

        List<Voucher> vouchers = voucherMapper.findAllPaged(voucherId, code, description, type, discountValue, minSpend, startDate, endDate, usageLimit, offset, size);

        long totalElements = voucherMapper.countAll(voucherId, code,description, type, discountValue, minSpend, startDate, endDate, usageLimit);

        List<VoucherDTOResponse> voucherDTOs = VoucherConverter.toDTOList(vouchers);

        int totalPages = (int) Math.ceil((double) totalElements / size);

        return PageResponse.<VoucherDTOResponse>builder()
                .content(voucherDTOs)
                .pageNumber(page)
                .pageSize(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .build();
    };

    @Override
    public VoucherDTOResponse createVoucher(VoucherDTORequest request) {
        // check trùng Code
        if(voucherMapper.findByCode(request.getCode()) != null) {
            throw new RuntimeException("Code "+request.getCode()+"đã tồn tại !!!");
        }

        // validate ngày bắt đầu và kết thúc
        validateDay(request.getStartDate(), request.getEndDate());

        Voucher voucher = VoucherConverter.toEntity(request);
        voucher.setCreatedAt(LocalDateTime.now());
        voucher.setUpdateAt(LocalDateTime.now());

        voucherMapper.insert(voucher);

        return VoucherConverter.toDTO(voucher);

    }

    @Override
    @Transactional
    public VoucherDTOResponse updateVoucher(Integer id, VoucherDTORequest request) {
        Voucher existsVoucher = voucherMapper.findById(id);
        if(existsVoucher == null ){
            throw new RuntimeException("Không tìm thấy Voucher có ID: "+id);
        }

        // check Code chuẩn bị thay đổi có trùng với Code đã tồn tại !!!
        if(request.getCode() != null && !request.getCode().equals(existsVoucher.getCode())){
            if(voucherMapper.findByCode(request.getCode()) != null) {
                throw new RuntimeException("Mã Voucher "+ request.getCode() + "đã được sử dụng bởi Voucher khác !!!");
            }
        }

        // Validate ngày
        // nếu chỉ cập nhật có ngày bắt đầu hoặc ngày kết thúc
        if (request.getStartDate() != null || request.getEndDate() != null){
            // Logic
            //                 ĐIỀU KIỆN                    ?       GIÁ TRỊ NẾU ĐÚNG        :   GIÁ TRỊ NẾU SAI
            //KIỂM TRA NẾU GIÁ TRỊ STARTDATE HOẶC ENDDATE RỖNG      ? NẾU CÓ LẤY GIÁ TRỊ MỚI    :   NẾU KHÔNG LẤY GIÁ TRỊ CŨ
            LocalDateTime newStart = request.getStartDate() != null ? request.getStartDate() : existsVoucher.getStartDate();
            LocalDateTime newEnd = request.getEndDate() != null ? request.getEndDate() : existsVoucher.getEndDate();

            //VALIDATE GIÁ TRỊ MỚI
            validateDay(newStart, newEnd);

            //GÁN GIÁ TRỊ ĐÃ ĐƯỢC VALIDATE VÀO DATABASE.
            if (request.getStartDate() != null) existsVoucher.setStartDate(request.getStartDate());
            if (request.getEndDate() != null) existsVoucher.setEndDate(request.getEndDate());
        }

        if(request.getCode() != null) existsVoucher.setCode((request.getCode()));
        if(request.getDescription() != null) existsVoucher.setDescription(request.getDescription());
        if (request.getType() != null) existsVoucher.setType(request.getType());
        if (request.getDiscountValue() != null) existsVoucher.setDiscountValue(request.getDiscountValue());
        if (request.getMinSpend() != null) existsVoucher.setMinSpend(request.getMinSpend());
        if (request.getUsageLimit() != null) existsVoucher.setUsageLimit(request.getUsageLimit());

        existsVoucher.setUpdateAt(LocalDateTime.now());

        voucherMapper.update(existsVoucher);

        return VoucherConverter.toDTO(existsVoucher);

    }

    @Override
    @Transactional
    public int deleteVoucher(Integer id) {
        Voucher existsVoucher = voucherMapper.findById(id);
        if (existsVoucher == null ){
            throw new RuntimeException("Không tìm thấy Voucher có ID: " + id);
        }
        return voucherMapper.deleteById(id);
    }

    @Override
    public VoucherDTOResponse findById(Integer id) {
        Voucher voucher = voucherMapper.findById(id);
        if (voucher == null) {
            throw new RuntimeException("Không tìm thấy Voucher có ID: "+ id);
        }

        return VoucherConverter.toDTO(voucher);
    }

    @Override
    public VoucherDTOResponse findByCode(String code) {
        Voucher voucher = voucherMapper.findByCode(code);
        if (voucher == null) {
            throw new RuntimeException("Không tìm thấy Voucher có Code: " + code);
        }
        return VoucherConverter.toDTO(voucher);
    }

    private void validateDay (LocalDateTime start, LocalDateTime end){
        if(start != null && end != null && start.isAfter(end)){
            throw new RuntimeException("Ngày bắt đầu không thể sau ngay kết thúc!");
        }
    }
}
