package com.fpl.edu.shoeStore.voucher.service;

import java.math.BigDecimal;
import java.util.List;

import com.fpl.edu.shoeStore.common.handler.PageResponse;
import com.fpl.edu.shoeStore.voucher.dto.request.VoucherDTORequest;
import com.fpl.edu.shoeStore.voucher.dto.response.VoucherCalcResponse;
import com.fpl.edu.shoeStore.voucher.dto.response.VoucherDTOResponse;

public interface VoucherService {
    PageResponse<VoucherDTOResponse> getAllVouchers(String keyword, String status, int page, int size);

    VoucherDTORequest getVoucherById(Integer id);

    void createVoucher(VoucherDTORequest request);

    void updateVoucher(Integer id, VoucherDTORequest request);

    void deleteVoucher(Integer id);

    /**
     * Kiểm tra và tính toán số tiền giảm giá
     * 
     * @param code       Mã voucher
     * @param orderTotal Tổng tiền đơn hàng
     * @param userId     ID người mua (để check lịch sử dùng)
     * @return Kết quả tính toán
     */
    VoucherCalcResponse applyVoucher(String code, BigDecimal orderTotal, Integer userId);

    List<VoucherDTOResponse> getValidVouchersForClient(BigDecimal cartTotal);

}
