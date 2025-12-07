package com.fpl.edu.shoeStore.product.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fpl.edu.shoeStore.common.PageResponse;
import com.fpl.edu.shoeStore.product.convert.ProductConverter;
import com.fpl.edu.shoeStore.product.dto.request.ProductDtoRequest;
import com.fpl.edu.shoeStore.product.dto.response.ProductDtoResponse;
import com.fpl.edu.shoeStore.product.entity.Product;
import com.fpl.edu.shoeStore.product.mapper.ProductMapper;
import com.fpl.edu.shoeStore.product.service.ProductService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;
    private final ProductConverter productConverter;

    @Override
    @Transactional
    public ProductDtoResponse createProduct(ProductDtoRequest request) {
        Product product = productConverter.toEntity(request);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        productMapper.insert(product);
        return productConverter.toResponse(product);
    }

    @Override
    @Transactional
    public ProductDtoResponse updateProduct(Long id, ProductDtoRequest request) {
        Product existing = productMapper.findById(id);
        if (existing == null) {
            throw new RuntimeException("Không tìm thấy Product id = " + id);
        }

        if (request.getCategoryId() != null) existing.setCategoryId(request.getCategoryId());
        if (request.getName() != null) existing.setName(request.getName());
        if (request.getSlug() != null) existing.setSlug(request.getSlug());
        if (request.getDescription() != null) existing.setDescription(request.getDescription());
        if (request.getSku() != null) existing.setSku(request.getSku());
        if (request.getBasePrice() != null) existing.setBasePrice(request.getBasePrice());
        if (request.getIsActive() != null) existing.setIsActive(request.getIsActive());

        existing.setUpdatedAt(LocalDateTime.now());
        productMapper.update(existing);

        return productConverter.toResponse(existing);
    }

    @Override
    @Transactional
    public int deleteProduct(Long id) {
        Product existing = productMapper.findById(id);
        if (existing == null) {
            throw new RuntimeException("Không tìm thấy Product để xóa");
        }
        return productMapper.deleteById(id);
    }

    @Override
    public ProductDtoResponse findById(Long id) {
        Product product = productMapper.findById(id);
        return product == null ? null : productConverter.toResponse(product);
    }

    @Override
    public PageResponse<ProductDtoResponse> findAllPaged(
            Long categoryId,
            String name,
            String slug,
            Boolean isActive,
            int page,
            int size
    ) {
        int offset = (page - 1) * size;

        List<Product> products = productMapper.findAllPaged(
                categoryId, name, slug, isActive, offset, size
        );

        long totalElements = productMapper.countAll(
                categoryId, name, slug, isActive
        );

        List<ProductDtoResponse> content = products.stream()
                .map(productConverter::toResponse)
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
