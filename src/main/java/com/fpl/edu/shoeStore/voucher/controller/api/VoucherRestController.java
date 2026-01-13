package com.fpl.edu.shoeStore.voucher.controller.api;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fpl.edu.shoeStore.common.handler.ApiResponse;
import com.fpl.edu.shoeStore.common.handler.PageResponse;
import com.fpl.edu.shoeStore.voucher.dto.request.VoucherDTORequest;
import com.fpl.edu.shoeStore.voucher.dto.response.VoucherCalcResponse;
import com.fpl.edu.shoeStore.voucher.dto.response.VoucherDTOResponse;
import com.fpl.edu.shoeStore.voucher.entity.Voucher;
import com.fpl.edu.shoeStore.voucher.service.VoucherService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/vouchers")
@RequiredArgsConstructor
public class VoucherRestController {

    private final VoucherService voucherService;

    /**
     * Get All Vouchers
     * GET /api/v1/vouchers
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<VoucherDTOResponse>>> getAllVouchers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            PageResponse<VoucherDTOResponse> pageResponse = voucherService.getAllVouchers(keyword, status, page, size);
            return ResponseEntity.ok(ApiResponse.<PageResponse<VoucherDTOResponse>>builder()
                    .success(true)
                    .statusCode(HttpStatus.OK.value())
                    .message("Lấy danh sách voucher thành công")
                    .data(pageResponse)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<PageResponse<VoucherDTOResponse>>builder()
                            .success(false)
                            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .message("Lỗi hệ thống: " + e.getMessage())
                            .data(null)
                            .build());
        }
    }

    /**
     * Get Voucher By ID
     * GET /api/v1/vouchers/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VoucherDTORequest>> getVoucherById(
            @PathVariable Integer id) {
        try {
            VoucherDTORequest voucher = voucherService.getVoucherById(id);
            return ResponseEntity.ok(ApiResponse.<VoucherDTORequest>builder()
                    .success(true)
                    .statusCode(HttpStatus.OK.value())
                    .message("Lấy thông tin voucher thành công")
                    .data(voucher)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.<VoucherDTORequest>builder()
                            .success(false)
                            .statusCode(HttpStatus.NOT_FOUND.value())
                            .message("Voucher not found: " + e.getMessage())
                            .data(null)
                            .build());
        }
    }

    /**
     * Create Voucher
     * POST /api/v1/vouchers
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createVoucher(
            @RequestBody VoucherDTORequest request) {
        try {
            voucherService.createVoucher(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.<Void>builder()
                            .success(true)
                            .statusCode(HttpStatus.CREATED.value())
                            .message("Tạo voucher thành công")
                            .data(null)
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<Void>builder()
                            .success(false)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .message("Lỗi tạo voucher: " + e.getMessage())
                            .data(null)
                            .build());
        }
    }

    /**
     * Update Voucher
     * PUT /api/v1/vouchers/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updateVoucher(
            @PathVariable Integer id,
            @RequestBody VoucherDTORequest request) {
        try {
            voucherService.updateVoucher(id, request);
            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .success(true)
                    .statusCode(HttpStatus.OK.value())
                    .message("Cập nhật voucher thành công")
                    .data(null)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<Void>builder()
                            .success(false)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .message("Lỗi cập nhật voucher: " + e.getMessage())
                            .data(null)
                            .build());
        }
    }

    /**
     * Delete Voucher
     * DELETE /api/v1/vouchers/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteVoucher(
            @PathVariable Integer id) {
        try {
            voucherService.deleteVoucher(id);
            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .success(true)
                    .statusCode(HttpStatus.OK.value())
                    .message("Xóa voucher thành công")
                    .data(null)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<Void>builder()
                            .success(false)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .message("Lỗi xóa voucher: " + e.getMessage())
                            .data(null)
                            .build());
        }
    }

    /**
     * API CHO KHÁCH HÀNG: Kiểm tra và tính tiền giảm giá
     * GET /api/v1/vouchers/apply?code=SALE10&total=500000&userId=1
     */
    @GetMapping("/apply")
    public ResponseEntity<ApiResponse<VoucherCalcResponse>> applyVoucher(
            @RequestParam String code,
            @RequestParam BigDecimal total,
            @RequestParam Integer userId) {
        try {
            VoucherCalcResponse response = voucherService.applyVoucher(code, total, userId);
            return ResponseEntity.ok(ApiResponse.<VoucherCalcResponse>builder()
                    .success(true)
                    .statusCode(HttpStatus.OK.value())
                    .message(response.getMessage())
                    .data(response)
                    .build());
        } catch (Exception e) {
            // Trả về lỗi 400 để Frontend hiển thị thông báo đỏ
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<VoucherCalcResponse>builder()
                            .success(false)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .message(e.getMessage()) // Ví dụ: "Mã đã hết hạn"
                            .data(null)
                            .build());
        }
    }

    /**
     * [MỚI] API LẤY DANH SÁCH VOUCHER CHO KHÁCH HÀNG (POPUP)
     * Hỗ trợ logic Upsell: Trả về danh sách voucher kèm trạng thái đủ điều kiện hay
     * không
     * GET /api/v1/vouchers/valid?total=300000
     */
    @GetMapping("/valid")
    public ResponseEntity<ApiResponse<List<VoucherDTOResponse>>> getValidVouchers(
            @RequestParam(defaultValue = "0") BigDecimal total) {
        try {
            // Gọi Service để lấy list voucher đã được chấm điểm (Eligible/Not Eligible)
            List<VoucherDTOResponse> vouchers = voucherService.getValidVouchersForClient(total);

            return ResponseEntity.ok(ApiResponse.<List<VoucherDTOResponse>>builder()
                    .success(true)
                    .statusCode(HttpStatus.OK.value())
                    .message("Lấy danh sách voucher hợp lệ thành công")
                    .data(vouchers)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<List<VoucherDTOResponse>>builder()
                            .success(false)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .message("Lỗi lấy danh sách voucher: " + e.getMessage())
                            .data(null)
                            .build());
        }
    }

}
