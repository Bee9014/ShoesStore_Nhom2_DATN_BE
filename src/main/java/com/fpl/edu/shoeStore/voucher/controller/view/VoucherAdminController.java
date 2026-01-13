package com.fpl.edu.shoeStore.voucher.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Voucher View Controller - Admin SSR
 */
@Controller
@RequestMapping("/admin/vouchers")
public class VoucherAdminController {

    @GetMapping
    public String index(Model model, jakarta.servlet.http.HttpServletRequest request) {
        model.addAttribute("pageTitle", "Quản lý Voucher");
        model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("pendingOrdersCount", 0);
        return "admin/pages/voucher-list";
    }
}
