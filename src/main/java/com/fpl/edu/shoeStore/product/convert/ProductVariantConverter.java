 package com.fpl.edu.shoeStore.product.convert;

     import com.fpl.edu.shoeStore.product.entity.ProductVariant;
     import com.fpl.edu.shoeStore.product.dto.request.ProductVariantDtoRequest;
     import com.fpl.edu.shoeStore.product.dto.response.ProductVariantDtoResponse;

     public class ProductVariantConverter {

         public static ProductVariant toEntity(ProductVariantDtoRequest dto) {
             return ProductVariant.builder()
                     .productId(dto.getProductId())
                     .skuCode(dto.getSkuCode())              // Đổi từ productVariantCode → skuCode
                     .price(dto.getPrice())
                     .qtyAvailable(dto.getQtyAvailable())    // Đổi từ stockQty → qtyAvailable
                     .weightGrams(dto.getWeightGrams())      // THÊM MỚI
                     .attribute(dto.getAttribute())          // THÊM MỚI
                     .image(dto.getImage())                  // THÊM MỚI
                     .createBy(dto.getCreateBy())            // THÊM MỚI
                     .updateBy(dto.getUpdateBy())            // THÊM MỚI
                     .build();
         }

         public static ProductVariantDtoResponse toResponse(ProductVariant entity) {
             return ProductVariantDtoResponse.builder()
                     .variantId(entity.getVariantId())
                     .productId(entity.getProductId())
                     .skuCode(entity.getSkuCode())           // Đổi từ productVariantCode → skuCode
                     .price(entity.getPrice())
                     .qtyAvailable(entity.getQtyAvailable()) // Đổi từ stockQty → qtyAvailable
                     .weightGrams(entity.getWeightGrams())   // THÊM MỚI
                     .attribute(entity.getAttribute())       // THÊM MỚI
                     .image(entity.getImage())               // THÊM MỚI
                     .createAt(entity.getCreateAt())         // Đổi từ createdAt
                     .updateAt(entity.getUpdateAt())         // Đổi từ updatedAt
                     .createBy(entity.getCreateBy())         // THÊM MỚI
                     .updateBy(entity.getUpdateBy())         // THÊM MỚI
                     .build();
         }
     }
