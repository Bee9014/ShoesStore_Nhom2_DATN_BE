package com.fpl.edu.shoeStore.product.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fpl.edu.shoeStore.common.handler.PageResponse;
import com.fpl.edu.shoeStore.product.convert.ProductConverter;
import com.fpl.edu.shoeStore.product.dto.request.ProductDtoRequest;
import com.fpl.edu.shoeStore.product.dto.response.ProductDtoResponse;
import com.fpl.edu.shoeStore.product.entity.Product;
import com.fpl.edu.shoeStore.product.mapper.ProductMapper;
import com.fpl.edu.shoeStore.product.service.ProductService;
import com.fpl.edu.shoeStore.product.service.ProductVariantService; // 👈 1. Import Service con

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;
    private final ProductVariantService productVariantService; // 👈 2. Inject Service con

    // Hàm saveFile giữ nguyên
    private String saveFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        try {
            Path uploadDir = Paths.get("uploads");
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = uploadDir.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi lưu file ảnh: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public ProductDtoResponse createProduct(ProductDtoRequest request, MultipartFile file) {
        // 1. Tạo Product Entity (Cha)
        Product product = ProductConverter.toEntity(request);

        // 2. Xử lý ảnh
        String imagePath = saveFile(file);
        if (imagePath != null) {
            product.setDefaultImage(imagePath);
        }
        product.setCreateAt(LocalDateTime.now());
        product.setUpdateAt(LocalDateTime.now());
        
        // 3. Insert Product -> Có ID
        productMapper.insert(product);

        // 4. 👇 GỌI SERVICE CON ĐỂ TẠO VARIANTS (QUAN TRỌNG)
        // Kiểm tra xem request có gửi kèm danh sách variants không
        if (request.getVariants() != null && !request.getVariants().isEmpty()) {
            // Truyền ID vừa tạo của cha sang cho con
            productVariantService.createVariants(product.getProductId(), request.getVariants());
        }

        return ProductConverter.toResponse(product);
    }

    @Override
    @Transactional
    public ProductDtoResponse updateProduct(Integer id, ProductDtoRequest request, MultipartFile file) {
        Product existing = productMapper.findById(id);
        if (existing == null) {
            throw new RuntimeException("Không tìm thấy Product id = " + id);
        }

        if (request.getCategoryId() != null) existing.setCategoryId(request.getCategoryId());
        if (request.getTitle() != null) existing.setTitle(request.getTitle());
        if (request.getDescription() != null) existing.setDescription(request.getDescription());
        if (request.getBrand() != null) existing.setBrand(request.getBrand());
        if (request.getCondition() != null) existing.setCondition(request.getCondition());
        if (request.getStatus() != null) existing.setStatus(request.getStatus());
        if (request.getUpdateBy() != null) existing.setUpdateBy(request.getUpdateBy());
        
        // Chỉ update ảnh nếu người dùng chọn file mới
        String newImagePath = saveFile(file);
        if (newImagePath != null) {
            existing.setDefaultImage(newImagePath);
        }

        existing.setUpdateAt(LocalDateTime.now());
        productMapper.update(existing);

        // (Tùy chọn) Nếu muốn update cả variants trong cùng API này thì gọi variantService ở đây
        // Nhưng thường update variants sẽ làm ở API riêng hoặc logic phức tạp hơn.

        return ProductConverter.toResponse(existing);
    }

    @Override
    @Transactional
    public int deleteProduct(Integer id) {
        Product existing = productMapper.findById(id);
        if (existing == null) {
            throw new RuntimeException("Không tìm thấy Product để xóa");
        }
        // Lưu ý: Nếu DB không có ON DELETE CASCADE, bạn cần xóa variants trước:
        // productVariantService.deleteByProductId(id); (Cần thêm hàm này bên Service con nếu cần)
        
        return productMapper.deleteById(id);
    }

    // Các hàm findById, findByTitle, findAllPaged giữ nguyên
    @Override
    public ProductDtoResponse findById(Integer id) {
        Product product = productMapper.findById(id);
        return product == null ? null : ProductConverter.toResponse(product);
    }

    @Override
    public ProductDtoResponse findByTitle(String title) {
        Product product = productMapper.findByTitle(title);
        return product == null ? null : ProductConverter.toResponse(product);
    }

    @Override
    public PageResponse<ProductDtoResponse> findAllPaged(Integer categoryId, String title, String status, Boolean isActive, int page, int size) {
        int offset = (page - 1) * size;
        List<Product> products = productMapper.findAllPaged(categoryId, title, status, isActive, offset, size);
        long totalElements = productMapper.countAll(categoryId, title, status, isActive);
        
        List<ProductDtoResponse> content = products.stream()
                .map(ProductConverter::toResponse)
                .collect(Collectors.toList());
                
        int totalPages = (int) Math.ceil((double) totalElements / size);
        
        return PageResponse.<ProductDtoResponse>builder()
                .content(content)
                .pageNumber(page)
                .pageSize(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .build();
    }
}