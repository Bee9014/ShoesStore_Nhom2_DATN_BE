 package com.fpl.edu.shoeStore.product.dto.response;

     import lombok.AllArgsConstructor;
     import lombok.Builder;
     import lombok.Data;
     import lombok.NoArgsConstructor;

     import java.time.LocalDate;
     import java.util.List;

     @Data
     @NoArgsConstructor
     @AllArgsConstructor
     @Builder
     public class ProductDetailDtoResponse {
         private Integer productId;
         private Integer categoryId;
         private String title;
         private String url;
         private String productCode;
         private String description;
         private String brand;
         private String condition;
         private String defaultImage;
         private String status;
         private LocalDate createAt;
         private LocalDate updateAt;
         private Integer createBy;
         private Integer updateBy;
         private List<ProductVariantDtoResponse> variants;
     }