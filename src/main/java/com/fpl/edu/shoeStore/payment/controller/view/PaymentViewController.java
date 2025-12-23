package com.fpl.edu.shoeStore.payment.controller.view;

import com.fpl.edu.shoeStore.payment.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Payment View Controller - Admin SSR
 * Render Thymeleaf templates cho admin payment management
 */
@Controller
@RequestMapping("/admin/payments")
@RequiredArgsConstructor
public class PaymentViewController {

    private final PaymentService paymentService;

    /**
     * Trang danh sách payments
     * GET /admin/payments
     */
    @GetMapping({"", "/"})
    public String paymentListPage(Model model, HttpServletRequest request) {
        model.addAttribute("pageTitle", "Quản lý thanh toán");
        model.addAttribute("currentPath", request.getRequestURI());
        
        // Có thể thêm dữ liệu ban đầu nếu cần
        // Ví dụ: thống kê số payment theo status
        // model.addAttribute("totalPayments", paymentService.countAll(...));
        
        return "admin/pages/payment-list";
    }

    /**
     * Trang chi tiết payment
     * GET /admin/payments/{id}
     */
    @GetMapping("/{id}")
    public String paymentDetailPage(
            @PathVariable Integer id,
            Model model,
            HttpServletRequest request
    ) {
        model.addAttribute("pageTitle", "Chi tiết thanh toán");
        model.addAttribute("currentPage", "payment");
        model.addAttribute("paymentId", id);
        
        return "admin/pages/payment-detail";
    }

    /**
     * Trang báo cáo thanh toán
     * GET /admin/payments/reports
     */
    @GetMapping("/reports")
    public String paymentReportsPage(Model model, HttpServletRequest request) {
        model.addAttribute("pageTitle", "Báo cáo thanh toán");
        model.addAttribute("currentPath", request.getRequestURI());
        
        return "admin/pages/payment-reports";
    }
}
