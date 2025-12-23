package com.fpl.edu.shoeStore.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * AuthViewController - Handle server-side rendering for Admin authentication
 * User authentication is handled by VueJS SPA (uses API endpoints only)
 */
@Controller
public class AuthViewController {

    /**
     * Admin login page - Server-side rendered with Thymeleaf
     */
    @GetMapping("/admin/login")
    public String adminLoginPage(
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String success,
            Model model) {
        
        if (error != null) {
            model.addAttribute("error", "Đăng nhập thất bại. Vui lòng kiểm tra lại thông tin.");
        }
        
        if (success != null) {
            model.addAttribute("success", success);
        }
        
        return "admin/login";
    }

    /**
     * Admin logout - Redirect to admin login
     */
    @GetMapping("/admin/logout")
    public String adminLogout() {
        // Clear session and redirect to admin login
        return "redirect:/admin/login?success=" + java.net.URLEncoder.encode("Đăng xuất thành công", java.nio.charset.StandardCharsets.UTF_8);
    }
}
