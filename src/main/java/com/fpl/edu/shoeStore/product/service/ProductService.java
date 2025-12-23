package com.fpl.edu.shoeStore.product.service;

import org.springframework.web.multipart.MultipartFile;

     import com.fpl.edu.shoeStore.common.handler.PageResponse;
     import com.fpl.edu.shoeStore.product.dto.request.ProductDtoRequest;
     import com.fpl.edu.shoeStore.product.dto.response.ProductDtoResponse;

     public interface ProductService {
         ProductDtoResponse createProduct(ProductDtoRequest request,  MultipartFile file);

         ProductDtoResponse updateProduct(Integer id, ProductDtoRequest request, MultipartFile file);

         int deleteProduct(Integer id);

         ProductDtoResponse findById(Integer id);

         ProductDtoResponse findByTitle(String title);                      // Đổi từ findByName

         PageResponse<ProductDtoResponse> findAllPaged(
             Integer categoryId,
             String title,
             String status,
             Boolean isActive,
             int page,
             int size
         );
     }
