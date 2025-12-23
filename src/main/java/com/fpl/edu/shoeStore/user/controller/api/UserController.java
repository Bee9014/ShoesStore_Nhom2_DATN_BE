package com.fpl.edu.shoeStore.user.controller.api;

import com.fpl.edu.shoeStore.common.handler.ApiResponse;
import com.fpl.edu.shoeStore.common.handler.PageResponse;
import com.fpl.edu.shoeStore.user.dto.request.UserDtoRequest;
import com.fpl.edu.shoeStore.user.dto.response.UserDtoResponse;
import com.fpl.edu.shoeStore.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Lấy danh sách users với phân trang và filter
     * GET /api/v1/users
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<UserDtoResponse>>> getAllUsers(
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) Integer roleId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            PageResponse<UserDtoResponse> pageResponse = userService.findAllPaged(
                    userId, username, fullName, email, phone,
                    roleId, status, page, size
            );

            return ResponseEntity.ok(ApiResponse.<PageResponse<UserDtoResponse>>builder()
                    .success(true)
                    .statusCode(HttpStatus.OK.value())
                    .message("Lấy danh sách người dùng thành công")
                    .data(pageResponse)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<PageResponse<UserDtoResponse>>builder()
                            .success(false)
                            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .message("Lỗi hệ thống: " + e.getMessage())
                            .data(null)
                            .build());
        }
    }

    /**
     * Lấy user theo ID
     * GET /api/v1/users/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDtoResponse>> getUserById(@PathVariable Integer id) {
        try {
            UserDtoResponse user = userService.findById(id);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.<UserDtoResponse>builder()
                                .success(false)
                                .statusCode(HttpStatus.NOT_FOUND.value())
                                .message("Không tìm thấy người dùng với ID: " + id)
                                .data(null)
                                .build());
            }

            return ResponseEntity.ok(ApiResponse.<UserDtoResponse>builder()
                    .success(true)
                    .statusCode(HttpStatus.OK.value())
                    .message("Lấy thông tin người dùng thành công")
                    .data(user)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<UserDtoResponse>builder()
                            .success(false)
                            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .message("Lỗi hệ thống: " + e.getMessage())
                            .data(null)
                            .build());
        }
    }

    /**
     * Tạo user mới
     * POST /api/v1/users
     */
    @PostMapping
    public ResponseEntity<ApiResponse<UserDtoResponse>> createUser(@RequestBody UserDtoRequest request) {
        try {
            UserDtoResponse created = userService.createUser(request);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.<UserDtoResponse>builder()
                            .success(true)
                            .statusCode(HttpStatus.CREATED.value())
                            .message("Tạo người dùng thành công")
                            .data(created)
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<UserDtoResponse>builder()
                            .success(false)
                            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .message("Lỗi tạo người dùng: " + e.getMessage())
                            .data(null)
                            .build());
        }
    }

    /**
     * Update user
     * PUT /api/v1/users/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDtoResponse>> updateUser(
            @PathVariable Integer id,
            @RequestBody UserDtoRequest request
    ) {
        try {
            UserDtoResponse updated = userService.updateUser(id, request);

            return ResponseEntity.ok(ApiResponse.<UserDtoResponse>builder()
                    .success(true)
                    .statusCode(HttpStatus.OK.value())
                    .message("Cập nhật người dùng thành công")
                    .data(updated)
                    .build());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.<UserDtoResponse>builder()
                            .success(false)
                            .statusCode(HttpStatus.NOT_FOUND.value())
                            .message(e.getMessage())
                            .data(null)
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<UserDtoResponse>builder()
                            .success(false)
                            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .message("Lỗi cập nhật người dùng: " + e.getMessage())
                            .data(null)
                            .build());
        }
    }

    /**
     * Toggle user status (active ⟷ blocked)
     * PUT /api/v1/users/{id}/status
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<UserDtoResponse>> toggleUserStatus(
            @PathVariable Integer id,
            @RequestBody(required = false) String newStatus
    ) {
        try {
            UserDtoResponse user = userService.findById(id);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.<UserDtoResponse>builder()
                                .success(false)
                                .statusCode(HttpStatus.NOT_FOUND.value())
                                .message("Không tìm thấy người dùng")
                                .data(null)
                                .build());
            }

            // Toggle status: active -> blocked, blocked -> active
            String currentStatus = user.getStatus();
            String targetStatus = "blocked".equals(currentStatus) ? "active" : "blocked";

            UserDtoRequest request = UserDtoRequest.builder()
                    .username(user.getUsername())
                    .fullName(user.getFullName())
                    .email(user.getEmail())
                    .phone(user.getPhone())
                    .roleId(user.getRoleId())
                    .status(targetStatus)
                    .build();

            UserDtoResponse updated = userService.updateUser(id, request);

            return ResponseEntity.ok(ApiResponse.<UserDtoResponse>builder()
                    .success(true)
                    .statusCode(HttpStatus.OK.value())
                    .message("Thay đổi trạng thái người dùng thành công")
                    .data(updated)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<UserDtoResponse>builder()
                            .success(false)
                            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .message("Lỗi thay đổi trạng thái: " + e.getMessage())
                            .data(null)
                            .build());
        }
    }

    /**
     * Xóa user
     * DELETE /api/v1/users/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Integer id) {
        try {
            userService.deleteUser(id);

            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .success(true)
                    .statusCode(HttpStatus.OK.value())
                    .message("Xóa người dùng thành công")
                    .data(null)
                    .build());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.<Void>builder()
                            .success(false)
                            .statusCode(HttpStatus.NOT_FOUND.value())
                            .message(e.getMessage())
                            .data(null)
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Void>builder()
                            .success(false)
                            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .message("Lỗi xóa người dùng: " + e.getMessage())
                            .data(null)
                            .build());
        }
    }
}
