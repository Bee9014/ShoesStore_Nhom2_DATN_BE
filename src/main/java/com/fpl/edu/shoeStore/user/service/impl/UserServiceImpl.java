package com.fpl.edu.shoeStore.user.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fpl.edu.shoeStore.common.handler.PageResponse;
import com.fpl.edu.shoeStore.user.convert.UserConverter;
import com.fpl.edu.shoeStore.user.dto.request.UserDtoRequest;
import com.fpl.edu.shoeStore.user.dto.response.UserDtoResponse;
import com.fpl.edu.shoeStore.user.entity.User;
import com.fpl.edu.shoeStore.user.mapper.UserMapper;
import com.fpl.edu.shoeStore.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    // Nếu bạn chưa cấu hình BCryptPasswordEncoder bean thì có thể comment dòng này lại
    private final PasswordEncoder passwordEncoder; 

    @Override
    @Transactional
    public UserDtoResponse createUser(UserDtoRequest request) {
        // 1. Convert DTO sang Entity
        User user = UserConverter.toEntity(request);

        // 2. Set các giá trị mặc định
        if (user.getStatus() == null) {
            user.setStatus("active");
        }
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        // 3. Xử lý mật khẩu (Tạm thời để nguyên, sau này bạn mở comment để mã hóa)
        String rawPassword = request.getPasswordHash();
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        
        // Lưu ý: Do Converter map "password" -> "passwordHash" rồi, nên ở đây user đã có pass.

        // 4. Insert vào DB
        userMapper.insert(user);

        // 5. Trả về kết quả (Lúc này user đã có ID do DB sinh ra)
        return UserConverter.toDto(user);
    }

    @Override
    @Transactional
    public UserDtoResponse updateUser(Integer id, UserDtoRequest request) {
        // 1. Kiểm tra tồn tại
        User existingUser = userMapper.findById(id);
        if (existingUser == null) {
            throw new RuntimeException("Không tìm thấy User có ID: " + id);
        }

        // 2. Update từng trường (Chỉ update nếu request có gửi lên)
        if (request.getUsername() != null) existingUser.setUsername(request.getUsername());
        if (request.getFullName() != null) existingUser.setFullName(request.getFullName());
        if (request.getEmail() != null) existingUser.setEmail(request.getEmail());
        if (request.getPhone() != null) existingUser.setPhone(request.getPhone());
        if (request.getStatus() != null) existingUser.setStatus(request.getStatus());
        if (request.getRoleId() != 0) existingUser.setRoleId(request.getRoleId());

        // Xử lý password khi update (Nếu có gửi pass mới thì mới đổi)
        if (request.getPasswordHash() != null && !request.getPasswordHash().isBlank()) {
         String rawPassword = request.getPasswordHash();
         String encodedPassword = passwordEncoder.encode(rawPassword);
         existingUser.setPasswordHash(encodedPassword);
     }

        existingUser.setUpdatedAt(LocalDateTime.now());

        // 3. Lưu xuống DB
        userMapper.update(existingUser);

        // 4. Trả về dữ liệu mới nhất
        return UserConverter.toDto(existingUser);
    }

    @Override
    @Transactional
    public int deleteUser(Integer id) {
        User existingUser = userMapper.findById(id);
        if (existingUser == null) {
            throw new RuntimeException("Không tìm thấy User để xóa");
        }
        // Xóa cứng
        return userMapper.deleteById(id);
    }

    @Override
    public UserDtoResponse findById(Integer id) {
        User user = userMapper.findById(id);
        if (user == null) {
            return null;
        }
        return UserConverter.toDto(user);
    }

    @Override
    public UserDtoResponse findByPhone(String phone) {
        User user = userMapper.findByPhone(phone);
        return UserConverter.toDto(user);
    }

    @Override
    public UserDtoResponse findByUsername(String username) {
        User user = userMapper.findByUsername(username);
        return UserConverter.toDto(user);
    }

    @Override
    public PageResponse<UserDtoResponse> findAllPaged(
            Integer userId,
            String username,
            String fullName,
            String email,
            String phone,
            Integer roleId,
            String status,
            int page,
            int size
    ) {
        // 1. Tính toán offset
        int offset = (page - 1) * size;

        // 2. Gọi Mapper
        // Lưu ý: Mapper này phải khớp với file UserMapper.java bạn vừa sửa
        List<User> users = userMapper.findAllPaged(
                userId, username, fullName, email, phone, roleId, status, offset, size
        );

        long totalElements = userMapper.countAll(
                userId, username, fullName, email, phone, roleId, status
        );

        // 3. Convert sang DTO List
        List<UserDtoResponse> userDtos = UserConverter.toDtoList(users);

        // 4. Tính tổng số trang
        int totalPages = (int) Math.ceil((double) totalElements / size);

        // 5. Đóng gói PageResponse
        return PageResponse.<UserDtoResponse>builder()
                .content(userDtos)
                .pageNumber(page)
                .pageSize(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .build();
    }
}