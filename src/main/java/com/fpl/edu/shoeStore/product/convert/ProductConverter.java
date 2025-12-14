 package com.fpl.edu.shoeStore.product.convert;

     import java.util.List;
     import java.util.stream.Collectors;

     import com.fpl.edu.shoeStore.product.dto.request.ProductDtoRequest;
     import com.fpl.edu.shoeStore.product.dto.response.ProductDetailDtoResponse;
     import com.fpl.edu.shoeStore.product.dto.response.ProductDtoResponse;
     import com.fpl.edu.shoeStore.product.entity.Product;
     import com.fpl.edu.shoeStore.product.entity.ProductVariant;

     public class ProductConverter {

         public static Product toEntity(ProductDtoRequest dto) {
             return Product.builder()
                     .categoryId(dto.getCategoryId())
                     .title(dto.getTitle())
                     .url(dto.getUrl())
                     .productCode(dto.getProductCode())
                     .description(dto.getDescription())
                     .brand(dto.getBrand())
                     .condition(dto.getCondition())
                     .defaultImage(dto.getDefaultImage())
                     .status(dto.getStatus())
                     .createBy(dto.getCreateBy())
                     .updateBy(dto.getUpdateBy())
                     .build();
         }

         public static ProductDtoResponse toResponse(Product entity) {
             return ProductDtoResponse.builder()
                     .productId(entity.getProductId())
                     .categoryId(entity.getCategoryId())
                     .title(entity.getTitle())
                     .url(entity.getUrl())
                     .productCode(entity.getProductCode())
                     .description(entity.getDescription())
                     .brand(entity.getBrand())
                     .condition(entity.getCondition())
                     .defaultImage(entity.getDefaultImage())
                     .status(entity.getStatus())
                     .createAt(entity.getCreateAt())
                     .updateAt(entity.getUpdateAt())
                     .createBy(entity.getCreateBy())
                     .updateBy(entity.getUpdateBy())
                     .build();
         }

         public static ProductDetailDtoResponse toDetailResponse(Product product, List<ProductVariant> variants) {
             return ProductDetailDtoResponse.builder()
                     .productId(product.getProductId())
                     .categoryId(product.getCategoryId())
                     .title(product.getTitle())
                     .url(product.getUrl())
                     .productCode(product.getProductCode())
                     .description(product.getDescription())
                     .brand(product.getBrand())
                     .condition(product.getCondition())
                     .defaultImage(product.getDefaultImage())
                     .status(product.getStatus())
                     .createAt(product.getCreateAt())
                     .updateAt(product.getUpdateAt())
                     .createBy(product.getCreateBy())
                     .updateBy(product.getUpdateBy())
                     .variants(variants.stream()
                             .map(ProductVariantConverter::toResponse)
                             .collect(Collectors.toList()))
                     .build();
         }
     }