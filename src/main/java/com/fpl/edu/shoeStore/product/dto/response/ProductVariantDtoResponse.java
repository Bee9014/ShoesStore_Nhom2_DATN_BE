 package com.fpl.edu.shoeStore.product.dto.response;

     import java.time.LocalDate;
     import lombok.AllArgsConstructor;
     import lombok.Builder;
     import lombok.Data;
     import lombok.NoArgsConstructor;

     @Data
     @NoArgsConstructor
     @AllArgsConstructor
     @Builder
     public class ProductVariantDtoResponse {
         private Integer variantId;
         private Integer productId;
         private String productVariantCode;
         private Double price;
         private Integer stockQty;
         private Integer weightGrams;
         private String attribute;
         private String image;
         private LocalDate createAt;
         private LocalDate updateAt;
         private Integer createBy;
         private Integer updateBy;
     }