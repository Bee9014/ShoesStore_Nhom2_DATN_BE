package com.fpl.edu.shoeStore.product.dto.request;

     import lombok.AllArgsConstructor;
     import lombok.Builder;
     import lombok.Data;
     import lombok.NoArgsConstructor;

     import java.math.BigDecimal;
     import java.util.List;

     @Data
     @NoArgsConstructor
     @AllArgsConstructor
     @Builder
     public class ProductDtoRequest {
         private Integer categoryId;
         private String title;
         private String url;
         private String productCode;
         private String description;
         private BigDecimal basePrice;
         private Boolean isActive;
         private String brand;
         private String condition;
         private String defaultImage;
         private String status;
         private Integer createBy;
         private Integer updateBy;

         private List<ProductVariantDtoRequest> variants;
     }