package com.fpl.edu.shoeStore.product.dto.response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
//import java.math.BigDecimal;
import java.util.List;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailDtoResponse {
    // Thông tin từ Product
    private Long productId;
    private Long categoryId;
    private String categoryName;        // Tên category (join từ bảng categories)
    private String name;
    private String slug;
    private String description;
    private String sku;                 // product_code trong DB
    private Double basePrice;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Danh sách các variant của product
    private List<ProductVariantDtoResponse> variants;
}