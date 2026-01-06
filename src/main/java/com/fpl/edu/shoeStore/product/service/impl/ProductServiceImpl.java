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
import com.fpl.edu.shoeStore.product.service.ProductVariantService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;
    private final ProductVariantService productVariantService;

    // --- HELPER: LƯU FILE ---
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

    // --- CREATE PRODUCT ---
    @Override
    @Transactional
    public ProductDtoResponse createProduct(ProductDtoRequest request, MultipartFile file) {
        Product product = ProductConverter.toEntity(request);
        String imagePath = saveFile(file);
        if (imagePath != null) {
            product.setDefaultImage(imagePath);
        }
        if (product.getIsActive() == null) {
            product.setIsActive(true); 
        }
        product.setCreateAt(LocalDateTime.now());
        product.setUpdateAt(LocalDateTime.now());
        
        productMapper.insert(product);

        if (request.getVariants() != null && !request.getVariants().isEmpty()) {
            productVariantService.createVariants(product.getProductId(), request.getVariants());
        }

        return ProductConverter.toResponse(product);
    }

    // --- UPDATE PRODUCT ---
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
        if (request.getIsActive() != null) existing.setIsActive(request.getIsActive());
        if (request.getBrand() != null) existing.setBrand(request.getBrand());
        if (request.getCondition() != null) existing.setCondition(request.getCondition());
        if (request.getStatus() != null) existing.setStatus(request.getStatus());
        if (request.getUpdateBy() != null) existing.setUpdateBy(request.getUpdateBy());
        
        String newImagePath = saveFile(file);
        if (newImagePath != null) {
            existing.setDefaultImage(newImagePath);
        }

        existing.setUpdateAt(LocalDateTime.now());
        productMapper.update(existing);

        return ProductConverter.toResponse(existing);
    }

    // --- DELETE PRODUCT ---
    @Override
    @Transactional
    public int deleteProduct(Integer id) {
        Product existing = productMapper.findById(id);
        if (existing == null) {
            throw new RuntimeException("Không tìm thấy Product để xóa");
        }
        return productMapper.deleteById(id);
    }

    // --- FIND BY ID/TITLE ---
    @Override
    public ProductDtoResponse findById(Integer id) {
        productMapper.incrementViewCount(id);
        Product product = productMapper.findById(id);
        return product == null ? null : ProductConverter.toResponse(product);
    }

    @Override
    public ProductDtoResponse findByTitle(String title) {
        Product product = productMapper.findByTitle(title);
        return product == null ? null : ProductConverter.toResponse(product);
    }

    // --- 1. FIND ALL PAGED (FIXED OFFSET ERROR) ---
    @Override
    public PageResponse<ProductDtoResponse> findAllPaged(Integer categoryId, String title, String status, Boolean isActive, int page, int size) {
        // Đảm bảo số trang tối thiểu là 1
        page = Math.max(page, 1);
        size = Math.max(size, 1);
        
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

    @Override
    public List<ProductDtoResponse> getFeaturedProducts() {
        List<Product> products = productMapper.findTopFeatured();
        return products.stream()
                .map(ProductConverter::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDtoResponse> getBestSellers() {
        List<Product> bestSellers = productMapper.findBestSellers();
        return bestSellers.stream()
                .map(ProductConverter::toResponse)
                .collect(Collectors.toList());
    }

    // --- 2. BEST SELLERS PAGED (FIXED OFFSET ERROR) ---
    @Override
    public PageResponse<ProductDtoResponse> getBestSellersPaged(int page, int size) {
        page = Math.max(page, 1);
        size = Math.max(size, 1);
        
        if (size > 50) size = 50; // Giới hạn tối đa 50 theo yêu cầu
        
        int offset = (page - 1) * size;
        
        List<Product> products = productMapper.findBestFiftySellers(size, offset);
        List<ProductDtoResponse> productDtos = products.stream()
                .map(ProductConverter::toResponse)
                .collect(Collectors.toList());
        
        long totalElements = productMapper.countBestSellers();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        
        return PageResponse.<ProductDtoResponse>builder()
                .content(productDtos)
                .pageNumber(page)
                .pageSize(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .build();
    }

    // --- 3. SEARCH PRODUCTS (FIXED OFFSET ERROR) ---
    @Override
    public PageResponse<ProductDtoResponse> searchProducts(String keyword, int page, int size) {
        page = Math.max(page, 1);
        size = Math.max(size, 1);
        
        int offset = (page - 1) * size;
        
        List<Product> products = productMapper.findProductsBySearch(keyword, size, offset);
        List<ProductDtoResponse> productDtos = products.stream()
                .map(ProductConverter::toResponse)
                .collect(Collectors.toList());
        
        long totalElements = productMapper.countSearchResults(keyword);
        int totalPages = (int) Math.ceil((double) totalElements / size);
        
        return PageResponse.<ProductDtoResponse>builder()
                .content(productDtos)
                .pageNumber(page)
                .pageSize(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .build();
    }
}