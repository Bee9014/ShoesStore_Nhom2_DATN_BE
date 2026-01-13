package com.fpl.edu.shoeStore.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RegisterRequestDto {
    @NotBlank(message = "Username không được để trống")
    private String username;

    @NotBlank(message = "Password không được để trống")
    private String password;

    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{3,6}$", message = "Email phải có định dạng hợp lệ (ví dụ: .com)")
    private String email;

    @Pattern(regexp = "^(0|\\+84)[0-9]{9,10}$", message = "Số điện thoại không hợp lệ")
    private String phone;

    private Integer gender; // 1: Nam, 0: Nữ (hoặc ngược lại tùy quy ước)

    // Nếu frontend gửi chuỗi yyyy-MM-dd, Jackson sẽ tự parse vào LocalDate nếu có
    // cấu hình hoặc dùng @JsonFormat
    // Tuy nhiên để đơn giản có thể dùng String rồi parse, hoặc dùng LocalDate trực
    // tiếp
    private java.time.LocalDate birthday;
}
