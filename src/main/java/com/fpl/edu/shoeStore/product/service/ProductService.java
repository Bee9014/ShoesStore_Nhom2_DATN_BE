package com.fpl.edu.shoeStore.product.service;

import com.fpl.edu.shoeStore.common.PageResponse;
import com.fpl.edu.shoeStore.product.dto.request.ProductDtoRequest;
import com.fpl.edu.shoeStore.product.dto.response.ProductDtoResponse;

public interface ProductService {

    ProductDtoResponse createProduct(ProductDtoRequest request);

    ProductDtoResponse updateProduct(Long id, ProductDtoRequest request);

    int deleteProduct(Long id);

    ProductDtoResponse findById(Long id);

    PageResponse<ProductDtoResponse> findAllPaged(
            Long categoryId,
            String name,
            String slug,
            Boolean isActive,
            int page,
            int size
    );
}
