package com.fpl.edu.shoeStore.product.service;

     import com.fpl.edu.shoeStore.common.handler.PageResponse;
     import com.fpl.edu.shoeStore.product.dto.request.ProductDtoRequest;
     import com.fpl.edu.shoeStore.product.dto.response.ProductDtoResponse;

     public interface ProductService {
         ProductDtoResponse createProduct(ProductDtoRequest request);

         ProductDtoResponse updateProduct(Integer id, ProductDtoRequest request);

         int deleteProduct(Integer id);

         ProductDtoResponse findById(Integer id);

         ProductDtoResponse findByTitle(String title);                      // Đổi từ findByName

         PageResponse<ProductDtoResponse> findAllPaged(
             Integer categoryId,                                            // Đổi Long → Integer
             String title,                                                  // Đổi từ name → title
             String status,                                                 // Đổi từ Boolean isActive → String status
             int page,
             int size
         );
     }
