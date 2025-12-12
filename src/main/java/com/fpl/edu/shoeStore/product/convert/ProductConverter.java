 package com.fpl.edu.shoeStore.product.convert;

     import com.fpl.edu.shoeStore.product.dto.request.ProductDtoRequest;
     import com.fpl.edu.shoeStore.product.dto.response.ProductDetailDtoResponse;
     import com.fpl.edu.shoeStore.product.dto.response.ProductDtoResponse;
     import com.fpl.edu.shoeStore.product.entity.Product;
     import com.fpl.edu.shoeStore.product.entity.ProductVariant;

     import java.util.List;
     import java.util.stream.Collectors;

     public class ProductConverter {

         public static Product toEntity(ProductDtoRequest dto) {
             return Product.builder()
                     .categoryId(dto.getCategoryId())
                     .title(dto.getTitle())                  // Đổi từ name → title
                     .description(dto.getDescription())
                     .brand(dto.getBrand())                  // THÊM MỚI
                     .condition(dto.getCondition())          // THÊM MỚI
                     .defaultImage(dto.getDefaultImage())    // THÊM MỚI
                     .status(dto.getStatus())                // Đổi từ isActive → status
                     .createBy(dto.getCreateBy())            // THÊM MỚI
                     .updateBy(dto.getUpdateBy())            // THÊM MỚI
                     .build();
         }

         public static ProductDtoResponse toResponse(Product entity) {
             return ProductDtoResponse.builder()
                     .productId(entity.getProductId())
                     .categoryId(entity.getCategoryId())
                     .title(entity.getTitle())               // Đổi từ name → title
                     .description(entity.getDescription())
                     .brand(entity.getBrand())               // THÊM MỚI
                     .condition(entity.getCondition())       // THÊM MỚI
                     .defaultImage(entity.getDefaultImage()) // THÊM MỚI
                     .status(entity.getStatus())             // Đổi từ isActive → status
                     .createAt(entity.getCreateAt())         // Đổi từ createdAt
                     .updateAt(entity.getUpdateAt())         // Đổi từ updatedAt
                     .createBy(entity.getCreateBy())         // THÊM MỚI
                     .updateBy(entity.getUpdateBy())         // THÊM MỚI
                     .build();
         }

         public static ProductDetailDtoResponse toDetailResponse(Product product, List<ProductVariant> variants) {
             return ProductDetailDtoResponse.builder()
                     .productId(product.getProductId())
                     .categoryId(product.getCategoryId())
                     .title(product.getTitle())              // Đổi từ name → title
                     .description(product.getDescription())
                     .brand(product.getBrand())              // THÊM MỚI
                     .condition(product.getCondition())      // THÊM MỚI
                     .defaultImage(product.getDefaultImage())// THÊM MỚI
                     .status(product.getStatus())            // Đổi từ isActive → status
                     .createAt(product.getCreateAt())        // Đổi từ createdAt
                     .updateAt(product.getUpdateAt())        // Đổi từ updatedAt
                     .createBy(product.getCreateBy())        // THÊM MỚI
                     .updateBy(product.getUpdateBy())        // THÊM MỚI
                     .variants(variants.stream()
                             .map(ProductVariantConverter::toResponse)
                             .collect(Collectors.toList()))
                     .build();
         }
     }