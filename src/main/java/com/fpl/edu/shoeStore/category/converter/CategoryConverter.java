package com.fpl.edu.shoeStore.category.converter;

import com.fpl.edu.shoeStore.category.dto.request.CategoryDtoRequest;
import com.fpl.edu.shoeStore.category.dto.response.CategoryDtoResponse;
import com.fpl.edu.shoeStore.category.entity.Category;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class CategoryConverter {

    /**
     * Convert Request DTO → Entity (for INSERT)
     */
    public static Category toEntity(CategoryDtoRequest dto) {
        return Category.builder()
                .parentId(dto.getParentId())
                .name(dto.getName())
                .url(dto.getUrl())
                .description(dto.getDescription())
                .isActive(dto.getIsActive() != null ? dto.getIsActive() : true) // Default true
                .sortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0) // Default 0
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy(dto.getCreatedBy())
                .updatedBy(dto.getUpdatedBy())
                .build();
    }

    /**
     * Convert Entity → Response DTO
     */
    public static CategoryDtoResponse toResponse(Category entity) {
        if (entity == null) return null;
        
        return CategoryDtoResponse.builder()
                .categoryId(entity.getCategoryId())
                .parentId(entity.getParentId())
                .parentName(null) // Set by mapper JOIN query
                .name(entity.getName())
                .url(entity.getUrl())
                .description(entity.getDescription())
                .isActive(entity.getIsActive())
                .sortOrder(entity.getSortOrder())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    /**
     * Convert List<Entity> → List<Response>
     */
    public static List<CategoryDtoResponse> toResponseList(List<Category> entities) {
        if (entities == null) return List.of();
        
        return entities.stream()
                .map(CategoryConverter::toResponse)
                .collect(Collectors.toList());
    }
}
