package com.fpl.edu.shoeStore.category.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDtoRequest {
    
    private Integer parentId;  // Nullable - root categories have null parent
    
    @NotBlank(message = "Tên danh mục không được để trống")
    @Size(min = 2, max = 100, message = "Tên danh mục phải từ 2-100 ký tự")
    private String name;
    
    @Size(max = 120, message = "URL không được vượt quá 120 ký tự")
    private String url;
    
    private String description;
    
    private Boolean isActive;  // Default true in service if null
    
    private Integer sortOrder; // Default 0 in service if null
    
    private Integer createdBy; // For audit
    private Integer updatedBy; // For audit
}
