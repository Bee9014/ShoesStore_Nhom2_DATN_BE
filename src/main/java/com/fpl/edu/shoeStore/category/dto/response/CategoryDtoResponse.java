package com.fpl.edu.shoeStore.category.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDtoResponse {
    private Integer categoryId;
    private Integer parentId;
    private String parentName;      // Tên danh mục cha (for admin table)
    private String name;
    private String url;
    private String description;
    private Boolean isActive;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer createdBy;
    private Integer updatedBy;
    
    // Số lượng products thuộc category này (optional)
    private Integer productCount;
    
    // Số lượng subcategories (optional)
    private Integer childCount;
}
