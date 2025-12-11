package com.fpl.edu.shoeStore.admin.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping({"", "/"})
    public String dashboard(Model model, Authentication authentication, HttpServletRequest request) {
        // Redirect to login if not authenticated
        if (authentication == null || !authentication.isAuthenticated() 
            || authentication.getPrincipal().equals("anonymousUser")) {
            return "redirect:/admin/login";
        }
        
        model.addAttribute("pageTitle", "Dashboard");
        model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("pendingOrdersCount", 5); // TODO: Get from service
        
        return "admin/pages/dashboard";
    }

    @GetMapping("/products")
    public String productList(Model model, HttpServletRequest request) {
        model.addAttribute("pageTitle", "Quản lý sản phẩm");
        model.addAttribute("currentPath", request.getRequestURI());
        return "admin/pages/product";
    }

    @GetMapping("/products/add")
    public String productAdd(Model model) {
        model.addAttribute("pageTitle", "Thêm sản phẩm");
        model.addAttribute("currentPage", "product");
        return "admin/product/add";
    }

    @GetMapping("/products/edit/{id}")
    public String productEdit(@PathVariable Long id, Model model) {
        model.addAttribute("pageTitle", "Chỉnh sửa sản phẩm");
        model.addAttribute("currentPage", "product");
        // TODO: Load product by id
        return "admin/product/edit";
    }

    @GetMapping("/products/{id}")
    public String productDetail(@PathVariable Long id, Model model) {
        model.addAttribute("pageTitle", "Chi tiết sản phẩm");
        model.addAttribute("currentPage", "product");
        // TODO: Load product by id
        return "admin/product/detail";
    }

    @GetMapping("/orders")
    public String orderList(Model model, HttpServletRequest request) {
        model.addAttribute("pageTitle", "Quản lý đơn hàng");
        model.addAttribute("currentPath", request.getRequestURI());
        // TODO: Load orders from database
        return "admin/pages/order-list";
    }

    @GetMapping("/orders/{id}")
    public String orderDetail(@PathVariable Long id, Model model) {
        model.addAttribute("pageTitle", "Chi tiết đơn hàng");
        model.addAttribute("currentPage", "order");
        // TODO: Load order by id
        return "admin/order/detail";
    }

    @GetMapping("/users")
    public String userList(Model model, HttpServletRequest request) {
        model.addAttribute("pageTitle", "Quản lý người dùng");
        model.addAttribute("currentPath", request.getRequestURI());
        // TODO: Load users from database
        return "admin/pages/user-list";
    }

    @GetMapping("/promotions")
    public String promotionList(Model model) {
        model.addAttribute("pageTitle", "Quản lý khuyến mãi");
        model.addAttribute("currentPage", "promotion");
        // TODO: Load promotions from database
        return "admin/promotion/list";
    }

    @GetMapping("/shipments")
    public String shipmentList(Model model) {
        model.addAttribute("pageTitle", "Quản lý vận chuyển");
        model.addAttribute("currentPage", "shipment");
        // TODO: Load shipments from database
        return "admin/shipment/list";
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        model.addAttribute("pageTitle", "Tài khoản");
        return "admin/profile";
    }

    @GetMapping("/settings")
    public String settings(Model model) {
        model.addAttribute("pageTitle", "Cài đặt");
        return "admin/settings";
    }
}
