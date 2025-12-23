package com.fpl.edu.shoeStore.order.controller.view;

import com.fpl.edu.shoeStore.order.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Order View Controller - Quản lý hiển thị trang admin
 * Pattern: Tách riêng View và API Controller như IoT project
 */
@Controller
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class OrderViewController {

    private final OrderService orderService;

    /**
     * Hiển thị trang danh sách đơn hàng
     * Path: /admin/orders
     */
    @GetMapping({"", "/"})
    public String orderListPage(Model model, HttpServletRequest request) {
        model.addAttribute("pageTitle", "Quản lý đơn hàng");
        model.addAttribute("currentPath", request.getRequestURI());
        
        // Get pending orders count for sidebar badge
        try {
            long pendingCount = orderService.countOrdersByStatus("PENDING");
            model.addAttribute("pendingOrdersCount", pendingCount);
        } catch (Exception e) {
            model.addAttribute("pendingOrdersCount", 0);
        }
        
        return "admin/pages/order-list";
    }

    /**
     * Hiển thị trang chi tiết đơn hàng
     * Path: /admin/orders/{id}
     */
    @GetMapping("/{id}")
    public String orderDetailPage(@PathVariable Long id, Model model, HttpServletRequest request) {
        model.addAttribute("pageTitle", "Chi tiết đơn hàng #" + id);
        model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("orderId", id);
        
        // Get pending orders count for sidebar badge
        try {
            long pendingCount = orderService.countOrdersByStatus("PENDING");
            model.addAttribute("pendingOrdersCount", pendingCount);
        } catch (Exception e) {
            model.addAttribute("pendingOrdersCount", 0);
        }
        
        return "admin/pages/order-detail";
    }
}
