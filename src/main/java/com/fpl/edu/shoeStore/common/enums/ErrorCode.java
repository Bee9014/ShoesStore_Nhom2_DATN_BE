package com.fpl.edu.shoeStore.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // ===== System errors =====
    SYSTEM_ERROR("1000", "Lỗi hệ thống. Vui lòng thử lại sau."),
    SQL_ERROR("1001", "Lỗi SQL. Vui lòng kiểm tra câu truy vấn."),
    UNEXPECTED_EXCEPTION("1002", "Lỗi không xác định."),
    PAGINATION_INVALID("1003", "Tham số phân trang không hợp lệ"),

    // ===== AUTH =====
    INVALID_CREDENTIALS("AUTH_001", "Tài khoản hoặc mật khẩu không chính xác"),
    INVALID_REFRESH_TOKEN("AUTH_002", "Refresh token không hợp lệ"),
    MISSING_OR_INVALID_TOKEN("AUTH_003", "Thiếu token hoặc token sai định dạng"),
    INVALID_TOKEN("AUTH_004", "Token không hợp lệ"),
    MISSING_CREDENTIAL("AUTH_005", "Thiếu credential"),
    FAILED_TO_CREATE_ACCOUNT("AUTH_007", "Không thể tạo tài khoản mới"),
    SERVER_ERROR("AUTH_008", "Lỗi server"),
    USERNAME_ALREADY_EXISTS("AUTH_009", "Username đã tồn tại."),
    GET_ACCOUNT_SUCCESS("AUTH_010", "Lấy account username thành công"),
    ACCOUNT_LOCKED("AUTH_011", "Tài khoản đã bị khóa, vui lòng liên hệ quản trị viên!"),
    ACCOUNT_DELETED("AUTH_012", "Tài khoản đã bị xóa!"),
    
    // ===== USER =====
    NOT_FOUND_USER("USER_001", "Không tìm thấy user"),
    DUPLICATE_USERNAME("USER_002", "Username đã tồn tại"),
    DUPLICATE_EMAIL("USER_003", "Email đã tồn tại"),
    DUPLICATE_PHONE("USER_004", "Số điện thoại đã tồn tại"),
    INSERT_USER_FAILED("USER_010", "Thêm user thất bại"),
    UPDATE_USER_FAILED("USER_011", "Cập nhật user thất bại"),
    DELETE_USER_FAILED("USER_012", "Xoá user thất bại"),
    SEARCH_USER_FAILED("USER_013", "Tìm kiếm user thất bại"),

    // ===== SUCCESS MESSAGE =====
    LOGIN_SUCCESS("AUTH_100", "Đăng nhập thành công"),
    LOGOUT_SUCCESS("AUTH_101", "Đăng xuất thành công"),
    REGISTER_SUCCESS("AUTH_102", "Đăng ký thành công"),
    
     // product 
     UPDATE_PRODUCT_FAILD("PRODUCT_100","CAP NHAT KO THANH CONG"),
     CREATE_PRODUCT_FAILD("PRODUCT_101","TAO MOI SAN PHAM KO THANH CONG"),



    USER_NOT_FOUND("USER_404", "Không tìm thấy thông tin người dùng");


   
    


    private final String code;
    private final String message;
}
