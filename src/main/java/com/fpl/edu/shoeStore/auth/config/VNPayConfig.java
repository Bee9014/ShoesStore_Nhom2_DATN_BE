package com.fpl.edu.shoeStore.auth.config;

import java.nio.charset.StandardCharsets;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.context.annotation.Configuration;

import jakarta.servlet.http.HttpServletRequest;


@Configuration
public class VNPayConfig {
    public static final String vnp_PayUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";

    public static String vnp_ReturnUrl = "http://localhost:3000/payment-result";

    // 👇 Đã điền mã từ email của bạn
    public static String vnp_TmnCode = "D1WF0ODA"; 
    
    // 👇 Đã điền chuỗi bí mật từ email của bạn
    public static String vnp_HashSecret = "YFUIDX7HG7IZ8GOWKTP3IBTHA1UWK28U"; 
    
    public static String vnp_ApiUrl = "https://sandbox.vnpayment.vn/merchant_webapi/api/transaction";

    // --- Giữ nguyên các hàm tiện ích bên dưới ---
    public static String hmacSHA512(String key, String data) {
        try {
            if (key == null || data == null) { return null; }
            Mac hmac512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmac512.init(secretKey);
            byte[] result = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception ex) { return ""; }
    }

    public static String getIpAddress(HttpServletRequest request) {
        String ipAdress;
        try {
            ipAdress = request.getHeader("X-FORWARDED-FOR");
            if (ipAdress == null) { ipAdress = request.getRemoteAddr(); }
        } catch (Exception e) { ipAdress = "Invalid IP:" + e.getMessage(); }
        return ipAdress;
    }

    public static String getRandomNumber(int len) {
        Random rnd = new Random();
        String chars = "0123456789";
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) { sb.append(chars.charAt(rnd.nextInt(chars.length()))); }
        return sb.toString();
    }
}
