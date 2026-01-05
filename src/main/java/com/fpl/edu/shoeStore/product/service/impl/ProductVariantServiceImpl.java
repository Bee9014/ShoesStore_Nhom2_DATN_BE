package com.fpl.edu.shoeStore.product.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fpl.edu.shoeStore.product.convert.ProductVariantConverter;
import com.fpl.edu.shoeStore.product.convert.StockHistoryConverter; // Import Converter
import com.fpl.edu.shoeStore.product.dto.request.ProductVariantDtoRequest;
import com.fpl.edu.shoeStore.product.dto.response.ProductVariantDtoResponse;
import com.fpl.edu.shoeStore.product.dto.response.StockHistoryDtoResponse; // Import DTO
import com.fpl.edu.shoeStore.product.entity.ProductVariant;
import com.fpl.edu.shoeStore.product.entity.StockHistory; // Import Entity History
import com.fpl.edu.shoeStore.product.mapper.ProductVariantMapper;
import com.fpl.edu.shoeStore.product.mapper.StockHistoryMapper; // Import Mapper History
import com.fpl.edu.shoeStore.product.service.ProductVariantService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductVariantServiceImpl implements ProductVariantService {

    private final ProductVariantMapper productVariantMapper;
    private final StockHistoryMapper stockHistoryMapper; // 👈 1. Inject Mapper Lịch sử

    // --- Hàm hỗ trợ lưu file ảnh ---
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
            throw new RuntimeException("Lỗi khi lưu file ảnh biến thể: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    // 👇 Cập nhật hàm này nhận thêm MultipartFile
    public ProductVariantDtoResponse createVariant(ProductVariantDtoRequest request, MultipartFile file) {
        ProductVariant variant = ProductVariantConverter.toEntity(request);
        
        // 1. Lưu ảnh nếu có
        String imagePath = saveFile(file);
        if (imagePath != null) {
            variant.setImage(imagePath); // Giả sử Entity đã có trường image
        }

        // 2. Logic Active
        if (variant.getIsActive() == null) {
            variant.setIsActive(true);
        }
        
        // 3. Logic SKU tự động
        if (variant.getProductVariantCode() == null || variant.getProductVariantCode().trim().isEmpty()) {
             variant.setProductVariantCode("PVC-" + System.currentTimeMillis());
        }

        productVariantMapper.insert(variant);
        return ProductVariantConverter.toResponse(variant);
    }

    @Override
    @Transactional
    public void createVariants(Integer productId, List<ProductVariantDtoRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            return;
        }

        for (ProductVariantDtoRequest req : requests) {
            ProductVariant variant = ProductVariantConverter.toEntity(req);
            variant.setProductId(productId);

            if (variant.getProductVariantCode() == null || variant.getProductVariantCode().trim().isEmpty()) {
                String autoCode = "PVC-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
                variant.setProductVariantCode(autoCode);
            }
            
            if (variant.getIsActive() == null) variant.setIsActive(true);
            if (variant.getCreateBy() == null) variant.setCreateBy(1);
            if (variant.getUpdateBy() == null) variant.setUpdateBy(1);

            productVariantMapper.insert(variant);
        }
    }

    @Override
    @Transactional
    public ProductVariantDtoResponse updateVariant(Integer variantId, ProductVariantDtoRequest request) {
        ProductVariant existingVariant = productVariantMapper.findById(variantId);
        if (existingVariant == null) {
            throw new RuntimeException("Product variant not found with id: " + variantId);
        }

        ProductVariant variant = ProductVariantConverter.toEntity(request);
        variant.setVariantId(variantId);
        variant.setProductId(existingVariant.getProductId()); 

        if (variant.getProductVariantCode() == null || variant.getProductVariantCode().isEmpty()) {
            variant.setProductVariantCode(existingVariant.getProductVariantCode());
        }

        if (variant.getIsActive() == null) {
            variant.setIsActive(existingVariant.getIsActive());
        }

        if (variant.getUpdateBy() == null) variant.setUpdateBy(1);

        productVariantMapper.update(variant);

        return ProductVariantConverter.toResponse(productVariantMapper.findById(variantId));
    }

    @Override
    @Transactional
    public void updateStock(Integer variantId, Integer quantity) {
        // 1. Lấy thông tin hiện tại
        ProductVariant existingVariant = productVariantMapper.findById(variantId);
        if (existingVariant == null) {
            throw new RuntimeException("Biến thể không tồn tại ID: " + variantId);
        }

        // 2. Tính toán tồn kho mới
        int currentStock = existingVariant.getStockQty() != null ? existingVariant.getStockQty() : 0;
        int newStock = currentStock + quantity;

        // 3. Kiểm tra logic: Không cho phép âm
        if (newStock < 0) {
            throw new RuntimeException("Kho không đủ hàng! Tồn hiện tại: " + currentStock + ", Yêu cầu trừ: " + Math.abs(quantity));
        }

        // 4. Update kho hàng
        productVariantMapper.updateStock(variantId, quantity);

        // 5. 👇 GHI LỊCH SỬ (QUAN TRỌNG) 👇
        StockHistory history = StockHistory.builder()
                .variantId(variantId)
                .amount(quantity)
                .stockBefore(currentStock)
                .stockAfter(newStock)
                .note(quantity > 0 ? "Nhập hàng / Cân bằng kho" : "Xuất hàng / Khách mua")
                .createBy(1) // Admin ID
                .build();

        stockHistoryMapper.insert(history);
        // 👆 HẾT PHẦN GHI LỊCH SỬ 👆
    }

    // --- Hàm mới: Xem lịch sử ---
    @Override
    public List<StockHistoryDtoResponse> getStockHistory(Integer variantId) {
        List<StockHistory> histories = stockHistoryMapper.findByVariantId(variantId);
        return histories.stream()
                .map(StockHistoryConverter::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteVariant(Integer variantId) {
        ProductVariant existingVariant = productVariantMapper.findById(variantId);
        if (existingVariant == null) throw new RuntimeException("Product variant not found");
        productVariantMapper.deleteById(variantId);
    }

    @Override
    public ProductVariantDtoResponse getVariantById(Integer variantId) {
        ProductVariant variant = productVariantMapper.findById(variantId);
        if (variant == null) throw new RuntimeException("Product variant not found");
        return ProductVariantConverter.toResponse(variant);
    }

    @Override
    public List<ProductVariantDtoResponse> getVariantsByProductId(Integer productId) {
        List<ProductVariant> variants = productVariantMapper.findByProductId(productId);
        return variants.stream().map(ProductVariantConverter::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<ProductVariantDtoResponse> getAllVariants() {
        List<ProductVariant> variants = productVariantMapper.findAll();
        return variants.stream().map(ProductVariantConverter::toResponse).collect(Collectors.toList());
    }

    @Override
    public ProductVariantDtoResponse getVariantByCode(String productVariantCode) {
        ProductVariant variant = productVariantMapper.findByCode(productVariantCode);
        if (variant == null) throw new RuntimeException("Product variant not found");
        return ProductVariantConverter.toResponse(variant);
    }
}