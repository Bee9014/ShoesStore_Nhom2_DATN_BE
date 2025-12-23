package com.fpl.edu.shoeStore.user.controller.view;

import com.fpl.edu.shoeStore.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * User View Controller - Admin SSR
 * Render Thymeleaf templates cho admin user management
 */
@Controller
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class UserViewController {

    private final UserService userService;

    /**
     * Trang danh sách users
     * GET /admin/users
     */
    @GetMapping({"", "/"})
    public String userListPage(Model model, HttpServletRequest request) {
        model.addAttribute("pageTitle", "Quản lý người dùng");
        model.addAttribute("currentPath", request.getRequestURI());
        
        return "admin/pages/user-list";
    }

    /**
     * Trang chi tiết user (Optional)
     * GET /admin/users/{id}
     */
    @GetMapping("/{id}")
    public String userDetailPage(
            @PathVariable Integer id,
            Model model,
            HttpServletRequest request
    ) {
        model.addAttribute("pageTitle", "Chi tiết người dùng");
        model.addAttribute("currentPage", "user");
        model.addAttribute("userId", id);
        
        return "admin/pages/user-detail";
    }
}
