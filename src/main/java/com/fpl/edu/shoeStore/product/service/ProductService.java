package com.fpl.edu.shoeStore.product.service;

import com.fpl.edu.shoeStore.common.handler.PageResponse;
import com.fpl.edu.shoeStore.product.dto.request.ProductDtoRequest;
import com.fpl.edu.shoeStore.product.dto.response.ProductDtoResponse;

/**
 * Product Service Interface
 */
public interface ProductService {

    /**
     * Create new product
     */
    ProductDtoResponse createProduct(ProductDtoRequest request);

    /**
     * Update product by ID
     */
    ProductDtoResponse updateProduct(Long id, ProductDtoRequest request);

    /**
     * Delete product by ID (soft delete)
     */
    int deleteProduct(Long id);

    /**
     * Find product by ID
     */
    ProductDtoResponse findById(Long id);

    /**
     * Find product by name (exact match)
     */
    ProductDtoResponse findByName(String name);

    /**
     * Find all products with pagination and filters
     * @param url - Updated: was slug
     */
    PageResponse<ProductDtoResponse> findAllPaged(
            Long categoryId,
            String name,
            String url,              // Updated: slug → url
            Boolean isActive,
            int page,
            int size
    );
}
